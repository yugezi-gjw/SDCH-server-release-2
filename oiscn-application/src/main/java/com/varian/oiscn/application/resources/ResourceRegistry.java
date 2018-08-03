package com.varian.oiscn.application.resources;

import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.anticorruption.fhircontext.FHIRLoggingInterceptor;
import com.varian.oiscn.anticorruption.fhircontext.HttpClientContextFactory;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRInterface;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.application.security.TokenAuthenticator;
import com.varian.oiscn.appointment.calling.CallingService;
import com.varian.oiscn.appointment.calling.CallingServiceException;
import com.varian.oiscn.appointment.resource.AppointmentResource;
import com.varian.oiscn.appointment.resource.CallingWaitingResource;
import com.varian.oiscn.appointment.service.RefreshAppointmentCacheService;
import com.varian.oiscn.base.applicationlanuch.ApplicationLaunchResource;
import com.varian.oiscn.base.cache.CacheManager;
import com.varian.oiscn.base.cache.CacheManagerPool;
import com.varian.oiscn.base.cache.CacheNameConstants;
import com.varian.oiscn.base.codesystem.CodeSystemPool;
import com.varian.oiscn.base.common.LocaleParam;
import com.varian.oiscn.base.coverage.PayorInfoResource;
import com.varian.oiscn.base.device.DeviceResource;
import com.varian.oiscn.base.diagnosis.DiagnosisResource;
import com.varian.oiscn.base.dynamicform.DynamicFormTemplateResource;
import com.varian.oiscn.base.extend.ImplementationExtensionService;
import com.varian.oiscn.base.group.GroupResource;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.practitioner.PractitionerResource;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.systemconfig.SystemConfigResource;
import com.varian.oiscn.base.systemconfig.SystemConfigServiceImp;
import com.varian.oiscn.base.tasklocking.TaskLockingServiceImpl;
import com.varian.oiscn.base.user.AuthenticationCache;
import com.varian.oiscn.base.user.AuthenticationResource;
import com.varian.oiscn.base.user.PermissionService;
import com.varian.oiscn.base.user.UserResource;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.cache.DeviceCache;
import com.varian.oiscn.carepath.resource.ActivityResource;
import com.varian.oiscn.carepath.resource.CarePathResource;
import com.varian.oiscn.carepath.service.CarePathConfigService;
import com.varian.oiscn.carepath.service.LinkCPInDynamicFormConfigService;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.TokenAuthenticationMode;
import com.varian.oiscn.connection.ConnectionParam;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.hipaa.HipaaEventResource;
import com.varian.oiscn.core.hipaa.config.AuditLogConfig;
import com.varian.oiscn.core.hipaa.log.AuditLogService;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.User;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.resource.EncounterResource;
import com.varian.oiscn.encounter.resource.SetupPhotoResource;
import com.varian.oiscn.order.resource.OrderResource;
import com.varian.oiscn.patient.registry.PatientRegistryResource;
import com.varian.oiscn.patient.resource.PatientResource;
import com.varian.oiscn.patient.service.RefreshPatientCacheService;
import com.varian.oiscn.resource.CommonResource;
import com.varian.oiscn.rt.TreatmentSummaryResource;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 12/30/2016.
 */
@Slf4j
public class ResourceRegistry {

    public static final String VARIAN_CN = "variancn";
    public static final String VARIAN_CN_PWD = "V@rian01";
    public static final String CONFIG_IMPLEMENTATION_EXTEND_YAML = "config\\ImplementationExtend.yaml";
    public static final String CONFIG_HISINTEGRATION_YAML = "config\\integration\\HisSystem.yaml";
    public static final String CONFIG_LINK_CAREPATH_YAML = "config\\LinkCPInDynamicForm.yaml";

    private Configuration configuration;

    private Environment environment;

    @Getter
    private static AuthenticationCache authenticationCache;

    private List<Object> resourceComponents = new ArrayList<>();

    /**
     * Constructor.<br>
     *
     * @param configuration Configuration
     * @param environment   Environment
     */
    public ResourceRegistry(Configuration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    /**
     * Initialization.<br>
     */
    public void initialize() {
        FHIRContextFactory.getInstance().setConfiguration(configuration);

        HttpClientContextFactory.getInstance().setEnvironmentAndConfiguration(environment, configuration);

        ConnectionParam.initParam(configuration);
        try {
            ConnectionPool.init();
        } catch (Exception e) {
            log.error("Connection Pool Initialization Error: {}", e.getMessage());
            // Connection error
            System.exit(-1);
        }

        SystemConfigPool.init(configuration);

        try {
            CarePathConfigService.init(configuration);
        } catch (Exception e) {
            log.error("CarePath Initialization Error: {}", e.getMessage());
        }

        try {
            ActivityCodesReader.init(configuration);
        } catch (Exception e) {
            log.error("ActivityCodesReader Initialization Error: {}", e.getMessage());
        }

        try {
            CallingService.init(configuration);
        } catch (CallingServiceException e) {
            log.error("CallingService Initialization Error: {}", e.getMessage());
            if (e.getBadItemList() != null) {
                log.error("Bad Item: {}", e.getBadItemList());
            }
        }

        try {
            LinkCPInDynamicFormConfigService.init(CONFIG_LINK_CAREPATH_YAML);
        } catch (Exception e) {
            log.warn("LinkCPInDynamicFormService init error: {}", e.getMessage());
        }

        ImplementationExtensionService.init(CONFIG_IMPLEMENTATION_EXTEND_YAML);
        HisPatientInfoConfigService.init(CONFIG_HISINTEGRATION_YAML);

        boolean isFHIRAvailable = FHIRInterface.isAvailabel(configuration.getFhirMetadataUri());
        if(!isFHIRAvailable) {
            log.error("Failed Fetch FHIR Metadata! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            System.exit(-1);
        }

        LocaleParam.initParam(configuration.getLocale());
        initCachePool();

        // FHIR Hipaa Logging Interceptor.
        FHIRContextFactory.getInstance().addInterceptor(new FHIRLoggingInterceptor());

        if (isFhirTokenAuthEnabled()) {
            UserAntiCorruptionServiceImp userAntiCorruptionServiceImp = new UserAntiCorruptionServiceImp(
                    configuration.getFhirServerBaseUri(),
                    configuration.getOspAuthenticationWsdlUrl(),
                    configuration.getOspAuthorizationWsdlUrl());
            User user = new User(VARIAN_CN, VARIAN_CN_PWD, "");
            Login login = userAntiCorruptionServiceImp.login(user);
            if (isNotEmpty(login.getToken())) {
                FHIRContextFactory.getInstance().registerAuthTokenInterceptor(new BearerTokenAuthInterceptor(login.getToken()));
            } else {
                // no token, authentication failed.
                throw new RuntimeException("FhirServer Authentication Failed !");
            }
        }
        CodeSystemPool.initStatusIcon();
        CodeSystemPool.initPatientLabel();
        CodeSystemPool.initDiagnosis(configuration.getDiagnosisCodeScheme(),
                configuration.getFhirServerConfiguration().getFhirLanguage());
        CodeSystemPool.initGroupPractitionerListMap();
        CodeSystemPool.initPayorInfos();
        CodeSystemPool.initBodyPart(configuration.getFhirServerConfiguration().getFhirLanguage());
        CodeSystemPool.initCarePath();
        CodeSystemPool.initPhysicistGroupInfoMap();
        initDevice();

        initAuditLogService();

        try {
            PermissionService.init(configuration);
        } catch (Exception e) {
            log.error("PermissionService Initialization Error: {}", e.getMessage());
        }

        authenticationCache = new AuthenticationCache(configuration.getDefaultTokenCacheTimeoutInMinutes());

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                new OspTokenValidateMonitorThread(authenticationCache, configuration),
                0,
                configuration.getOspTokenValidationInterval(),
                TimeUnit.MINUTES
        );

        // Diff token -> username within specified interval, and remove their resource locks.
        TaskLockingServiceImpl service = new TaskLockingServiceImpl(null);
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                new ResourceAutoUnLockThread(service, authenticationCache),
                1, // no need startup so quickly.
                configuration.getResourceAutoUnlockInMinutes(),
                TimeUnit.MINUTES
        );

        if (SystemConfigPool.getRefreshCacheFromFHIRInterval() > 0) {
            Runnable[] cacheInitializeList = new Runnable[]{new RefreshAppointmentCacheService(), new RefreshPatientCacheService()};
            Arrays.stream(cacheInitializeList).forEach(cacheInitialize -> Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(
                    cacheInitialize, 0, SystemConfigPool.getRefreshCacheFromFHIRInterval(), TimeUnit.MINUTES
            ));
        } else {
            log.warn("Cannot initialize the cache from FHIR. Please check the configuration [RefreshCacheFromFHIRInterval] in the database.");
        }

        int refreshInterval = 10;
        List<String> refreshTime = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.ACTIVITY_REFRESH_TIME);
        if (refreshTime != null && refreshTime.size() > 0) {
            try {
                refreshInterval = Integer.parseInt(refreshTime.get(0));
            } catch (NumberFormatException e) {
                log.warn("{} - Using Default Config ({}) for BAD Config: ({})", SystemConfigConstant.ACTIVITY_REFRESH_TIME, refreshInterval, refreshTime.get(0));
            }
        }
        if (refreshInterval > 0) {
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                    new TaskUpdateStatusThread(),
                    1, // no need startup so quickly.
                    refreshInterval,
                    TimeUnit.SECONDS
            );
        }

        ////因为anti project目前不能依赖base project，所以在这里初始化一下
        PatientIdMapper.init(SystemConfigPool.getPatientIdMapper(), SystemConfigPool.getPatientId2Mapper());

        addResourceComponents();
        registerComponents();
    }

    protected void initDevice() {
        DevicesReader.init();
        DeviceAntiCorruptionServiceImp deviceService = new DeviceAntiCorruptionServiceImp();
        List<DeviceDto> radiationDeviceList = deviceService.queryDeviceByType("RadiationDevice");
        radiationDeviceList.forEach(fhirDto -> {
            DeviceCache.put(fhirDto);
            log.info("radiationDevice [{}]", fhirDto.toString());
        });
    }

    private void initCachePool() {
        CacheManagerPool.getInstance().putToCachePool(CacheNameConstants.SETUP_PHOTO,
                new CacheManager(CacheNameConstants.SETUP_PHOTO));
    }

    private void initAuditLogService() {
        AuditLogConfig config = new AuditLogConfig();
        SystemConfigServiceImp service = new SystemConfigServiceImp();
        Map<String, String> confMap = service.getAuditLog();
        // Initialization with default value
        config.setHostName(SystemConfigPool.initStringConf(confMap, "hostName", "localhost"));
        config.setPort(SystemConfigPool.initIntegerConf(confMap, "port", 55020));
        config.setTimeoutInMs(SystemConfigPool.initIntegerConf(confMap, "timeoutInMs", 300));
        config.setLogThreadCount(SystemConfigPool.initIntegerConf(confMap, "logThreadCount", 1));
        config.setLogBatchSize(SystemConfigPool.initIntegerConf(confMap, "logBatchSize", 100));

        AuditLogService logService = new AuditLogService(config);
        logService.startLogThread();
    }

    private boolean isFhirTokenAuthEnabled() {
        return TokenAuthenticationMode.BEAR.equals(TokenAuthenticationMode.fromCode(configuration.getFhirTokenAuthEnabled()));
    }

    private void addResourceComponents() {
        resourceComponents.add(new AuthDynamicFeature(new OAuthCredentialAuthFilter.Builder<>()
                .setAuthenticator(new TokenAuthenticator(authenticationCache))
                .setPrefix("Bearer")
                .buildAuthFilter()));
        //If you want to use @Auth to inject a custom Principal type into your resource
        resourceComponents.add(new AuthValueFactoryProvider.Binder<>(UserContext.class));

        resourceComponents.add(new AuthenticationResource(configuration, environment, authenticationCache));
        resourceComponents.add(new PatientResource(configuration, environment));
        resourceComponents.add(new GroupResource(configuration, environment));
        resourceComponents.add(new PractitionerResource(configuration, environment));
        resourceComponents.add(new OrderResource(configuration, environment));
        resourceComponents.add(new AppointmentResource(configuration, environment));
        resourceComponents.add(new CarePathResource(configuration, environment));
        resourceComponents.add(new ActivityResource(configuration, environment));
        resourceComponents.add(new DeviceResource(configuration, environment));
        resourceComponents.add(new EncounterResource(configuration, environment));
        resourceComponents.add(new DiagnosisResource(configuration, environment));
        resourceComponents.add(new DynamicFormTemplateResource(configuration, environment));
        resourceComponents.add(new TreatmentSummaryResource(configuration, environment));
        resourceComponents.add(new ApplicationLaunchResource(configuration, environment));
        resourceComponents.add(new SystemConfigResource(configuration, environment));
        resourceComponents.add(new CallingWaitingResource(configuration, environment));
        resourceComponents.add(new PayorInfoResource(configuration, environment));
        resourceComponents.add(new CommonResource(configuration, environment));
        resourceComponents.add(new UserResource(configuration, environment));
        resourceComponents.add(new SetupPhotoResource(configuration, environment));
        resourceComponents.add(new HipaaEventResource(configuration, environment));
        resourceComponents.add(new PatientRegistryResource(configuration, environment));
    }

    private void registerComponents() {
        for (Object obj : resourceComponents) {
            environment.jersey().register(obj);
        }
    }
}