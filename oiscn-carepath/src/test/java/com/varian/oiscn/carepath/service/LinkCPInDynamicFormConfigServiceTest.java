package com.varian.oiscn.carepath.service;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.OrderAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathServiceImpl;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.mockito.Matchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LinkCPInDynamicFormConfigService.class, SystemConfigPool.class, PatientEncounterHelper.class})
public class LinkCPInDynamicFormConfigServiceTest {

    private String configFile = "..\\config\\LinkCPInDynamicForm.yaml";

    @Test
    public void testInit() {
        LinkCPInDynamicFormConfigService.init(configFile);
        Assert.assertNotNull(LinkCPInDynamicFormConfigService.getItem("PlaceImmobilizationAndCTOrder"));
    }

    @Test
    public void testLinkOptionalCP() throws Exception {
        LinkCPInDynamicFormConfigService.init(configFile);

        UserContext userContext = MockDtoUtil.givenUserContext();


        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = PowerMockito.mock(DynamicFormInstanceServiceImp.class);
        PowerMockito.whenNew(DynamicFormInstanceServiceImp.class).withAnyArguments().thenReturn(dynamicFormInstanceServiceImp);
        PowerMockito.when(dynamicFormInstanceServiceImp.queryFieldValueByPatientSerListAndFieldName(anyString(), anyString())).thenReturn("true");
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryDefaultDepartment()).thenReturn("1");
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        PowerMockito.when(carePathAntiCorruptionServiceImp.linkCarePath(anyString(), anyString(), anyString())).thenReturn("1");
        EncounterServiceImp encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        CarePathInstance carePathInstance = MockDtoUtil.givenACarePathInstance();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathInstanceByInstanceId(anyString())).thenReturn(carePathInstance);
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = PowerMockito.mock(OrderAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(OrderAntiCorruptionServiceImp.class).withNoArguments().thenReturn(orderAntiCorruptionServiceImp);
        OrderDto orderDto = MockDtoUtil.givenAnOrderDto();
        orderDto.setDueDate(new Date());
        PowerMockito.when(orderAntiCorruptionServiceImp.queryOrderById(anyString())).thenReturn(orderDto);
        PowerMockito.when(orderAntiCorruptionServiceImp.updateOrder(anyObject())).thenReturn("1");

        EncounterCarePathServiceImpl encounterCarePathService = PowerMockito.mock(EncounterCarePathServiceImpl.class);
        PowerMockito.whenNew(EncounterCarePathServiceImpl.class).withAnyArguments().thenReturn(encounterCarePathService);
        Encounter encounter = MockDtoUtil.givenAnEncounter();
        PowerMockito.when(encounterServiceImp.queryByPatientSer(anyLong())).thenReturn(encounter);
        PowerMockito.when(encounterCarePathService.addEncounterCarePath(anyObject())).thenReturn(true);

        PowerMockito.mockStatic(PatientEncounterHelper.class);

        LinkCPInDynamicFormConfigService.linkOptionalCP(userContext, "PlaceImmobilizationAndCTOrder", 1L);
    }
}

