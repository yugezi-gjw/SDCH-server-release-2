/**
 * 
 */
package com.varian.oiscn.application.resources;

import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.anticorruption.fhircontext.HttpClientContextFactory;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRInterface;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.UserAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.calling.CallingService;
import com.varian.oiscn.base.cache.CacheManagerPool;
import com.varian.oiscn.base.codesystem.CodeSystemPool;
import com.varian.oiscn.base.common.LocaleParam;
import com.varian.oiscn.base.extend.ImplementationExtensionService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.systemconfig.SystemConfigServiceImp;
import com.varian.oiscn.base.user.PermissionService;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.cache.DeviceCache;
import com.varian.oiscn.carepath.service.CarePathConfigService;
import com.varian.oiscn.carepath.service.LinkCPInDynamicFormConfigService;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.config.FhirServerConfiguration;
import com.varian.oiscn.connection.ConnectionParam;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.hipaa.log.AuditLogService;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.User;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Environment;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FHIRInterface.class, ConnectionPool.class, DriverManager.class, BasicDataSourceFactory.class, BasicDataSource.class, ResourceRegistry.class, User.class, UserAntiCorruptionServiceImp.class, FHIRContextFactory.class, HttpClientContextFactory.class, ConnectionParam.class, CarePathConfigService.class,
	CarePathConfigService.class, ActivityCodesReader.class, CallingService.class, DevicesReader.class, ImplementationExtensionService.class,
		HisPatientInfoConfigService.class, LocaleParam.class, SystemConfigPool.class, CodeSystemPool.class, PermissionService.class, PatientIdMapper.class, CacheManagerPool.class,
        AuditLogService.class, Executors.class, LinkCPInDynamicFormConfigService.class})
public class ResourceRegistryTest {
	
    private Configuration configuration;
    private Environment environment;
	private ResourceRegistry rr;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		configuration = PowerMockito.mock(Configuration.class);
        PowerMockito.when(configuration.getFhirTokenAuthEnabled()).thenReturn(1);
        FhirServerConfiguration fhirConf = PowerMockito.mock(FhirServerConfiguration.class);
        PowerMockito.when(fhirConf.getFhirLanguage()).thenReturn("fhirLang");
		PowerMockito.when(configuration.getFhirServerConfiguration()).thenReturn(fhirConf);
		PowerMockito.when(configuration.getOspTokenValidationInterval()).thenReturn(1);
		PowerMockito.when(configuration.getResourceAutoUnlockInMinutes()).thenReturn(1);
        
		environment = PowerMockito.mock(Environment.class);
		JerseyEnvironment jersey = PowerMockito.mock(JerseyEnvironment.class);
		PowerMockito.when(environment.jersey()).thenReturn(jersey);
		PowerMockito.doNothing().when(jersey).register(Matchers.any());
	}

	/**
	 * Test method for {@link com.varian.oiscn.application.resources.ResourceRegistry#ResourceRegistry(com.varian.oiscn.config.Configuration, io.dropwizard.setup.Environment)}.
	 */
	@Test
	public void testResourceRegistry() {
		rr = new ResourceRegistry(configuration, environment);
		Assert.assertNotNull(rr);
	}

	/**
	 * Test method for {@link com.varian.oiscn.application.resources.ResourceRegistry#initialize()}.
	 * @throws Exception 
	 */
	@Test
	public void testInitializeOK() throws Exception {
		PowerMockito.mockStatic(FHIRContextFactory.class);
		FHIRContextFactory fhirCtxFac = PowerMockito.mock(FHIRContextFactory.class);
		PowerMockito.doNothing().when(fhirCtxFac).setConfiguration(configuration);
		PowerMockito.when(FHIRContextFactory.getInstance()).thenReturn(fhirCtxFac);
		
		PowerMockito.mockStatic(HttpClientContextFactory.class);
		HttpClientContextFactory httpCtxFac = PowerMockito.mock(HttpClientContextFactory.class);
		PowerMockito.doNothing().when(httpCtxFac).setEnvironmentAndConfiguration(environment, configuration);
		PowerMockito.when(HttpClientContextFactory.getInstance()).thenReturn(httpCtxFac);
		
		PowerMockito.mockStatic(ConnectionParam.class);
		PowerMockito.doNothing().when(ConnectionParam.class, "initParam", Matchers.any());
		
        BasicDataSource basicDataSource = PowerMockito.mock(BasicDataSource.class);
        PowerMockito.mockStatic(BasicDataSourceFactory.class);
        PowerMockito.when(BasicDataSourceFactory.createDataSource(Mockito.any())).thenReturn(basicDataSource);
        
		PowerMockito.mockStatic(CarePathConfigService.class);
		PowerMockito.doNothing().when(CarePathConfigService.class, "init", Matchers.any());
		
		PowerMockito.mockStatic(ActivityCodesReader.class);
		PowerMockito.doNothing().when(ActivityCodesReader.class, "init", Matchers.any());

		PowerMockito.mockStatic(CallingService.class);
		PowerMockito.doNothing().when(CallingService.class, "init", Matchers.any());

        PowerMockito.mockStatic(LinkCPInDynamicFormConfigService.class);
        PowerMockito.doNothing().when(LinkCPInDynamicFormConfigService.class, "init", Matchers.any());

		PowerMockito.mockStatic(ImplementationExtensionService.class);
		PowerMockito.doNothing().when(ImplementationExtensionService.class, "init", Matchers.any());

		PowerMockito.mockStatic(HisPatientInfoConfigService.class);
		PowerMockito.doNothing().when(HisPatientInfoConfigService.class, "init", Matchers.any());

		PowerMockito.mockStatic(FHIRInterface.class);
        PowerMockito.when(FHIRInterface.isAvailabel(Mockito.anyString())).thenReturn(true);
        
		PowerMockito.mockStatic(LocaleParam.class);
		PowerMockito.doNothing().when(LocaleParam.class, "initParam", Matchers.any());

		PowerMockito.mockStatic(SystemConfigPool.class);
		PowerMockito.doNothing().when(SystemConfigPool.class, "init", Matchers.any());
		PowerMockito.when(SystemConfigPool.getRefreshCacheFromFHIRInterval()).thenReturn(2);
	
		PowerMockito.mockStatic(CodeSystemPool.class);
		PowerMockito.doNothing().when(CodeSystemPool.class, "initStatusIcon");
		PowerMockito.doNothing().when(CodeSystemPool.class, "initPatientLabel");
		PowerMockito.doNothing().when(CodeSystemPool.class, "initDiagnosis", Matchers.any(), Matchers.any());
		PowerMockito.doNothing().when(CodeSystemPool.class, "initGroupPractitionerListMap");
		PowerMockito.doNothing().when(CodeSystemPool.class, "initPayorInfos");
		PowerMockito.doNothing().when(CodeSystemPool.class, "initBodyPart", Matchers.any());
		PowerMockito.doNothing().when(CodeSystemPool.class, "initCarePath");
		PowerMockito.doNothing().when(CodeSystemPool.class, "initPhysicistGroupInfoMap");
		
		PowerMockito.mockStatic(DevicesReader.class);
		PowerMockito.doNothing().when(DevicesReader.class, "init");
		
		DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(deviceAntiCorruptionServiceImp);
        List<DeviceDto> radiationDeviceList = new ArrayList<>();
        DeviceDto dto = new DeviceDto();
        dto.setId("id01");
        dto.setCode("code01");
        dto.setName("name01");
        radiationDeviceList.add(dto);
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByType(Mockito.anyString())).thenReturn(radiationDeviceList);
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByCode(Mockito.anyString())).thenReturn(dto);
		PowerMockito.when(DevicesReader.getAllDeviceDto()).thenReturn(radiationDeviceList);
		
        SystemConfigServiceImp service = PowerMockito.mock(SystemConfigServiceImp.class);
        PowerMockito.whenNew(SystemConfigServiceImp.class).withAnyArguments().thenReturn(service);
		
        Map<String, String> confMap = new HashMap<>();
        PowerMockito.when(service.getAuditLog()).thenReturn(confMap);
        AuditLogService logService = PowerMockito.mock(AuditLogService.class);
        PowerMockito.whenNew(AuditLogService.class).withAnyArguments().thenReturn(logService);
        
		
		PowerMockito.mockStatic(PermissionService.class);
		PowerMockito.doNothing().when(PermissionService.class, "init", Matchers.any());
		
		PatientIdMapper.init(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID, PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID);
		
		PowerMockito.mockStatic(CacheManagerPool.class);
		CacheManagerPool cmPool = PowerMockito.mock(CacheManagerPool.class);
		PowerMockito.doNothing().when(cmPool).putToCachePool(Matchers.any(), Matchers.any());
		PowerMockito.when(CacheManagerPool.getInstance()).thenReturn(cmPool);
		
		AuditLogService alService = PowerMockito.mock(AuditLogService.class);
        PowerMockito.whenNew(AuditLogService.class).withAnyArguments().thenReturn(alService);
        PowerMockito.doNothing().when(alService).startLogThread();
        
        UserAntiCorruptionServiceImp userService = PowerMockito.mock(UserAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(UserAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(userService);
        
        User user = PowerMockito.mock(User.class);
        PowerMockito.whenNew(User.class).withAnyArguments().thenReturn(user);
        
        Login login = PowerMockito.mock(Login.class);
        PowerMockito.when(login.getToken()).thenReturn("token");
        
        PowerMockito.when(userService.login(Mockito.any())).thenReturn(login);
        
        PowerMockito.when(configuration.getFhirServerBaseUri()).thenReturn("getFhirServerBaseUri");
        PowerMockito.when(configuration.getOspAuthenticationWsdlUrl()).thenReturn("getOspAuthenticationWsdlUrl");
        PowerMockito.when(configuration.getOspAuthorizationWsdlUrl()).thenReturn("getOspAuthorizationWsdlUrl");
        
		rr = new ResourceRegistry(configuration, environment);
		rr.initialize();
		
		Assert.assertNotNull(DeviceCache.getByAriaCode("code01"));
		Assert.assertNotNull(ResourceRegistry.getAuthenticationCache());
	}
}
