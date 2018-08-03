package com.varian.oiscn.base.group;

import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.util.I18nReader;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 2/7/2017.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource extends AbstractResource {

    private GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp;

    public GroupResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
    }

    @GET
    @Path("/groups")
    public Response queryAllPhysicianGroups(@Auth UserContext userContext) {
        GroupTreeNode rootGroupTreeNode = GroupPractitionerHelper.getOncologyGroupTreeNode();
        if(rootGroupTreeNode == null){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No groups found.").build();
        }
        List<GroupDto> groupDtoList = new ArrayList<>();
        if(rootGroupTreeNode.getSubItems().isEmpty()){
            //如果没有第二级的分组，则直接回传Oncologist的id，显示成所有医生
            GroupDto groupDto = new GroupDto(rootGroupTreeNode.getId(), I18nReader.getLocaleValueByKey("Oncologist.DisplayName.NoSecondaryGroup"));
            groupDtoList.add(groupDto);
        } else{
            for(GroupTreeNode groupTreeNode : rootGroupTreeNode.getSubItems()){
                GroupDto groupDto = new GroupDto(groupTreeNode.getId(), groupTreeNode.getName());
                groupDtoList.add(groupDto);
            }
        }

        return Response.ok(groupDtoList).build();
    }
}
