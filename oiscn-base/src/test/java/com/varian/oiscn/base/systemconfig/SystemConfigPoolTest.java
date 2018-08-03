package com.varian.oiscn.base.systemconfig;

import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.config.Configuration;
import io.dropwizard.util.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 11/7/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemConfigPool.class, SystemConfigServiceImp.class})
public class SystemConfigPoolTest {
    private SystemConfigServiceImp systemConfigServiceImp;

    private Configuration configuration;

    @Before
    public void setup() throws Exception {
         systemConfigServiceImp = PowerMockito.mock(SystemConfigServiceImp.class);
        PowerMockito.whenNew(SystemConfigServiceImp.class).withNoArguments().thenReturn(systemConfigServiceImp);

        configuration = new Configuration();
    }

    @Test
    public void givenWhenGetConfigValueThenReturnTheValue() {
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        List<String> result = SystemConfigPool.queryConfigValueByName("itemKey");
        Assert.assertEquals("1", result.get(0));
    }

    @Test
    public void givenWhenQueryVIDPrefixThenReturnThePrefix() {
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        String result = SystemConfigPool.queryVIDPrefix();
        Assert.assertEquals("2", result);
    }

    @Test
    public void givenWhenQueryVIDThenReturnTheVID() {
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        String result = SystemConfigPool.queryStartVIDNumber();
        Assert.assertEquals("3", result);
    }

    @Test
    public void givenWhenQueryDepartmentThenReturnTheID() {
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        String result = SystemConfigPool.queryDefaultDepartment();
        Assert.assertEquals("4", result);
    }

    @Test
    public void givenWhenQueryTreatmentActivityCodeThenReturnString(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        String code = SystemConfigPool.queryTreatmentActivityCode();
        Assert.assertNotNull(code);
        Assert.assertEquals("DoTreatment",code);
    }

    @Test
    public void givenWhenQueryStoredTreatmentAppointment2LocalThenReturnBoolean(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        boolean r = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        Assert.assertTrue(r);
    }

    @Test
    public void givenWhenQueryDiagnosisSearchTopN(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        String diagnosisSearchTopN = SystemConfigPool.queryDiagnosisSearchTopN();
        Assert.assertEquals("50", diagnosisSearchTopN);
    }
    
    @Test
    public void testGetRefreshCacheFromFHIRInterval(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals(12, SystemConfigPool.getRefreshCacheFromFHIRInterval());
    }
    
    @Test
    public void testGetPatientIdMapper(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals("ariaId", SystemConfigPool.getPatientIdMapper());
        Assert.assertEquals("hisId", SystemConfigPool.getPatientId2Mapper());
    }
    
    @Test
    public void testQueryTimeSlotCount(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals("COUNT_PER_SLOT", SystemConfigPool.queryTimeSlotCount());
    }
    
    @Test
    public void testQueryViewAllPatientsForPhysicistFromConfig(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals(true, SystemConfigPool.queryViewAllPatientsForPhysicistFromConfig());
    }
    
    @Test
    public void testQueryAuditLogEntryNumPerBatch(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals(222, SystemConfigPool.queryAuditLogEntryNumPerBatch());
    }

    @Test
    public void testQueryGroup(){
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);
        SystemConfigPool.init(configuration);
        Assert.assertEquals("Nurse",SystemConfigPool.queryGroupRoleNurse());
        Assert.assertEquals("Oncologist",SystemConfigPool.queryGroupRoleOncologist());
        Assert.assertEquals("Physicist",SystemConfigPool.queryGroupRolePhysicist());
        Assert.assertEquals("Therapist",SystemConfigPool.queryGroupRoleTherapist());
        Assert.assertEquals("Nurse",SystemConfigPool.queryGroupNursePrefix());
        Assert.assertEquals("Oncologist",SystemConfigPool.queryGroupOncologistPrefix());
        Assert.assertEquals("Physicist",SystemConfigPool.queryGroupPhysicistPrefix());
        Assert.assertEquals("Technician",SystemConfigPool.queryGroupTechnicianPrefix());
    }

    @Test
    public void testInitConfiguration() {
        Map<String, List<String>> configs = givenSystemConfigs();
        PowerMockito.when(systemConfigServiceImp.queryAllConfigValues()).thenReturn(configs);

        Map<String, String> defaultConf = givenDefaultConf();
        PowerMockito.when(systemConfigServiceImp.getDefaultConf()).thenReturn(defaultConf);

        Map<String, String> fhirConf = givenFHIRConf();
        PowerMockito.when(systemConfigServiceImp.getFHIRServerConf()).thenReturn(fhirConf);

        Map<String, String> httpConf = givenHttpClient();
        PowerMockito.when(systemConfigServiceImp.getHttpClientConf()).thenReturn(httpConf);

        Map<String, String> localeConf = givenLocaleConf();
        PowerMockito.when(systemConfigServiceImp.getLocaleConf()).thenReturn(localeConf);

        Configuration configuration = PowerMockito.mock(Configuration.class);
        SystemConfigPool.init(configuration);
        Assert.assertNotNull(SystemConfigPool.conf);
    }

    @Test
    public void testInitStringConf() {
        Map<String, String> defaultConf = givenDefaultConf();
        String value = SystemConfigPool.initStringConf(defaultConf, "defaultTokenCacheTimeoutInMinutes", "30");
        Assert.assertEquals("30", value);
    }

    @Test
    public void testInitIntegerConf() {
        Map<String, String> fhirConf = givenFHIRConf();
        int value = SystemConfigPool.initIntegerConf(fhirConf, "fhirConnectionTimeout", 40000);
        Assert.assertEquals(40000, value);
    }

    @Test
    public void testInitBooleanConf() {
        Map<String, String> httpConf = givenHttpClient();
        boolean value = SystemConfigPool.initBooleanConf(httpConf, "cookiesEnabled", false);
        Assert.assertFalse(value);
    }

    @Test
    public void testInitDurationConf() {
        Duration duration = Duration.days(1);
        Duration value = SystemConfigPool.initDurationConf(new HashMap<>(), "test", duration);
        Assert.assertEquals(duration, value);
    }

    private Map<String, String> givenLocaleConf() {
        Map<String, String> localeConf = new HashMap<>();
        localeConf.put("country", "CN");
        localeConf.put("language", "zh");
        return localeConf;
    }

    private Map<String, String> givenHttpClient() {
        Map<String, String> httpConf = new HashMap<>();
        httpConf.put("timeout", "10000");
        httpConf.put("connectionTimeout", "10000");
        httpConf.put("connectionRequestTimeout", "10000");
        httpConf.put("timeToLive", "1");
        httpConf.put("cookiesEnabled", "false");
        httpConf.put("maxConnections", "1024");
        httpConf.put("maxConnectionsPerRoute", "1024");
        httpConf.put("keepAlive", "0");
        return httpConf;
    }

    private Map<String, String> givenFHIRConf() {
        Map<String, String> fhirConf = new HashMap<>();
        fhirConf.put("fhirConnectionTimeout", "40000");
        fhirConf.put("fhirConnectionRequestTimeout", "40000");
        fhirConf.put("fhirSocketTimeout", "40000");
        fhirConf.put("fhirLanguage", "CHS");
        return fhirConf;
    }

    private Map<String, String> givenDefaultConf() {
        Map<String, String> defaultConf = new HashMap<>();
        defaultConf.put("defaultTokenCacheTimeoutInMinutes", "30");
        defaultConf.put("resourceAutoUnlockInMinutes", "5");
        defaultConf.put("ospTokenValidationInterval", "5");
        defaultConf.put("fhirTokenAuthEnabled", "0");
        defaultConf.put("performanceLogging", "false");
        return defaultConf;
    }

    private Map<String, List<String>> givenSystemConfigs() {
        Map<String, List<String>> systemConfigs = new HashMap<>();
        systemConfigs.put("itemKey", Arrays.asList("1"));
        systemConfigs.put(SystemConfigConstant.VID_PREFIX, Arrays.asList("2"));
        systemConfigs.put(SystemConfigConstant.VID, Arrays.asList("3"));
        systemConfigs.put(SystemConfigConstant.DEFAULT_DEPARTMENT, Arrays.asList("4"));
        systemConfigs.put(SystemConfigConstant.TREATMENT_ACTIVITY_CODE,Arrays.asList("DoTreatment"));
        systemConfigs.put(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL,Arrays.asList("true"));
        systemConfigs.put(SystemConfigConstant.DIAGNOSIS_SEARCH_TOP_N, Arrays.asList("50"));
        systemConfigs.put(SystemConfigConstant.REFRESH_CACHE_FROM_FHIR_INTERVAL, Arrays.asList("12"));
        systemConfigs.put(SystemConfigConstant.PATIENT_ID_ONE, Arrays.asList("ariaId"));
        systemConfigs.put(SystemConfigConstant.PATIENT_ID_TWO, Arrays.asList("hisId"));
        systemConfigs.put(SystemConfigConstant.COUNT_PER_SLOT, Arrays.asList("COUNT_PER_SLOT"));
        systemConfigs.put(SystemConfigConstant.VIEW_ALLPATIENTS_PHYSICIST_CONFIG, Arrays.asList("true"));
        systemConfigs.put(SystemConfigConstant.AUDIT_LOG_ENTRY_NUM_PER_BATCH, Arrays.asList("222"));
        systemConfigs.put(SystemConfigConstant.GROUP_ROLE_NURSE,Arrays.asList("Nurse"));
        systemConfigs.put(SystemConfigConstant.GROUP_ROLE_ONCOLOGIST,Arrays.asList("Oncologist"));
        systemConfigs.put(SystemConfigConstant.GROUP_ROLE_THERAPIST,Arrays.asList("Therapist"));
        systemConfigs.put(SystemConfigConstant.GROUP_ROLE_PHYSICIST,Arrays.asList("Physicist"));
        systemConfigs.put(SystemConfigConstant.GROUP_NURSE_PREFIX,Arrays.asList("Nurse"));
        systemConfigs.put(SystemConfigConstant.GROUP_ONCOLOGIST_PREFIX,Arrays.asList("Oncologist"));
        systemConfigs.put(SystemConfigConstant.GROUP_TECHNICIAN_PREFIX,Arrays.asList("Technician"));
        systemConfigs.put(SystemConfigConstant.GROUP_PHYSICIST_PREFIX,Arrays.asList("Physicist"));
        return systemConfigs;
    }
}
