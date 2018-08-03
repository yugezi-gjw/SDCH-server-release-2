package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import org.apache.commons.lang3.StringUtils;
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
@PrepareForTest({ActivityInstanceForPatientAssembler.class, PatientEncounterHelper.class})
public class ActivityInstanceForPatientAssemblerTest {

    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;

    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;

    private ActivityInstanceAssembled assembler;

    private Configuration configuration;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    private EncounterServiceImp encounterServiceImp;

    @Before
    public void setup() throws Exception {
        carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withNoArguments().thenReturn(carePathAntiCorruptionServiceImp);
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
    public void givenPatientDtoListWhenCarePathInstanceIsNullThenInstanceIdIsNull() {
        List<PatientDto> patientDtoList = MockDtoUtil.givenAPatientList();
        PatientDto.PatientLabel label = new PatientDto.PatientLabel();
        label.setLabelId("labelId");
        label.setLabelTag(configuration.getAlertPatientLabelDesc());
        label.setLabelText("labelText");
        patientDtoList.get(0).addPatientLabel(label);
        final List<String> patientIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> patientIdList.add(patientDto.getPatientSer()));
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList)).thenReturn(new HashMap());

        final List<String> hisIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> hisIdList.add(patientDto.getHisId()));
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList)).thenReturn(confirmedPaymentMap);
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap<String, String>());
        CoverageDto coverageDto = new CoverageDto("patientId", "insuranceTypeCode", "insuranceTypeDesc");
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(Arrays.asList(coverageDto));
        }};

        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);
        assembler = new ActivityInstanceForPatientAssembler(patientDtoList, Arrays.asList("physicianGroupId"), configuration, new UserContext());
        List<ActivityInstanceVO> activityInstances = assembler.getActivityInstances();
        Assert.assertNull(activityInstances.get(0).getInstanceId());
    }

    @Test
    public void givenPatientDtoListWhenTheFirstActivityAvailableThenReturnTheActivityInstance() {
        List<PatientDto> patientDtoList = MockDtoUtil.givenAPatientList();
        final List<String> patientIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> patientIdList.add(patientDto.getPatientSer()));
        Map<String, List<CarePathInstance>> instance = givenCarePathInstanceMapOfTheFirstActivityAvailable();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList)).thenReturn(instance);

        final List<String> hisIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> hisIdList.add(patientDto.getHisId()));
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList)).thenReturn(confirmedPaymentMap);
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap<String, String>());
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(new ArrayList());
        }};
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("12121");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(111L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCpInstanceId(1L);
                    setEncounterId(111L);
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setCrtTime(new Date());
                    setCrtUser("SysAdmin");
                }}));
            }});
        }});
        assembler = new ActivityInstanceForPatientAssembler(patientDtoList, Arrays.asList("1"), configuration, new UserContext());
        List<ActivityInstanceVO> activityInstances = assembler.getActivityInstances();
        ActivityInstance theFirstActivityInstance = instance.get("patientSer").get(0).getActivityInstances().get(0);
        Assert.assertEquals(theFirstActivityInstance.getInstanceID(), activityInstances.get(0).getInstanceId());
        Assert.assertNotEquals(StringUtils.EMPTY, activityInstances.get(0).getNextAction());
        assembler = new ActivityInstanceForPatientAssembler(patientDtoList, Arrays.asList("otherGroupId"), configuration, new UserContext());
        activityInstances = assembler.getActivityInstances();
        Assert.assertEquals(StringUtils.EMPTY, activityInstances.get(0).getNextAction());
    }

    @Test
    public void givenPatientDtoListWhenTwoActivitiesAvailableThenReturnTheActivityInstance() {
        List<PatientDto> patientDtoList = MockDtoUtil.givenAPatientList();
        final List<String> patientIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> patientIdList.add(patientDto.getPatientSer()));
        Map<String, List<CarePathInstance>> instance = givenCarePathInstanceMapOfTwoActiveActivities();
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryCarePathListByPatientIDList(patientIdList)).thenReturn(instance);

        final List<String> hisIdList = new ArrayList<>();
        patientDtoList.forEach(patientDto -> hisIdList.add(patientDto.getHisId()));
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList)).thenReturn(confirmedPaymentMap);
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap<String, String>());
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(new ArrayList());
        }};
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("12121");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(111L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCpInstanceId(1L);
                    setEncounterId(111L);
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setCrtTime(new Date());
                    setCrtUser("SysAdmin");
                }}));
            }});
        }});
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);
        assembler = new ActivityInstanceForPatientAssembler(patientDtoList, Arrays.asList("1"), configuration, new UserContext());
        List<ActivityInstanceVO> activityInstances = assembler.getActivityInstances();
        Assert.assertNotEquals(StringUtils.EMPTY, activityInstances.get(0).getNextAction());
        assembler = new ActivityInstanceForPatientAssembler(patientDtoList, Arrays.asList("otherGroupId"), configuration, new UserContext());
        activityInstances = assembler.getActivityInstances();
        Assert.assertEquals(StringUtils.EMPTY, activityInstances.get(0).getNextAction());
    }

    private Map<String, List<CarePathInstance>> givenCarePathInstanceMapOfTheFirstActivityAvailable() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        HashMap<String, List<CarePathInstance>> result = new HashMap<>();
        result.put("patientSer", Arrays.asList(instance));
        return result;
    }

    private Map<String, List<CarePathInstance>> givenCarePathInstanceMapOfTwoActiveActivities() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        instance.getActivityInstances().get(0).setStatus(CarePathStatusEnum.COMPLETED);
        instance.getActivityInstances().get(0).setLastModifiedDT(new Date());
        instance.getActivityInstances().get(0).setIsActiveInWorkflow(false);
        instance.getActivityInstances().get(1).setStatus(CarePathStatusEnum.ACTIVE);
        instance.getActivityInstances().get(1).setIsActiveInWorkflow(true);
        instance.getActivityInstances().get(2).setStatus(CarePathStatusEnum.ACTIVE);
        HashMap<String, List<CarePathInstance>> result = new HashMap<>();
        result.put("patientSer", Arrays.asList(instance));
        return result;
    }

    private Map<String, Boolean> givenConfirmedPaymentMap() {
        Map<String, Boolean> map = new HashMap<>();
        map.put("hisId", Boolean.TRUE);
        return map;
    }
}
