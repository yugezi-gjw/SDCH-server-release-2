package com.varian.oiscn.base.practitioner;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PractitionerAntiCorruptionServiceImp;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 2/8/2017.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PractitionerResource extends AbstractResource {

	private PractitionerAntiCorruptionServiceImp practitionerAntiCorruptionServiceImp;

	public PractitionerResource(Configuration configuration, Environment environment) {
		super(configuration, environment);
		practitionerAntiCorruptionServiceImp = new PractitionerAntiCorruptionServiceImp();
	}

	@GET
	@Path("/practitioners/searchByGroupId")
	public Response queryPhysiciansByGroupId(@Auth UserContext userContext, @QueryParam("groupId") String groupId) {
		// TODO: add error message when there is no corresponding group.
		if (StringUtils.isEmpty(groupId)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Group Id").build();
		}
		List<PractitionerTreeNode> practitionerTreeNodeList = GroupPractitionerHelper.getAllPractitionersOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),groupId);

		if(practitionerTreeNodeList == null){
			// TODO: add error message when there is no corresponding group.
			return Response.status(Response.Status.BAD_REQUEST).entity("Group Id does not exist.").build();
		} else{
			List<PractitionerDto> practitionerDtoList = new ArrayList<>();
			practitionerTreeNodeList.forEach(practitionerTreeNode -> practitionerDtoList.add(new PractitionerDto(practitionerTreeNode.getId(), practitionerTreeNode.getName(),practitionerTreeNode.getParticipantType())));
			return Response.ok(practitionerDtoList).build();
		}
	}

	@GET
	@Path("/practitioners/searchRegisterGroup")
	public Response queryRegisterGroupByPractitionerId(@Auth UserContext userContext, @QueryParam("practitionerId") String practitionerId) {
		if (StringUtils.isEmpty(practitionerId)) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Bad Practitioner ID").build();
		}
		GroupTreeNode groupTreeNode = GroupPractitionerHelper.getRegisterGroupByPractitionerId(GroupPractitionerHelper.getOncologyGroupTreeNode(),practitionerId);

		if(groupTreeNode == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Cannot find a group ID").build();
		} else{
			int oncologistPrefix = groupTreeNode.getOriginalName().indexOf('_');
			//如果有下划线存在，则有第二级分组
			if(oncologistPrefix != -1){
				return Response.ok(new GroupDto(groupTreeNode.getId(), groupTreeNode.getName())).build();
			} else {
				return Response.ok(new GroupDto(groupTreeNode.getId(), I18nReader.getLocaleValueByKey("Oncologist.DisplayName.NoSecondaryGroup"))).build();
			}
		}
	}

	@GET
	@Path("/practitioners/loginUserRedirectedToFirstActivity")
	public Response checkIfLoginUserCanGoToFirstActivity(@Auth UserContext userContext, @QueryParam("primaryPhysicianId") String primaryPhysicianId,
													   @QueryParam("patientSer") Long patientSer) {
		CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
		CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(String.valueOf(patientSer));
		List<ActivityInstance> activityInstanceList = carePathInstance.getActivityInstances();
		if(activityInstanceList == null || activityInstanceList.isEmpty()){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fail to retrieve the activity list of this patient.").build();
		}
		ActivityInstance firstActivityInstance = null;
		for(ActivityInstance activityInstance : activityInstanceList){
			if(activityInstance.getInstanceID() != null){
				firstActivityInstance = activityInstance;
				break;
			}
		}
		if(firstActivityInstance == null){
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fail to retrieve the first activity.").build();
		}
		boolean isGroupBased = false;
		List<String> supportCarePathTemplateNameList = new ArrayList<>();
		String defaultTemplateName = configuration.getDefaultCarePathTemplateName();
		supportCarePathTemplateNameList.add(defaultTemplateName);
		List<CarePathConfigItem> carePathConfigItems = this.configuration.getCarePathConfig().getCarePath();
		if(carePathConfigItems != null){
			carePathConfigItems.forEach(carePathConfigItem -> supportCarePathTemplateNameList.add(carePathConfigItem.getTemplateId()));
		}

		for(int i = 0; i < supportCarePathTemplateNameList.size() && !isGroupBased; i++){
			String templateName = supportCarePathTemplateNameList.get(i);
			CarePathAntiCorruptionServiceImp antiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
			CarePathTemplate template = antiCorruptionServiceImp.queryCarePathByTemplateName(templateName);

			if(template == null){
				log.error("Fail to find the carepath template: " + templateName);
			} else {
				for (PlannedActivity plannedActivity : template.getActivities()) {
					if (plannedActivity.getActivityCode().equals(firstActivityInstance.getActivityCode())) {
						isGroupBased = plannedActivity.getAutoAssignPrimaryOncologist();
						break;
					}
				}
			}
		}
		boolean redirectedToFirstActivity = false;
		if(isGroupBased){
			GroupTreeNode groupTreeNode = GroupPractitionerHelper.getTopmostGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),userContext.getLogin().getResourceSer().toString(),ParticipantTypeEnum.PRACTITIONER);
			if(groupTreeNode == null){
				//当前用户如果没有医生最大组则不在医生分组中应该算false
				FirstActivityVO firstActivityVO = new FirstActivityVO(redirectedToFirstActivity, null, null, null, null, null);
				return Response.status(Response.Status.OK).entity(firstActivityVO).build();
			}
			List<PractitionerTreeNode> practitionerTreeNodeList = GroupPractitionerHelper.getAllPractitionersOfAGroup(GroupPractitionerHelper.getOncologyGroupTreeNode(),groupTreeNode.getId());
			for(PractitionerTreeNode practitionerTreeNode : practitionerTreeNodeList){
				if(practitionerTreeNode.getId().equals(primaryPhysicianId)){
					redirectedToFirstActivity = true;
					break;
				}
			}
		} else{
			if(userContext.getLogin().getStaffGroups().contains(firstActivityInstance.getDefaultGroupID())){
				redirectedToFirstActivity = true;
			}
		}
		FirstActivityVO firstActivityVO = new FirstActivityVO();
		firstActivityVO.setRedirectedToFirstActivity(redirectedToFirstActivity);
		if(redirectedToFirstActivity){
			firstActivityVO.setPatientSer(String.valueOf(patientSer));
			firstActivityVO.setActivityId(firstActivityInstance.getId());
			firstActivityVO.setActivityInstanceId(firstActivityInstance.getInstanceID());
			firstActivityVO.setActivityType(firstActivityInstance.getActivityType().name());
			firstActivityVO.setActivityCode(firstActivityInstance.getActivityCode());
		}
		return Response.status(Response.Status.OK).entity(firstActivityVO).build();
	}
}
