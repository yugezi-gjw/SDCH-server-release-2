package com.varian.oiscn.base.dynamicform;

import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by gbt1220 on 6/28/2017.
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DynamicFormTemplateResource extends AbstractResource {

    private DynamicFormTemplateServiceImp dynamicFormTemplateServiceImp;

    public DynamicFormTemplateResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        dynamicFormTemplateServiceImp = new DynamicFormTemplateServiceImp();
    }

    @Path("/dynamicformtemplate/search")
    @GET
    public Response search(@Auth UserContext userContext, @QueryParam("templateId") String templateId) {
        if (isBlank(templateId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(StringUtils.EMPTY).build();
        }
        String templateJson = StringUtils.EMPTY;
        Map<String, String> jsonMap = dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(Arrays.asList(templateId));
        if (!jsonMap.isEmpty()) {
            templateJson = jsonMap.get(templateId);
        }
        return Response.status(Response.Status.OK).entity(templateJson).build();
    }

    @Path("/dynamicformtemplate/search")
    @POST
    public Response search(@Auth UserContext userContext, List<KeyValuePair> templateIdKeyValuePairList) {
        if (templateIdKeyValuePairList == null || templateIdKeyValuePairList.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(StringUtils.EMPTY).build();
        }
        List<String> templateIdList = new ArrayList<>();
        templateIdKeyValuePairList.forEach(keyValuePair ->
                templateIdList.add(keyValuePair.getKey())
        );
        List<Map<String, JSONObject>> jsonList = new ArrayList<>();
        Map<String, String> jsonMap = dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(templateIdList);
        JsonConfig config = new JsonConfig();
        config.setIgnoreDefaultExcludes(true);
        for(String templateId:templateIdList){
            String value = jsonMap.get(templateId);
            if(StringUtils.isEmpty(value)){
                log.error("Can't find json file for templateId: {}", templateId);
                return  Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new KeyValuePair("msg","Can't find json file for templateId "+templateId)).build();
            }
            JSONObject jsonObject;
            if(value.startsWith("\uFEFF")){
                jsonObject = JSONObject.fromObject(value.replaceFirst("\uFEFF",""),config);
            }else{
                jsonObject = JSONObject.fromObject(value,config);
            }
            jsonList.add(jsonObject);
        }

        return Response.status(Response.Status.OK).entity(jsonList).build();
    }



    @Path("/dynamicformtemplate/searchName")
    @POST
    public Response searchTemplateName(@Auth UserContext userContext, List<KeyValuePair> templateIds) {
        if (templateIds == null || templateIds.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(StringUtils.EMPTY).build();
        }
        List<String> tmpIdList = new ArrayList<>();
        for (KeyValuePair template : templateIds) {
            tmpIdList.add(template.getKey());
        }
        List<KeyValuePair> result = dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(tmpIdList);
        List<KeyValuePair> sortResult = new ArrayList<>();
        for (KeyValuePair keyValuePair : templateIds) {
            for (KeyValuePair keypair : result) {
                if (keyValuePair.getKey().equals(keypair.getKey())) {
                    sortResult.add(keypair);
                    break;
                }
            }
        }
        return Response.status(Response.Status.OK).entity(sortResult).build();
    }

    @Path("/dynamicformtemplate/defaultvalue")
    @GET
    public Response searchDefaultValueTemplateNameByTemplateId(@Auth UserContext userContext,@QueryParam("activityCode") String activityCode,@QueryParam("templateId") String templateId){
        if (StringUtils.isEmpty(templateId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(StringUtils.EMPTY).build();
        }
        List<String> defaultValueTemplateIdList = ActivityCodesReader.getActivityCode(activityCode).getTemplateDefaultValues().get(templateId);
        List<KeyValuePair> sortResult = new ArrayList<>();
        if(defaultValueTemplateIdList != null && !defaultValueTemplateIdList.isEmpty()){
            List<KeyValuePair> result = dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(defaultValueTemplateIdList);
            for (KeyValuePair keyValuePair : result) {
                for (String tmpId : defaultValueTemplateIdList) {
                    if (keyValuePair.getKey().equals(tmpId)) {
                        sortResult.add(keyValuePair);
                        break;
                    }
                }
            }
        }
        return Response.ok(sortResult).build();
    }
}
