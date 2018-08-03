/**
 *
 */
package com.varian.oiscn.carepath.service;

import com.varian.oiscn.base.codesystem.CodeSystemPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import com.varian.oiscn.core.carepath.CarePathTemplateVO;
import com.varian.oiscn.core.codesystem.CodeValue;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CarePathConfigService.class, CodeSystemPool.class, HttpClients.class})
public class CarePathConfigServiceTest {

    protected Configuration configuration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        String configFile = "../config/CarePath.yaml";
        PowerMockito.when(configuration.getCarePathConfigFile()).thenReturn(configFile);

    }

    /**
     * Test method for {@link com.varian.oiscn.carepath.service.CarePathConfigService#init(com.varian.oiscn.config.Configuration)}.
     */
    @Test
    public void testInit() {
        CarePathConfigService.init(configuration);
        Assert.assertNotNull(CarePathConfigService.carePathConfigItemList);
        Assert.assertTrue(CarePathConfigService.carePathConfigItemList.size() > 0);
    }

    /**
     * Test method for {@link com.varian.oiscn.carepath.service.CarePathConfigService#getCarePathTemplateList()}.
     */
    @Test
    public void testGetCarePathTemplateList() {
        List<CodeValue> carepathList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            carepathList.add(new CodeValue("00" + i, "template0" + i));
        }
        List<CarePathConfigItem> itemList = new ArrayList<>();
        for (int i = 3; i < 10; i++) {
            CarePathConfigItem item = new CarePathConfigItem();
            item.setTemplateId("template0" + i);
            item.setTemplateName("模板0" + i);
            item.setDescription("好多好多" + i + "的描述" + i);
            itemList.add(item);
        }

        CarePathConfigService.carePathConfigItemList = itemList;

        PowerMockito.mockStatic(CodeSystemPool.class);
        PowerMockito.when(CodeSystemPool.getCarePathList()).thenReturn(carepathList);

        List<CarePathTemplateVO> cpTemplateList = CarePathConfigService.getCarePathTemplateList();
        Assert.assertNotNull(cpTemplateList);
        Assert.assertTrue(cpTemplateList.size() == 7);
    }

    /**
     * Test method for {@link com.varian.oiscn.carepath.service.CarePathConfigService#getTemplateNameById(java.lang.String)}.
     */
    @Test
    public void testGetTemplateNameById() {
        List<CarePathConfigItem> itemList = new ArrayList<>();
        for (int i = 3; i < 10; i++) {
            CarePathConfigItem item = new CarePathConfigItem();
            item.setTemplateId("template0" + i);
            item.setTemplateName("模板0" + i);
            item.setDescription("好多好多" + i + "的描述" + i);
            itemList.add(item);
        }

        CarePathConfigService.carePathConfigItemList = itemList;
        for (int i = 3; i < 10; i++) {
            Assert.assertEquals("模板0" + i, CarePathConfigService.getTemplateNameById("template0" + i));
        }
    }

}
