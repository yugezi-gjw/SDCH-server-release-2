package com.varian.oiscn.patient.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.base.assembler.RegistrationVOAssembler;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.base.dynamicform.DynamicFormTemplateServiceImp;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.user.PermissionService;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.PhotoUtil;
import com.varian.oiscn.base.util.PinyinUtil;
import com.varian.oiscn.base.vid.VIDGeneratorServiceImp;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConstants;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathServiceImpl;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.dynamicform.DynamicFormRecord;
import com.varian.oiscn.encounter.service.DynamicFormRecordServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.patient.assembler.CoverageAssembler;
import com.varian.oiscn.patient.service.PatientServiceImp;
import com.varian.oiscn.patient.view.*;
import com.varian.oiscn.patient.workflowprogress.PatientWorkflowProgressHelper;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Created by gbt1220 on 1/3/2017.
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientResource extends AbstractResource {
    public static final String GBK = "GBK";
    private static final String WORKFLOW_REGISTRED_NAME = I18nReader.getLocaleValueByKey("PatientResource.workflowRegistredName");
    private PatientAntiCorruptionServiceImp antiCorruptionServiceImp;
    private PatientCacheService patientCacheService;
    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private DynamicFormTemplateServiceImp dynamicFormTemplateServiceImp;
    private DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp;

    public PatientResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        patientCacheService = new PatientCacheService();
        carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
        dynamicFormTemplateServiceImp = new DynamicFormTemplateServiceImp();
    }



    @GET
    @Path("/patient/search")
    public Response searchByHIsId(@Auth UserContext userContext,
                           @QueryParam("hisId") String hisId){
        if (isBlank(hisId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RegistrationVO()).build();
        }
        PatientDto fhirPatientDto = null;
        if (!isBlank(hisId)) {
            fhirPatientDto = antiCorruptionServiceImp.queryPatientWithPhotoByHisId(hisId);
        }
        if(fhirPatientDto == null){
            return Response.status(Response.Status.OK).entity(new PatientDto()).build();
        }
        RegistrationVO registrationVO = buildRegistrationVO(userContext,fhirPatientDto);
        return Response.ok(registrationVO).build();
    }

    @GET
    @Path("/patient/{patientSer}")
    public Response searchByPatientSer(@Auth UserContext userContext,
                           @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new RegistrationVO()).build();
        }
        PatientDto fhirPatientDto = patientCacheService.queryPatientByPatientIdWithPhoto(patientSer.toString());
        if (fhirPatientDto == null) {
            return Response.status(Response.Status.OK).entity(new PatientDto()).build();
        }
        RegistrationVO registrationVO = buildRegistrationVO(userContext,fhirPatientDto);
        return Response.ok(registrationVO).build();
    }

    private RegistrationVO buildRegistrationVO(UserContext userContext,PatientDto fhirPatientDto){
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;
        RegistrationVO registrationVO = RegistrationVOAssembler.getRegistrationVO(fhirPatientDto);
        // 处理Aria得到的照片信息 byte -> base64.
        byte[] photoByte = registrationVO.getPhotoByte();
        if (photoByte != null && photoByte.length > 0) {
            // 有照片信息
            final String photo = PhotoUtil.encode(photoByte);
            registrationVO.setPhoto(photo);
            registrationVO.setPhotoByte(null);
        }
        PatientServiceImp patientServiceImp = new PatientServiceImp(userContext);
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(Long.valueOf(fhirPatientDto.getPatientSer()));

        //encounter为null代表是已注册结束治疗的用户，那么需要置空以下字段。
        if (encounter == null) {
            registrationVO.setPhysicianId(null);
            registrationVO.setPhysicianName(null);
            registrationVO.setPhysicianPhone(null);
            registrationVO.setPatientSource(null);
            registrationVO.setInsuranceType(null);
        }
        if (encounter != null) {
            registrationVO.setPhysicianGroupId(encounter.getPrimaryPhysicianGroupID());
            registrationVO.setPhysicianGroupName(encounter.getPrimaryPhysicianGroupName());
            registrationVO.setPhysicianBId(encounter.getPhysicianBId());
            registrationVO.setPhysicianBName(encounter.getPhysicianBName());
            registrationVO.setPhysicianCId(encounter.getPhysicianCId());
            registrationVO.setPhysicianCName(encounter.getPhysicianCName());
        }
//      get patientHistory from fhir first,if not,then get it from localDB,if not ,then get from HIS
        if (StringUtils.isEmpty(registrationVO.getPatientHistory())) {
            Patient p = patientServiceImp.queryPatientByPatientSer(fhirPatientDto.getPatientSer());
            if (p != null && StringUtils.isNotEmpty(p.getPatientHistory())) {
                registrationVO.setPatientHistory(p.getPatientHistory());
            } else {
//               Query patient history from Hospital Information System
                registrationVO.setPatientHistory(patientServiceImp.queryPatientHistoryFromHIS(fhirPatientDto.getHisId()));
            }
        }

        if (encounter != null) {
            registrationVO.setPhysicianComment(encounter.getPhysicianComment());
            registrationVO.setAllergyInfo(encounter.getAllergyInfo());
        }
        //get ECOG Score, ECOG Description, Positive sign, Insurance Type and Patient Source
        if (encounter != null) {

            // ECOG, positive sign is retrieved from local DB.
            registrationVO.setEcogScore(encounter.getEcogScore());
            registrationVO.setEcogDesc(encounter.getEcogDesc());
            registrationVO.setPositiveSign(encounter.getPositiveSign());

            registrationVO.setInsuranceTypeCode(encounter.getInsuranceTypeCode());
            registrationVO.setInsuranceType(encounter.getInsuranceType());

            registrationVO.setPatientSource(encounter.getPatientSource());

            registrationVO.setAge(encounter.getAge());

            // alert information should from Aria. PS: when a patient is completed treatment, alert already cleared.
            // note: Only patient has encounter, from Aria.
            assembleAlert2RegistrationVO(registrationVO, fhirPatientDto.getLabels(), configuration);

        }

        if (encounter != null) {
            List<Diagnosis> diagnosesInEncounter = encounter.getDiagnoses();
            if (diagnosesInEncounter != null && !diagnosesInEncounter.isEmpty()) {
                Diagnosis diagnosisEncounter = diagnosesInEncounter.get(0);
                RegistrationVOAssembler.assemblerDiagnosisData2RegistrationVO(registrationVO, diagnosisEncounter);
            }
        }

        //encounter为null代表是已注册结束治疗的用户，所以不需要回提示(是否紧急)
        if (encounter != null) {
            flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
            boolean urgentFlag = flagAntiCorruptionServiceImp.checkPatientStatusIcon(fhirPatientDto.getPatientSer(), StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
            registrationVO.setUrgent(urgentFlag);
        }

        if (fhirPatientDto != null) {
            flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
            boolean activeFlag = flagAntiCorruptionServiceImp.checkPatientStatusIcon(fhirPatientDto.getPatientSer(), StatusIconPool.get(configuration.getActiveStatusIconDesc()));
            registrationVO.setActiveFlag(activeFlag);
        } else {
            registrationVO.setActiveFlag(false);
        }
        return registrationVO;
    }

    @GET
    @Path("/patient/photo/{patientSerList}")
    public Response getPhoto(@Auth UserContext userContext, @PathParam("patientSerList") String patientSerString) {
        if (isBlank(patientSerString)) {
            Map<String, String> resp = new HashMap<>(1);
            resp.put("errMsg", "No patientSer String in the request!");
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        List<Long> patientSerList = new ArrayList<>();
        for (String patientSerStr : patientSerString.split(",")) {

            Long patientSer = null;
            if(StringUtils.isNumeric(StringUtils.trimToEmpty(patientSerStr))){
                patientSer = new Long(StringUtils.trimToEmpty(patientSerStr));
            }
            if (patientSer == null || patientSer < 0) {
                // bad patientSer String
                String err = "Bad patientSer: [" + patientSerString + "]";
                log.warn(err);
                return build400Response(err);
            }
            patientSerList.add(patientSer);
        }

        // from local storage.
        PatientServiceImp patientService = new PatientServiceImp(userContext);

        Map<Long, byte[]> photoByteList = patientService.getPhotoListByPatientSerList(patientSerList);
        // Base64 coding photo data
        List<Map<String, String>> photoList = new ArrayList<>(photoByteList.size());
        Map<String, String> mapPhoto; // each photo would be a element with id / photo fields.
        Set<Entry<Long, byte[]>> entrySet = photoByteList.entrySet();
        for (Entry<Long, byte[]> entry : entrySet) {
            mapPhoto = new HashMap<>(2);
            mapPhoto.put("id", entry.getKey().toString());
            mapPhoto.put("photo", PhotoUtil.encode(entry.getValue()));
            photoList.add(mapPhoto);
        }
        return Response.ok(photoList).build();
    }

    @PUT
    @Path("/patient/photo/{patientSer}")
    public Response updatePhoto(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer, RegistrationVO registrationVO) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }

        Map<String, String> resp = new HashMap<>(1);
        byte[] photoByte = new byte[0];
        if (isBlank(registrationVO.getPhoto())) {
            // clear the photo
            photoByte = null;
        } else {
            // decode the avatar string to binary.
            photoByte = Base64.decodeBase64(registrationVO.getPhoto());
        }

        if (photoByte == null || photoByte.length == 0) {
            // decode error.
            String err = "Bad Patient Photo Data Found!";
            log.warn(err);
            return build400Response(err);
        }
        //Store to FHIR.
        PatientDto patientDto = patientCacheService.queryPatientByPatientId(patientSer.toString());
        patientDto.setPhoto(photoByte);
        String returnPatientSer = antiCorruptionServiceImp.update(patientSer, patientDto);
        if (StringUtils.isEmpty(returnPatientSer)) {
            String errMsg = "Fail to update the photo in Aria!";
            resp.put("errMsg", errMsg);
            log.error(errMsg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resp).build();
        }

        // from local storage.
        PatientServiceImp patientService = new PatientServiceImp(userContext);
        int affectedRow = patientService.updatePhotoByPatientSer(patientSer, photoByte);
        if (affectedRow == 0) {
            resp.put("errMsg", "No Patient Photo Data is updated in Qin with PatientSer " + patientSer);
        }
        return Response.status(Response.Status.ACCEPTED).entity(resp).build();
    }

    private void assembleAlert2PatientDto(PatientDto patientDto, String alert, Configuration configuration) {
        String alertCode = PatientLabelPool.get(configuration.getAlertPatientLabelDesc());
        if (isEmpty(alertCode)) {
            log.error("Can't get the alert code, please check the alert config.");
            return;
        }
        PatientDto.PatientLabel alertLabel = new PatientDto.PatientLabel();
        alertLabel.setLabelId(alertCode);
        alertLabel.setLabelTag(configuration.getAlertPatientLabelDesc());
        alertLabel.setLabelText(StringUtils.defaultString(alert, ""));
        patientDto.addPatientLabel(alertLabel);
    }

    private void assembleAlert2RegistrationVO(RegistrationVO registrationVO, List<PatientDto.PatientLabel> labels, Configuration configuration) {
        if (labels == null) {
            return;
        }
        String alertCode = PatientLabelPool.get(configuration.getAlertPatientLabelDesc());
        if (isEmpty(alertCode)) {
            log.error("Can't get the alert code, please check the alert config.");
            return;
        }
        Optional<PatientDto.PatientLabel> optional = labels.stream().filter(patientLabel -> StringUtils.equals(patientLabel.getLabelId(), alertCode)).findAny();
        if (optional.isPresent()) {
            PatientDto.PatientLabel alertLabel = optional.get();
            registrationVO.setWarningText(alertLabel.getLabelText());
        }
    }

    /**
     * Update the patient's information.<br>
     *
     * @param userContext
     * @param registrationVO
     * @return update result
     */
    @PUT
    @Path("/patient")
    public Response update(@Auth UserContext userContext, RegistrationVO registrationVO) {
        if (!verifyMandatoryDataAndLength(registrationVO)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(registrationVO).build();
        }

        if (!isBlank(registrationVO.getPhoto())) {
            // decode the avatar string to binary.
            byte[] photoByte = PhotoUtil.decode(registrationVO.getPhoto());
            if (photoByte == null || photoByte.length == 0) {
                // decode error.
                Map<String, String> response = new HashMap<>(1);
                response.put("errMsg", "Updating Patient information with Bad Avatar Data!");
                return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
            }
            registrationVO.setPhotoByte(photoByte);
        }
        registrationVO.setChineseName(StringUtils.trimToEmpty(registrationVO.getChineseName()));
        registrationVO.setPinyin(PinyinUtil.chineseName2PinyinAcronyms(registrationVO.getChineseName()));

        PatientServiceImp patientServiceImp = new PatientServiceImp(userContext);
        Patient p = patientServiceImp.queryPatientByPatientSer(registrationVO.getPatientSer());
        if (StringUtils.isEmpty(registrationVO.getPatientHistory())) {
//      Get patient history from fhir first.If not get it,then get patient history from localDB,if
//      not get it again,then get patient history from HIS
//      Get patientDto from fhir
            PatientDto tmpPatientDto = patientCacheService.queryPatientByPatientId(registrationVO.getPatientSer());
            if (tmpPatientDto == null || StringUtils.isEmpty(tmpPatientDto.getPatientHistory())) {
//          Get patient history from localDB
                if (p != null && StringUtils.isEmpty(p.getPatientHistory())) {
//                Get patient history from HIS
                    registrationVO.setPatientHistory(patientServiceImp.queryPatientHistoryFromHIS(registrationVO.getHisId()));
                }
            }
        }
        PatientDto patientDto = RegistrationVOAssembler.getPatientDto(registrationVO);
        assembleAlert2PatientDto(patientDto, registrationVO.getWarningText(), configuration);

        String patientSer = antiCorruptionServiceImp.updatePatient(patientDto);
        if (StringUtils.isEmpty(patientSer)) {
            return Response.status(Response.Status.NOT_MODIFIED).entity(registrationVO).build();
        }
        registrationVO.setPatientSer(patientSer);

        //TODO: Diagnosis code can't be changed in ARIA

        //update urgent
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        boolean isUrgentInDb = flagAntiCorruptionServiceImp.checkPatientStatusIcon(registrationVO.getPatientSer(),
                StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        if (isUrgentInDb && !registrationVO.isUrgent()) {
            flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(registrationVO.getPatientSer(),
                    StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        } else if (!isUrgentInDb && registrationVO.isUrgent()) {
            flagAntiCorruptionServiceImp.markPatientStatusIcon(registrationVO.getPatientSer(),
                    StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        }

        boolean isActive = flagAntiCorruptionServiceImp.checkPatientStatusIcon(registrationVO.getPatientSer(),
                StatusIconPool.get(configuration.getActiveStatusIconDesc()));

        if (!isActive) {
            //update active patient status
            flagAntiCorruptionServiceImp.markPatientStatusIcon(registrationVO.getPatientSer(),
                    StatusIconPool.get(configuration.getActiveStatusIconDesc()));
            //assign new carepath
            String defaultDepartmentId = SystemConfigPool.queryDefaultDepartment();
            if (isEmpty(registrationVO.getCarePathTemplateId())) {
                CarePathTemplate defaultTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(configuration.getDefaultCarePathTemplateName());
                registrationVO.setCarePathTemplateId(defaultTemplate.getId());
            }
            boolean assign = carePathAntiCorruptionServiceImp.assignCarePath(defaultDepartmentId, registrationVO.getCarePathTemplateId(), patientSer);
            if (assign) {//重新Assign CarePath
                CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(patientSer);
                if (carePathInstance != null) {
                    registrationVO.setCarePathInstanceId(carePathInstance.getId());
                }
            /*
            如果本地Patient表中没有记录，说明是ARIA里的旧数据，需要在本地创建patient和encounter；
            如果本地Patient表中存在记录，说明是Qin里停止治疗的患者，则只需要创建encounter即可
             */
                if (p != null) {
                    //add new encounter in local db
                    patientServiceImp.updateWithNewEncounter(registrationVO);
                } else {
                    patientServiceImp.create(registrationVO);
                }
                PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer);
            }
        } else {
            //update local db
            patientServiceImp.update(registrationVO);
        }

        //Update insurance type
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
        coverageAntiCorruptionServiceImp.updateCoverage(CoverageAssembler.getCoverageDto(patientSer, registrationVO.getInsuranceTypeCode()));

        registrationVO.setPatientSer(patientSer);
        return Response.status(Response.Status.ACCEPTED).entity(registrationVO).build();
    }

    private boolean verifyMandatoryDataAndLength(RegistrationVO registrationVO) {
        if (isBlank(registrationVO.getChineseName()) ||
                isBlank(registrationVO.getHisId()) ||
                isBlank(registrationVO.getPhysicianGroupId()) ||
                isBlank(registrationVO.getPhysicianId())) {
            return false;
        }
        try {
            if (verifyRegistrationHisIdAndNameLength(registrationVO) || verifyRegistrationOthersLength(registrationVO)) {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException: {}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * if registration's hisID's length >25 or registration's chineseName's length >64,
     * return true;
     *
     * @param registrationVO
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean verifyRegistrationHisIdAndNameLength(RegistrationVO registrationVO) throws UnsupportedEncodingException {
        return strLengthGreatThan(registrationVO.getHisId(), 25, GBK) || strLengthGreatThan(registrationVO.getChineseName(), 64, GBK);
    }

    /**
     * verify NationalId,ContactPerson,ContactPhone for registration
     *
     * @param registrationVO
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean verifyRegistrationOthersLength(RegistrationVO registrationVO) throws UnsupportedEncodingException {
        return strLengthGreatThan(registrationVO.getNationalId(), 25, GBK)
                || strLengthGreatThan(registrationVO.getContactPerson(), 64, GBK)
                || strLengthGreatThan(registrationVO.getContactPhone(), 64, GBK);
    }

    /**
     * verify str's bytes length weather grete than  greateThan
     *
     * @param str
     * @param greateThan
     * @param encoding
     * @return
     * @throws UnsupportedEncodingException
     */
    private boolean strLengthGreatThan(String str, int greateThan, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(str) && str.getBytes(encoding).length > greateThan;
    }

    @Path("/patient/history/{patientSer}")
    @POST
    public Response modifyPatientHistory(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer, RegistrationVO registrationVO) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        boolean result = new PatientServiceImp(userContext).updatePatientHistory(patientSer, registrationVO.getPatientHistory());
        if (result) {
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }

    /**
     * get patient workflow progress
     *
     * @param userContext
     * @param patientSer
     * @return
     */
    @Path("/patient/{patientSer}/workflowProgress")
    @GET
    public Response patientWorkflowProgress(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryLastCarePathByPatientID(patientSer.toString());
        // return all carepath instance, including the already stopped carepath.
        PatientWorkflowProgressHelper helper = new PatientWorkflowProgressHelper(carePathInstance, configuration.getKeyActivityType(), configuration.getKeyActivityTypeValue());
        List<WorkflowProgressNode> list = helper.getKeyActivityWorkflowOfPatient();
        String firstNodeName = StringUtils.EMPTY;
        // Query encounter carepath count.
        EncounterCarePathServiceImpl ecpService = new EncounterCarePathServiceImpl(userContext);
        int encounterCarePathNo = ecpService.countCarePathByPatientSer(new Long(patientSer));
        if (encounterCarePathNo > 1) {
            // firstNodeName = "第" + encounterCarePathNo + "回治疗";
            firstNodeName = StringUtils.EMPTY;
        } else {
            firstNodeName = WORKFLOW_REGISTRED_NAME;
        }
        list.add(0, new WorkflowProgressNode(firstNodeName, true, WorkflowProgressNodeStatus.COMPLETED));

        PatientWorkflowProgressVO patientWorkflowProgressVO = new PatientWorkflowProgressVO();
        patientWorkflowProgressVO.setActivityNodes(list);
//      处理关键节点之间小圈的状态 开始。
//      小圈之后的关键节点是进行中或已完成，则小圈的状态是已完成，否则是未开始状态
        List<WorkflowProgressNode> notCompletedNodeList = new ArrayList<>();
//      首先查找list中状态是“未开始”或者“进行中”的节点
        for (WorkflowProgressNode node : list) {
            if (!node.getStatus().equals(WorkflowProgressNodeStatus.COMPLETED)) {
                notCompletedNodeList.add(node);
            }
        }
//      如果有这样的节点，首先处理关键节点状态是“正在进行”的情况
        notCompletedNodeList.forEach(notCompletedNode -> {
            if (notCompletedNode.getIsKeyActivity() && notCompletedNode.getStatus().equals(WorkflowProgressNodeStatus.IN_PROGRESS)) {
//              将当前索引指向的节点到该节点之前的关键节点之间的所有节点状态置为“已完成”
                setNodeStatusBetweenLeftKeyActivityNodeAndRightNode(list, notCompletedNode, WorkflowProgressNodeStatus.COMPLETED);
            }
        });

//      如果有这样的节点，其次处理关键节点是“未开始”的情况
        notCompletedNodeList.forEach(notCompletedNode -> {
            if (notCompletedNode.getIsKeyActivity() && notCompletedNode.getStatus().equals(WorkflowProgressNodeStatus.NOT_STARTED)) {
//              将当前索引指向的节点到该节点之前的关键节点之间的所有节点状态置为“未开始”
                setNodeStatusBetweenLeftKeyActivityNodeAndRightNode(list, notCompletedNode, WorkflowProgressNodeStatus.NOT_STARTED);
            }
        });
//      如果有这样的节点，最后处理非关键节点。如果之前处理了关键节点，则非关键节点应该已经处理了
        notCompletedNodeList.forEach(notCompletedNode -> {
            if (!notCompletedNode.getIsKeyActivity() && notCompletedNode.getStatus().equals(WorkflowProgressNodeStatus.IN_PROGRESS)) {
//              将当前索引指向的节点到该节点之前的关键节点之间的所有节点状态置为“未开始”
                setNodeStatusBetweenLeftKeyActivityNodeAndRightNode(list, notCompletedNode, WorkflowProgressNodeStatus.NOT_STARTED);
            }
        });
        WorkflowProgressNode treatmentNode = list.remove(list.size() - 1);
        WorkflowProgressTreatmentNode workflowProgressTreatmentNode = new WorkflowProgressTreatmentNode(0, 0);
        workflowProgressTreatmentNode.setActivityName(treatmentNode.getActivityName());
        workflowProgressTreatmentNode.setIsKeyActivity(treatmentNode.getIsKeyActivity());
        workflowProgressTreatmentNode.setStatus(treatmentNode.getStatus());

        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(patientSer.toString());
        if (treatmentSummaryDtoOptional.isPresent()) {
            TreatmentSummaryDto treatmentSummaryDto = treatmentSummaryDtoOptional.get();
            List<PlanSummaryDto> planList = treatmentSummaryDto.getPlans();
            if (planList != null && !planList.isEmpty()) {
                workflowProgressTreatmentNode.setTotalTreatmentCount(planList.get(0).getPlannedFractions());
                workflowProgressTreatmentNode.setTreatedCount(planList.get(0).getDeliveredFractions());
                if (WorkflowProgressNodeStatus.COMPLETED.equals(workflowProgressTreatmentNode.getStatus())) {
                    if (workflowProgressTreatmentNode.getTreatedCount() < workflowProgressTreatmentNode.getTotalTreatmentCount()) {
                        workflowProgressTreatmentNode.setStatus(WorkflowProgressNodeStatus.IN_PROGRESS);
                    }
                }
            }
        }
        patientWorkflowProgressVO.setTreatmentActivityNodes(Arrays.asList(workflowProgressTreatmentNode));
        return Response.ok(patientWorkflowProgressVO).build();
    }

    private void setNodeStatusBetweenLeftKeyActivityNodeAndRightNode(List<WorkflowProgressNode> list, WorkflowProgressNode rightActivityNode, WorkflowProgressNodeStatus normalProgressNodeStatus) {
        int idx = list.indexOf(rightActivityNode);
        if (rightActivityNode.getIsKeyActivity()) {
            idx--;
        }
        for (; idx >= 0; idx--) {
            if (list.get(idx).getIsKeyActivity()) {
                break;
            }
            list.get(idx).setStatus(normalProgressNodeStatus);
        }
    }

    @Path("/patient/dynamicforms/{patientSer}")
    @GET
    public Response queryPatientDynamicForms(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        if (encounter == null) {
            log.warn("No Active Encounter for PatientSer [{}]", patientSer);
            return Response.ok(new ArrayList<>()).build();
        }
        String encounterId = encounter.getId();
        List<PatientDynamicFormVO> list = listDynamicForm(userContext, patientSer, new Long(encounterId));
        return Response.ok(list).build();
    }

    @Path("/patient/history/dynamicform/list")
    @GET
    public Response listDynamicFormHistory(@Auth UserContext userContext,
                                           @QueryParam("patientSer") Long patientSer,
                                           @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        List<PatientDynamicFormVO> list = listDynamicForm(userContext, patientSer, encounterId);
        return Response.ok(list).build();
    }

    protected List<PatientDynamicFormVO> listDynamicForm(UserContext userContext, Long patientSer, Long encounterId) {
        List<PatientDynamicFormVO> list = new ArrayList<>();
        DynamicFormRecordServiceImp dynamicFormRecordServiceImp = new DynamicFormRecordServiceImp(userContext);
        List<DynamicFormRecord> dynamicFormRecordList = dynamicFormRecordServiceImp.queryDynamicFormRecordInfoByEncounterId(patientSer, encounterId);
        for (DynamicFormRecord dynamicFormRecord : dynamicFormRecordList) {
            PatientDynamicFormVO patientDynamicFormVO = new PatientDynamicFormVO();
            String templateId = dynamicFormRecord.getTemplateId();
            if (StringUtils.isNotBlank(templateId)) {
                List<KeyValuePair> nameList = dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(Arrays.asList(templateId));
                if (nameList == null || nameList.isEmpty()) {
                    log.error("Dynamic Form Template [{}] is not found, please check DynamicFormTemplate Table!!!", templateId);
                    continue;
                }
                // template display name
                patientDynamicFormVO.setSelectedTemplateHeader(nameList.get(0).getValue());
            }
            patientDynamicFormVO.setId(dynamicFormRecord.getId());
            patientDynamicFormVO.setSelectedTemplateId(dynamicFormRecord.getTemplateId());
            patientDynamicFormVO.setCarePathInstanceId(dynamicFormRecord.getCarePathInstanceId());
            patientDynamicFormVO.setCompletedDt(DateUtil.formatDate(dynamicFormRecord.getCreateDate(), DateUtil.DATE_TIME_FORMAT));
            list.add(patientDynamicFormVO);
        }
        return list;
    }

    @Path("/patient/dynamicform/detail")
    @GET
    public Response queryDynamicFormDetail(@Auth UserContext userContext, @QueryParam("patientSer") Long patientSer,
                                           @QueryParam("dynamicformId") String dynamicformId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (StringUtils.isEmpty(dynamicformId)) {
            build400Response("dynamicformId cannot be empty.");
        }
        DynamicFormRecordServiceImp dynamicFormRecordServiceImp = new DynamicFormRecordServiceImp(userContext);
        return Response.ok(dynamicFormRecordServiceImp.queryDynamicFormRecordInfoById(dynamicformId)).build();
    }

    @Path("/patient/queryExistingDynamicForm/{patientSer}")
    @GET
    public Response queryExistingDynamicForm(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer,
                                             @QueryParam("id") String id) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (StringUtils.isEmpty(id)) {
            build400Response("id cannot be empty.");
        }
        DynamicFormRecordServiceImp dynamicFormRecordServiceImp = new DynamicFormRecordServiceImp(userContext);
        return Response.ok(dynamicFormRecordServiceImp.queryDynamicFormRecordInfoById(id)).build();
    }

    @Path("/patient/dynamicformdevice/{patientSer}")
    @GET
    public Response searchDeviceFromDynamicForm(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        this.dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(userContext);
        String deviceCode = this.dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(patientSer.toString(), "jiasuqi");
        DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
        DeviceDto deviceDto;
        if (StringUtils.isNotEmpty(deviceCode)) {
            deviceDto = deviceAntiCorruptionServiceImp.queryDeviceByCode(deviceCode);
        } else {
            deviceDto = new DeviceDto();
        }
        return Response.ok(deviceDto).build();
    }

    /**
     * Get physician comments in batch according to patientSerMapList
     *
     * @param userContext
     * @param patientSerMapList
     * @return
     */
    @Path("/patient/physiciancomment")
    @POST
    public Response getPhysicianCommentsInBatch(@Auth UserContext userContext, final List<KeyValuePair> patientSerMapList) {
        if (patientSerMapList == null || patientSerMapList.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("Empty Patient Id List.").build();
        }

        List<String> patientSerList = new ArrayList<>();
        patientSerMapList.forEach(keyValuePair -> patientSerList.add(keyValuePair.getValue()));

        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Map<String, String> result = encounterServiceImp.getPhysicianCommentsInBatch(patientSerList);
        for (Map.Entry<String, String> entry : result.entrySet()) {
            if (isEmpty(entry.getValue())) {

                //TODO Add the logic of fetching data from FHIR.
                PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();

            }
        }
        return Response.ok(result).build();
    }

    @Path("/patient/getvid")
    @GET
    public Response searchVID(@Auth UserContext userContext) {
        VIDGeneratorServiceImp serviceImp = new VIDGeneratorServiceImp();
        String vid = serviceImp.generateVID();
        return Response.ok(vid).build();
    }

    protected boolean validatePatientSer(String patientSer) {
        return !isBlank(patientSer);
    }

    /**
     * Check if VID already exists in Aria.<br>
     *
     * @param userContext
     * @param vid
     * @return VidExists, true - VID existed<br>
     * VidExists, false - VID Not existed or VID for himself.
     */
    @Path("/patient/check-vid/{vid}")
    @GET
    public Response checkVidExists(@Auth UserContext userContext, @PathParam("vid") String vid, @QueryParam("patientSer") Long patientSer) {
        Boolean isExisted = isVidExisted(vid, patientSer);
        KeyValuePair ret = new KeyValuePair("VidExists", isExisted.toString());
        return Response.ok(ret).build();
    }

    protected Boolean isVidExisted(String vid, Long patientSer) {
        boolean ret = false;
        List<PatientDto> dtoList = antiCorruptionServiceImp.queryByAriaId(vid);
        if (dtoList != null && dtoList.size() > 0) {
            for (PatientDto dto : dtoList) {
                if (!dto.getPatientSer().equals(String.valueOf(patientSer))) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }

    @Path("/patient/carepath/{instanceId}")
    @GET
    public Response checkVidExists(@Auth UserContext userContext, @PathParam("instanceId") String instanceId) {
        CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryCarePathInstanceByInstanceId(instanceId);
        return Response.ok(carePathInstance).build();
    }

    @Path("/patient/searchAuthority")
    @GET
    public Response getPatientAuthorityTable(@Auth UserContext userContext){
        List<GroupTreeNode> resultList = new ArrayList<>();
        GroupTreeNode patientAuthTree;
        String group = userContext.getLogin().getGroup();
        patientAuthTree = GroupPractitionerHelper.copy(GroupPractitionerHelper.getOncologyGroupTreeNode());
//      query permission by group name
        List<String> patientGroupList = new ArrayList<>();
        GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
        List<GroupDto> groupDtoList = groupAntiCorruptionServiceImp.queryGroupListByResourceID(String.valueOf(userContext.getLogin().getResourceSer()));
        if(groupDtoList != null ){
            groupDtoList.forEach(groupDto -> {
//              获取该group本身节点
                GroupTreeNode node = GroupPractitionerHelper.searchGroupById(groupDto.getGroupId());
                if(node != null){
//                对node进行平铺处理
                    List<GroupTreeNode> nodeList = GroupPractitionerHelper.parallelTreeNode(node);
//                  查询每个组是否有patient的查看权限，将patient组名加入到patientGroupList中。
                    nodeList.forEach(groupTreeNode -> {
                        List<String> resourceList = PermissionService.getResourceGroupsOfViewPatientList(groupTreeNode.getOriginalName());
                        if(resourceList != null){
                            resourceList.forEach(r->{
                                if(!patientGroupList.contains(r)){
                                    patientGroupList.add(r);
                                }
                            });
                        }
                    });
                }else{
                    log.error("GroupId:{} GroupName:{} is not in the groupCache.",groupDto.getGroupId(),groupDto.getGroupName());
                }
            });
        }

//        user have view patient group
       if(!patientGroupList.isEmpty()){
            List<String> needDelGroupIdList = new ArrayList<>();
            List<String> patientGroupIdList = new ArrayList<>();
            List<String> viewGroupIdList = new ArrayList<>();
//          将患者的组名转换成Id，存储到patientGroupIdList中。
            groupName2GroupId(patientAuthTree,patientGroupList,patientGroupIdList);
//          根据patientGroupList中的组名，查找没有查看权限的组，存储到无关节点list中，下一步删除
            GroupPractitionerHelper.findDelGroupIdList(patientAuthTree,patientGroupIdList,needDelGroupIdList);
//          删除无关节点needDelGroupIdList中的子树
           GroupPractitionerHelper.cleanNode(patientAuthTree,needDelGroupIdList);
//          根据配置的patientGroupIdList，查询本节点及其子节点
           patientGroupIdList.forEach(groupId->{
               GroupTreeNode tmp = GroupPractitionerHelper.searchGroupById(groupId);
               List<GroupTreeNode> parallelList = GroupPractitionerHelper.parallelTreeNode(tmp);
               parallelList.forEach(g->{
                   if(!viewGroupIdList.contains(g.getId())){
                       viewGroupIdList.add(g.getId());
                   }
               });
           });

           if(patientGroupList.contains(SystemConfigPool.queryGroupOncologistPrefix())){
               patientAuthTree.setId(ActivityCodeConstants.ALL_PATIENTS);
               patientAuthTree.setName(I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.allPatient"));
           }

           if(SystemConfigPool.queryGroupRoleOncologist().equalsIgnoreCase(group)) {
               if(patientGroupList.contains(SystemConfigPool.queryGroupOncologistPrefix())){
                   resultList.add(0,new GroupTreeNode(ActivityCodeConstants.MY_PATIENTS,
                           I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.myPatient"),
                           I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.myPatient")));
               }else {
                   patientAuthTree.setId(ActivityCodeConstants.MY_PATIENTS);
                   patientAuthTree.setName(I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.myPatient"));
               }
           }
           if(patientAuthTree.getId() != null){
               if(StringUtils.isNumeric(patientAuthTree.getId())){
                   patientAuthTree.setIsShow(false);
               }
               resultList.add(patientAuthTree);
           }
           userContext.getLogin().setPatientAuthTree(patientAuthTree);
           userContext.getLogin().setPermissionGroupIdList(viewGroupIdList);
       }else{
           resultList = new ArrayList<>();
           if(SystemConfigPool.queryGroupRoleOncologist().equalsIgnoreCase(group)) {
               patientAuthTree = new GroupTreeNode(ActivityCodeConstants.MY_PATIENTS,
                       I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.myPatient"),
                       I18nReader.getLocaleValueByKey("CarePathTemplateAssembler.myPatient"));
               resultList.add(patientAuthTree);
           }
       }
        return Response.ok(resultList).build();
    }


    /**
     *
     * @param root
     * @param groupNameList
     * @param groupIdList
     */
    private static void groupName2GroupId(GroupTreeNode root,List<String> groupNameList,List<String> groupIdList){
        if(groupNameList.contains(root.getOriginalName())){
            groupIdList.add(root.getId());
        }
        root.getSubItems().forEach(groupTreeNode -> groupName2GroupId(groupTreeNode,groupNameList,groupIdList));
    }
}
