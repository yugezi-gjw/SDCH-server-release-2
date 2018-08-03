package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

/**
 * Created by gbt1220 on 6/8/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ActivityInstanceForOrderAssembler.class, PatientEncounterHelper.class})
public class ActivityInstanceForOrderAssemblerTest {

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;

    private Configuration configuration;

    private ActivityInstanceAssembled assembler;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    private EncounterServiceImp encounterServiceImp;

    @Before
    public void setup() throws Exception {
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(coverageAntiCorruptionServiceImp);
        configuration = PowerMockito.mock(Configuration.class);
        confirmPaymentServiceImp = PowerMockito.mock(ConfirmPaymentServiceImp.class);
        PowerMockito.whenNew(ConfirmPaymentServiceImp.class).withAnyArguments().thenReturn(confirmPaymentServiceImp);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
        PowerMockito.mockStatic(PatientEncounterHelper.class);
    }

    @Test
    public void givenOrderDtoListWhenGetThenReturnInstanceList() {
        List<OrderDto> orderDtoList = MockDtoUtil.givenOrderList();
        Map<String, List<CarePathInstance>> carePathInstanceMap = givenCarePathInstanceMap();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(Arrays.asList("1"))).thenReturn(carePathInstanceMap);
        Map<String, PatientDto> patientDtoMap = givenAPatientMap();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(Arrays.asList("1"))).thenReturn(patientDtoMap);
        final List<String> hisIdList = givenHisIdList();
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap<String, String>());
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList)).thenReturn(confirmedPaymentMap);
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(new ArrayList());
        }};
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);

        assembler = new ActivityInstanceForOrderAssembler(orderDtoList, configuration, new UserContext());
        Assert.assertEquals(1, assembler.getActivityInstances().size());
    }

    @Test
    public void givenOrderDtoListWhenTheOrderIsNotTheFirstInstanceInCarePathThenReturnInstanceList() {
        List<OrderDto> orderDtoList = givenAnOrderListOfNotTheFirstInstance();
        Map<String, List<CarePathInstance>> carePathInstanceMap = givenCarePathInstanceMap();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(Arrays.asList("1"))).thenReturn(carePathInstanceMap);
        Map<String, PatientDto> patientDtoMap = givenAPatientMap();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(Arrays.asList("1"))).thenReturn(patientDtoMap);
        final List<String> hisIdList = givenHisIdList();
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap<String, String>());
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList)).thenReturn(confirmedPaymentMap);
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(new ArrayList());
        }};
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("12121");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(111L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCpInstanceId(2L);
                    setEncounterId(111L);
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setCrtTime(new Date());
                    setCrtUser("SysAdmin");
                }}));
            }});
        }});
        assembler = new ActivityInstanceForOrderAssembler(orderDtoList, configuration, new UserContext());
        Assert.assertEquals(1, assembler.getActivityInstances().size());
    }

    private List<OrderDto> givenAnOrderListOfNotTheFirstInstance() {
        List<OrderDto> orderDtoList = MockDtoUtil.givenOrderList();
        orderDtoList.get(0).setOrderId("2");
        return orderDtoList;
    }

    private Map<String, List<CarePathInstance>> givenCarePathInstanceMap() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        instance.getActivityInstances().get(1).setInstanceID("2");
        HashMap<String, List<CarePathInstance>> result = new HashMap<>();
        result.put("1", Arrays.asList(instance));
        return result;
    }

    private Map<String, PatientDto> givenAPatientMap() {
        PatientDto patientDto = MockDtoUtil.givenAPatient();
        Map<String, PatientDto> result = new HashMap<>();
        result.put("1", patientDto);
        return result;
    }

    private List<String> givenHisIdList() {
        final List<String> hisIdList = new ArrayList<>();
        hisIdList.add("hisId");
        return hisIdList;
    }

    private Map<String, Boolean> givenConfirmedPaymentMap() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("hisId", Boolean.TRUE);
        return map;
    }
}
