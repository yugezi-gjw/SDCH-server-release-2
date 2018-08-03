package com.varian.oiscn.base.systemconfig;

import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import com.varian.oiscn.config.LocaleConfiguration;
import com.varian.oiscn.util.LogUtil;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 9/26/2017.
 */
@Slf4j
public class SystemConfigPool {

    public static final String DEFAULT_ABBREVIATION = "CCIP";
    
    protected static Configuration conf = null;
    /**
     * Default view for users which has no default view.
     */
    private static Map<String, List<String>> systemConfigs;
    /**
     * Hospital Abbreviation. (e.g.BCRI, SDCH, CCIP by default)<br>
     */
    private static String dynamicFormTemplateCategory = "";
    
    public static String getDynamicFormTemplateCategory() {
        return dynamicFormTemplateCategory;
    }

    private static SystemConfigServiceImp service = new SystemConfigServiceImp();

    /**
     * init all config from db
     */
    public static void init(Configuration configuration) {
        conf = configuration;
        long start = System.currentTimeMillis();
        systemConfigs = service.queryAllConfigValues();
        if (systemConfigs == null || systemConfigs.size() == 0) {
            throw new RuntimeException("No Configuration in SystemConfig Table, Please check Database!!!");
        }
        // init configuration which is from local.yaml
        Map<String, String> defaultConf = service.getDefaultConf();

        int defaultTokenCacheTimeoutInMinutes = initIntegerConf(defaultConf, "defaultTokenCacheTimeoutInMinutes", 30);
        conf.setDefaultTokenCacheTimeoutInMinutes(defaultTokenCacheTimeoutInMinutes);

        int resourceAutoUnlockInMinutes = initIntegerConf(defaultConf, "resourceAutoUnlockInMinutes", 5);
        conf.setResourceAutoUnlockInMinutes(resourceAutoUnlockInMinutes);

        int ospTokenValidationInterval = initIntegerConf(defaultConf, "ospTokenValidationInterval", 5);
        conf.setOspTokenValidationInterval(ospTokenValidationInterval);

        int fhirTokenAuthEnabled = initIntegerConf(defaultConf, "fhirTokenAuthEnabled", 0);
        conf.setFhirTokenAuthEnabled(fhirTokenAuthEnabled);

        String performanceLogging = initStringConf(defaultConf, "performanceLogging", "false");
        LogUtil.setPerformanceLogging("true".equalsIgnoreCase(performanceLogging));

        if (StringUtils.isNotBlank(conf.getDynamicFormTemplateCategory())) {
            // default : CCIP
            dynamicFormTemplateCategory = conf.getDynamicFormTemplateCategory().trim();
        } else {
            dynamicFormTemplateCategory = DEFAULT_ABBREVIATION;
        }
        
        // FHIR configuration
        FhirServerConfiguration fhirServerConfiguration = new FhirServerConfiguration();

        // fetch FHIR configuration from SystemConfig
        Map<String, String> fhirConf = service.getFHIRServerConf();

        int fhirConnectionTimeout = initIntegerConf(fhirConf, "fhirConnectionTimeout", 40000);
        fhirServerConfiguration.setFhirConnectionTimeout(fhirConnectionTimeout);

        int fhirConnectionRequestTimeout = initIntegerConf(fhirConf, "fhirConnectionRequestTimeout", 40000);
        fhirServerConfiguration.setFhirConnectionRequestTimeout(fhirConnectionRequestTimeout);

        int fhirSocketTimeout = initIntegerConf(fhirConf, "fhirSocketTimeout", 40000);
        fhirServerConfiguration.setFhirSocketTimeout(fhirSocketTimeout);

        String fhirLanguage = initStringConf(fhirConf, "fhirLanguage", "CHS");
        fhirServerConfiguration.setFhirLanguage(fhirLanguage);
        conf.setFhirServerConfiguration(fhirServerConfiguration);

        // httpClientConfiguration
        JerseyClientConfiguration httpClientConfiguration = new JerseyClientConfiguration();
        Map<String, String> httpConf = service.getHttpClientConf();

        // timeout: 10000ms
        // connectionTimeout: 10000ms
        // connectionRequestTimeout: 10000ms
        // timeToLive: 1 hour
        // cookiesEnabled: false
        // maxConnections: 1024
        // maxConnectionsPerRoute: 1024
        // keepAlive: 0s
        Duration duration = initDurationConf(httpConf, "timeout", Duration.milliseconds(10000));
        httpClientConfiguration.setTimeout(duration);

        Duration connectionTimeout = initDurationConf(httpConf, "connectionTimeout", Duration.milliseconds(10000));
        httpClientConfiguration.setConnectionTimeout(connectionTimeout);

        Duration connectionRequestTimeout = initDurationConf(httpConf, "connectionRequestTimeout", Duration.milliseconds(10000));
        httpClientConfiguration.setConnectionRequestTimeout(connectionRequestTimeout);

        Duration timeToLive = initDurationConf(httpConf, "timeToLive", Duration.hours(1));
        httpClientConfiguration.setTimeToLive(timeToLive);

        boolean cookiesEnabled = initBooleanConf(httpConf, "cookiesEnabled", false);
        httpClientConfiguration.setCookiesEnabled(cookiesEnabled);

        int maxConnections = initIntegerConf(httpConf, "maxConnections", 1024);
        httpClientConfiguration.setMaxConnections(maxConnections);

        int maxConnectionsPerRoute = initIntegerConf(httpConf, "maxConnectionsPerRoute", 1024);
        httpClientConfiguration.setMaxConnectionsPerRoute(maxConnectionsPerRoute);

        Duration keepAlive = initDurationConf(httpConf, "keepAlive", Duration.seconds(0));
        httpClientConfiguration.setKeepAlive(keepAlive);
        conf.setHttpClientConfiguration(httpClientConfiguration);

        // fetch locale configuration from SystemConfig
        Map<String, String> localeConf = service.getLocaleConf();
        LocaleConfiguration locale = new LocaleConfiguration();

        String country = initStringConf(localeConf, "country", "CN");
        locale.setCountry(country);

        String language = initStringConf(localeConf, "language", "zh");
        locale.setLanguage(language);
        conf.setLocale(locale);

        log.debug("SystemConfigPool init last: {} ms", (System.currentTimeMillis() - start));
    }

    public static String initStringConf(Map<String, String> confMap, String key, String defaultValue) {
        String retValue = defaultValue;
        String confValue = confMap.get(key);
        if (StringUtils.isNotBlank(confValue)) {
            retValue = confValue;
        } else {
            log.warn("Bad Configuration in SystemConfig [{}]: [{}]", key, confValue);
        }
        return retValue;
    }

    public static int initIntegerConf(Map<String, String> confMap, String key, int defaultValue) {
        int retValue = defaultValue;
        String confValue = null;
        try {
            confValue = confMap.get(key);
            retValue = Integer.parseInt(confValue);
        } catch (Exception e) {
            log.warn("Bad Configuration in SystemConfig [{}]: [{}]", key, confValue);
        }
        return retValue;
    }

    public static boolean initBooleanConf(Map<String, String> confMap, String key, boolean defaultValue) {
        boolean retValue = defaultValue;
        String confValue = null;
        try {
            confValue = confMap.get(key);
            retValue = Boolean.parseBoolean(confValue);
        } catch (Exception e) {
            log.warn("Bad Configuration in SystemConfig [{}]: [{}]", key, confValue);
        }
        return retValue;
    }

    public static Duration initDurationConf(Map<String, String> confMap, String key, Duration defaultValue) {
        Duration retValue = defaultValue;
        String confValue = null;
        try {
            confValue = confMap.get(key);
            retValue = Duration.parse(confValue);
        } catch (Exception e) {
            log.warn("Bad Configuration in SystemConfig [{}]: [{}]", key, confValue);
        }
        return retValue;
    }

    /**
     * query config values by config name
     * @param name config name
     * @return config values
     */
    public static List<String> queryConfigValueByName(String name) {
        List<String> result = systemConfigs.get(name);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * query vid prefix
     * @return vid prefix
     */
    public static String queryVIDPrefix() {
        List<String> vidPrefix = queryConfigValueByName(SystemConfigConstant.VID_PREFIX);
        if (!vidPrefix.isEmpty()) {
            return vidPrefix.get(0);
        }
        return StringUtils.EMPTY;
    }

    /**
     * query start number of vid
     * @return start number of vid, default is '0'
     */
    public static String queryStartVIDNumber() {
        List<String> vids = queryConfigValueByName(SystemConfigConstant.VID);
        if (!vids.isEmpty()) {
            return vids.get(0);
        }
        return "0";
    }

    /**
     * query default department id
     * @return default department id
     */
    public static String queryDefaultDepartment() {
        List<String> defaultDepartmentIds = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT);
        if (!defaultDepartmentIds.isEmpty()) {
            return defaultDepartmentIds.get(0);
        } else {
            log.error("Please configure default department in local db.");
            return null;
        }
    }

    /**
     * 获取治疗节点的code
     * @return
     */
    public static String queryTreatmentActivityCode(){
        String treatmentActivityCode = null;
        List<String> treatmentActivityCodeList = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE);
        if (!treatmentActivityCodeList.isEmpty()) {
            treatmentActivityCode = treatmentActivityCodeList.get(0);
        }
        return treatmentActivityCode;
    }

    /**
     * 查询是否将治疗的预约优先保存到本地
     * @return
     */
    public static boolean queryStoredTreatmentAppointment2Local(){
        List<String> appointmentStoredToLocalList = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL);
        boolean appointmentStoredToLocal = false;
        if (!appointmentStoredToLocalList.isEmpty()) {
            appointmentStoredToLocal = Boolean.valueOf(appointmentStoredToLocalList.get(0));
        }
        return appointmentStoredToLocal;
    }


    public static int getRefreshCacheFromFHIRInterval(){
        List<String> cacheRefreshingInterval = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.REFRESH_CACHE_FROM_FHIR_INTERVAL);
        if(cacheRefreshingInterval.isEmpty()){
            return -1;
        }
        int result = 0;
        try{
            result = Integer.parseInt(cacheRefreshingInterval.get(0));
        } catch(NumberFormatException e){
            log.error("Cannot parse the configuration REFRESH_CACHE_FROM_FHIR_INTERVAL. The value in database is " + cacheRefreshingInterval.get(0));
            return -1;
        }
        return result;
    }

    public static String getPatientIdMapper(){
        String patientIdOne = PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID;
        List<String> ariaIdList = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.PATIENT_ID_ONE);
        if (!ariaIdList.isEmpty()) {
            patientIdOne = ariaIdList.get(0);
        }
        return patientIdOne;
    }

    public static String getPatientId2Mapper(){
        String ariaId2 = PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID;
        List<String> ariaList = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.PATIENT_ID_TWO);
        if (!ariaList.isEmpty()) {
            ariaId2 = ariaList.get(0);
        }
        return ariaId2;
    }

    /**
     * 查询诊断编码和诊断部位搜索最多返回多少条数据
     * @return
     */
    public static String queryDiagnosisSearchTopN(){
        List<String> diagnosisSearchTopN = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DIAGNOSIS_SEARCH_TOP_N);
        if(!diagnosisSearchTopN.isEmpty()){
            return diagnosisSearchTopN.get(0);
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * query max count per time slot
     * @return
     */
    public static String queryTimeSlotCount() {
        List<String> timeSlotCounts = queryConfigValueByName(SystemConfigConstant.COUNT_PER_SLOT);
        if (!timeSlotCounts.isEmpty()) {
            return timeSlotCounts.get(0);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Query flag for physicist grouping
     * @return
     */
    public static boolean queryViewAllPatientsForPhysicistFromConfig() {
        List<String> result = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.VIEW_ALLPATIENTS_PHYSICIST_CONFIG);
        if(!result.isEmpty()) {
            String value = result.get(0);
            return Boolean.valueOf(value);
        }
        return false;
    }

    public static int queryAuditLogEntryNumPerBatch() {
        List<String> result = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.AUDIT_LOG_ENTRY_NUM_PER_BATCH);
        if(!result.isEmpty()) {
            String value = result.get(0);
            return Integer.valueOf(value);
        }
        return -1;
    }

    public static List<String> queryAssignMachineTaskFields(String activityCode) {
        List<String> result = SystemConfigPool.queryConfigValueByName(activityCode);
        return result;
    }

    /**
     * Aria Oncologist group role
     * @return
     */
    public static String queryGroupRoleOncologist(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_ROLE_ONCOLOGIST);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * Aria Nurse group role
     * @return
     */
    public static String queryGroupRoleNurse(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_ROLE_NURSE);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * Aria Physicist group role
     * @return
     */
    public static String queryGroupRolePhysicist(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_ROLE_PHYSICIST);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * Aria Therapist group role
     * @return
     */
    public static String queryGroupRoleTherapist(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_ROLE_THERAPIST);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }


    /**
     * Oncologis Group Prefix
     * @return
     */
    public static String queryGroupOncologistPrefix(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_ONCOLOGIST_PREFIX);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * Nurse Group Prefix
     * @return
     */
    public static String queryGroupNursePrefix(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_NURSE_PREFIX);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * GroupPhysicistPrefix Group Prefix
     * @return
     */
    public static String queryGroupPhysicistPrefix(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_PHYSICIST_PREFIX);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    /**
     * Technician Group Prefix
     * @return
     */
    public static String queryGroupTechnicianPrefix(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.GROUP_TECHNICIAN_PREFIX);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    public static String queryPhysicistGroupingActivityCode(){
        List<String> list = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.PHYSICIST_GROUPING_ACTIVITY_CODE);
        if(!list.isEmpty()){
            return list.get(0);
        }
        return null;
    }
}
