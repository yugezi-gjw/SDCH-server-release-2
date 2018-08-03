package com.varian.oiscn.base.dynamicform;

/**
 * Created by gbt1220 on 6/28/2017.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DynamicFormTemplateResource.class, ActivityCodesReader.class})
public class DynamicFormTemplateResourceTest {

    private Configuration configuration;

    private Environment environment;

    private DynamicFormTemplateServiceImp dynamicFormTemplateServiceImp;

    private DynamicFormTemplateResource dynamicFormTemplateResource;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        dynamicFormTemplateServiceImp = PowerMockito.mock(DynamicFormTemplateServiceImp.class);
        PowerMockito.whenNew(DynamicFormTemplateServiceImp.class).withNoArguments().thenReturn(dynamicFormTemplateServiceImp);
        dynamicFormTemplateResource = new DynamicFormTemplateResource(configuration, environment);
    }

    @Test
    public void givenEmptyTemplateIdWhenSearchThenReturnBadRequest() {
        String emptyTemplateId = "";
        Response response = dynamicFormTemplateResource.search(new UserContext(), emptyTemplateId);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenTemplateIdWhenSearchThenReturnJsonString() {
        String templateId = "testTemplateId";
        String json = "jsonString";
        Map<String, String> jsonList = new HashMap<String, String>() {{
            put(templateId, json);
        }};
        PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(Arrays.asList(templateId))).thenReturn(jsonList);
        Response response = dynamicFormTemplateResource.search(new UserContext(), templateId);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is(json));
    }

    @Test
    public void givenTemplateIdListWhenSearchThenReturnJsonListString() {
        String templateId1 = "testTemplateId1";
        String templateId2 = "testTemplateId2";
        List<KeyValuePair> templateIdList = Arrays.asList(new KeyValuePair(templateId1, null), new KeyValuePair(templateId2, null));

        String json1 = "{\"key\":\"value\"}";
        String json2 = "{\"key2\":\"value2\"}";
        Map<String, String> jsonList = new LinkedHashMap<String, String>() {{
            put(templateId1, json1);
            put(templateId2, json2);
        }};
        List<JSONObject> list = Arrays.asList(JSONObject.fromObject(json1), JSONObject.fromObject(json2));
        PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateJsonByTemplateId(Arrays.asList(templateId1, templateId2))).thenReturn(jsonList);
        Response response = dynamicFormTemplateResource.search(new UserContext(), templateIdList);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is(list));
    }

    @Test
    public void givenEmptyTemplateNameWhenSearchThenReturnBadRequest() {
        List<KeyValuePair> templateList = new ArrayList<>();
        Response response = dynamicFormTemplateResource.searchTemplateName(new UserContext(), templateList);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenTemplateIdsWhenSearchThenReturnJsonString() throws JsonProcessingException {
        List<KeyValuePair> templateList = Arrays.asList(new KeyValuePair("templateId1", null), new KeyValuePair("templateId2", null));
        List<String> templateIds = new ArrayList<>();
        for (KeyValuePair temp : templateList) {
            templateIds.add(temp.getKey());
        }
        String json = "[{\"key\":\"templateId1\",\"value\":\"name1\"},{\"key\":\"templateId2\",\"value\":\"name2\"}]";

        List<KeyValuePair> ret = Arrays.asList(new KeyValuePair("templateId1", "name1"), new KeyValuePair("templateId2", "name2"));
        PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(templateIds)).thenReturn(ret);
        Response response = dynamicFormTemplateResource.searchTemplateName(new UserContext(), templateList);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        String value = Jackson.newObjectMapper().writeValueAsString(response.getEntity());
        assertThat(value, is(json));
    }

    @Test
    public void givenTemplateIdAndActivityCodeWhenSearchDefaultValueTemplateNameByTemplateIdThenReturnDefaultValueTemplateList() {
        PowerMockito.mockStatic(ActivityCodesReader.class);
        String templateId = "templateId";
        PowerMockito.when(ActivityCodesReader.getActivityCode(Matchers.anyString())).thenReturn(new ActivityCodeConfig() {{
            addTemplateDefaultValues(templateId, Arrays.asList("defaultValueTemplateId1", "defaultValueTemplateId2"));
        }});

        PowerMockito.when(dynamicFormTemplateServiceImp.queryTemplateNamesByTemplateIds(ActivityCodesReader.getActivityCode("").getTemplateDefaultValues().get(templateId)))
                .thenReturn(Arrays.asList(new KeyValuePair("defaultValueTemplateId1", "name1"), new KeyValuePair("defaultValueTemplateId2", "name2")));

        Response response = dynamicFormTemplateResource.searchDefaultValueTemplateNameByTemplateId(new UserContext(),"activityCode", templateId);
        Assert.assertNotNull(response.getEntity());
        Assert.assertTrue(((List) response.getEntity()).size() == 2);
    }
}
