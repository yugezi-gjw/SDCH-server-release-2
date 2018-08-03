package com.varian.oiscn.base.util;

import com.varian.oiscn.config.Configuration;

/**
 * Created by gbt1220 on 6/7/2017.
 */

import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.util.I18nReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ActivityCodesReader.class)
public class ActivityCodesReaderTest {

    @InjectMocks
    private ActivityCodesReader reader;

    private FileInputStream fileInputStream;

    private Yaml yaml;
    
    protected Configuration configuration;

    @Before
    public void setup() throws Exception {
        Locale.setDefault(Locale.CHINA);
        fileInputStream = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStream);
        yaml = PowerMockito.mock(Yaml.class);
        PowerMockito.whenNew(Yaml.class).withNoArguments().thenReturn(yaml);
        
        configuration = PowerMockito.mock(Configuration.class);
        String configFile = "../config/CarePath.yaml";
        PowerMockito.when(configuration.getCarePathConfigFile()).thenReturn(configFile);
    }

    @Test
    public void givenActivityCodeWhenGetThenReturnTheActivityCode() throws Exception {
        LinkedHashMap map = givenActivityCodesMap();
        PowerMockito.when(yaml.load(fileInputStream)).thenReturn(map);
        ActivityCodesReader.init(configuration);
        ActivityCodeConfig placeImmoCTOrder = ActivityCodesReader.getActivityCode("PlaceImmoCTOrder");
        Assert.assertEquals("PlaceImmoCTOrder", placeImmoCTOrder.getName());
        Assert.assertEquals(I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointment"), placeImmoCTOrder.getContent());//模拟定位申请
        Assert.assertEquals(I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointed"), placeImmoCTOrder.getCompletedContent());//模拟定位已申请
        Assert.assertEquals(I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointment"), placeImmoCTOrder.getEntryContent());//模拟定位
        Assert.assertEquals("DYNAMIC_FORM", placeImmoCTOrder.getWorkspaceType());
        Assert.assertEquals("PlaceImmoCTOrder", placeImmoCTOrder.getDynamicFormTemplateIds().get(0));
        Assert.assertTrue(placeImmoCTOrder.getNeedChargeBill());
        Assert.assertNotNull(placeImmoCTOrder.getReleaseResourceForActivity());
    }

    @Test
    public void givenActivityCodeWhenFileNotFoundThenReturnEmpty() throws Exception {
        PowerMockito.when(yaml.load(fileInputStream)).thenThrow(FileNotFoundException.class);
        ActivityCodesReader.init(configuration);
        Assert.assertNull(ActivityCodesReader.getActivityCode("PlaceImmoCTOrder").getName());
    }

    @Test
    public void givenWhenGetNeedChargeBillListThenReturnResult() {
        LinkedHashMap map = givenActivityCodesMap();
        PowerMockito.when(yaml.load(fileInputStream)).thenReturn(map);
        ActivityCodesReader.init(configuration);
        List<ActivityCodeConfig> needChargeList = ActivityCodesReader.getNeedChargeBillActivityCodeList();
        Assert.assertEquals(1, needChargeList.size());
    }

    @Test
    public void givenRelativeCodeWhenGetSourceCodeThenReturnResult() {
        LinkedHashMap map = givenActivityCodesMap();
        PowerMockito.when(yaml.load(fileInputStream)).thenReturn(map);
        ActivityCodesReader.init(configuration);
        ActivityCodeConfig placeImmoCTOrder = ActivityCodesReader.getSourceActivityCodeByRelativeCode("DoImportCTImage");
        Assert.assertEquals("ImportCTImage", placeImmoCTOrder.getName());
    }

    @Test
    public void givenDefaultValueActivityCodeWhenGetActivityCodeThenReturnDefaultValue() {
        LinkedHashMap map = givenActivityCodesMap();
        PowerMockito.when(yaml.load(fileInputStream)).thenReturn(map);
        ActivityCodesReader.init(configuration);
        ActivityCodeConfig placeImmoCTOrder = ActivityCodesReader.getActivityCode("PlaceImmoCTOrder");
        Assert.assertNotNull(placeImmoCTOrder);
        Assert.assertTrue(!placeImmoCTOrder.getTemplateDefaultValues().get("PlaceImmoCTOrder").isEmpty());
    }

    private LinkedHashMap givenActivityCodesMap() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("PlaceImmoCTOrder", new LinkedHashMap<>());
        LinkedHashMap linkedHashMap = (LinkedHashMap) map.get("PlaceImmoCTOrder");
        linkedHashMap.put("content", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointment")); //模拟定位申请
        linkedHashMap.put("entryContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointment")); //模拟定位申请
        linkedHashMap.put("completedContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.simulateImmobilizationAppointed")); //模拟定位已申请
        linkedHashMap.put("needChargeBill", Boolean.TRUE);
        linkedHashMap.put("releaseResourceForActivity", "ActivityCode01,ActivityCode02");
        linkedHashMap.put("workspace", new LinkedHashMap<>());
        LinkedHashMap workspaceMap = (LinkedHashMap) linkedHashMap.get("workspace");
        workspaceMap.put("workspaceType", "DYNAMIC_FORM");
        workspaceMap.put("dynamicFormTemplate", new ArrayList<LinkedHashMap>());
        List<LinkedHashMap> dynamicFormTemplateMapList = (List<LinkedHashMap>) workspaceMap.get("dynamicFormTemplate");
        LinkedHashMap templateMap1 = new LinkedHashMap();
        templateMap1.put("templateId", "PlaceImmoCTOrder");

        List<LinkedHashMap<String, String>> defaultValueList = new ArrayList<>();
        LinkedHashMap<String, String> defaultValueMap = new LinkedHashMap<>();
        defaultValueMap.put("templateId", "PlaceImmoCTOrderDefaultValue1");
        defaultValueList.add(defaultValueMap);
        LinkedHashMap<String, String> defaultValueMap2 = new LinkedHashMap<>();
        defaultValueMap2.put("templateId", "PlaceImmoCTOrderDefaultValue1");
        defaultValueList.add(defaultValueMap2);
        templateMap1.put(ActivityCodesReader.DYNAMIC_FORM_DEFAULT_VALUE_TEMPLATE, defaultValueList);

        LinkedHashMap templateMap2 = new LinkedHashMap();
        templateMap2.put("templateId", "PlaceImmoCTOrderSwitch");
        dynamicFormTemplateMapList.add(templateMap1);
        dynamicFormTemplateMapList.add(templateMap2);
        workspaceMap.put("eclipseModuleId", "");

        map.put("ImportCTImage>>DoImportCTImage", new LinkedHashMap<>());
        LinkedHashMap linkedHashMap2 = (LinkedHashMap) map.get("ImportCTImage>>DoImportCTImage");
        linkedHashMap2.put("content", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importCTImage")); //导入CT图像
        linkedHashMap2.put("entryContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importCTImage")); //导入CT图像
        linkedHashMap2.put("completedContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importedCTImage")); //CT图像已导入
        linkedHashMap2.put("workspace", new LinkedHashMap<>());
        LinkedHashMap workspaceMap2 = (LinkedHashMap) linkedHashMap2.get("workspace");
        workspaceMap2.put("workspaceType", "ECLIPSE_DYNAMIC_FORM");
        workspaceMap2.put("dynamicFormTemplate", new ArrayList<LinkedHashMap>());
        List<LinkedHashMap> dynamicFormTemplateMapList2 = (List<LinkedHashMap>) workspaceMap2.get("dynamicFormTemplate");
        LinkedHashMap templateMap3 = new LinkedHashMap();
        templateMap3.put("templateId", "PlaceImmoCTOrder");
        LinkedHashMap templateMap4 = new LinkedHashMap();
        templateMap4.put("templateId", "PlaceImmoCTOrderSwitch");
        dynamicFormTemplateMapList2.add(templateMap3);
        dynamicFormTemplateMapList2.add(templateMap4);
        workspaceMap2.put("eclipseModuleId", "ImportExport");

        map.put("DoImportCTImage", new LinkedHashMap<>());
        LinkedHashMap linkedHashMap3 = (LinkedHashMap) map.get("DoImportCTImage");
        linkedHashMap3.put("content", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importCTImage")); //导入CT图像
        linkedHashMap3.put("entryContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importCTImage")); //导入CT图像
        linkedHashMap3.put("completedContent", I18nReader.getLocaleValueByKey("ActivityCodesReaderTests.importedCTImage")); //CT图像已导入
        linkedHashMap3.put("workspace", new LinkedHashMap<>());
        LinkedHashMap workspaceMap3 = (LinkedHashMap) linkedHashMap3.get("workspace");
        workspaceMap3.put("workspaceType", "ECLIPSE_DYNAMIC_FORM");
        workspaceMap3.put("dynamicFormTemplate", new ArrayList<LinkedHashMap>());
        workspaceMap3.put("eclipseModuleId", "ImportExport");

        return map;
    }
}
