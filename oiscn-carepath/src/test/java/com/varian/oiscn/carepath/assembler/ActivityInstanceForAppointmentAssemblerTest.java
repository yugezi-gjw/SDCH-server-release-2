package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
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
@PrepareForTest({ActivityInstanceForAppointmentAssembler.class})
public class ActivityInstanceForAppointmentAssemblerTest {

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;
    private ActivityInstanceAssembled assembler;
    private Configuration configuration;
    private ConfirmPaymentServiceImp confirmPaymentServiceImp;
    private EncounterServiceImp encounterServiceImp;

    @Before
    public void setup() throws Exception {
        patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        coverageAntiCorruptionServiceImp = PowerMockito.mock(CoverageAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(CoverageAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(coverageAntiCorruptionServiceImp);
        configuration = PowerMockito.mock(Configuration.class);
        confirmPaymentServiceImp = PowerMockito.mock(ConfirmPaymentServiceImp.class);
        PowerMockito.whenNew(ConfirmPaymentServiceImp.class).withAnyArguments().thenReturn(confirmPaymentServiceImp);
        encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
        PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);
    }

    @Test
    public void givenAppointmentDtoListWhenGetThenReturnInstanceList() {
        List<AppointmentDto> appointmentDtoList = MockDtoUtil.givenAppointmentListDto();
        Map<String, PatientDto> patientDtoMap = givenAPatientMap();
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(Arrays.asList("1"))).thenReturn(patientDtoMap);
        final List<String> hisIdList = givenHisIdList();
        Map<String, Boolean> confirmedPaymentMap = givenConfirmedPaymentMap();
        PowerMockito.when(encounterServiceImp.queryPhysicianCommentsByHisIdList(hisIdList)).thenReturn(new HashMap());
        PowerMockito.when(confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList, "DoCTSim")).thenReturn(confirmedPaymentMap);
        Pagination<CoverageDto> pagination = new Pagination<CoverageDto>(){{
            setLstObject(new ArrayList());
        }};
        PowerMockito.when(coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(), Matchers.anyInt())).thenReturn(pagination);
        assembler = new ActivityInstanceForAppointmentAssembler(appointmentDtoList, configuration, new UserContext());
        Assert.assertEquals(1, assembler.getActivityInstances().size());
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
