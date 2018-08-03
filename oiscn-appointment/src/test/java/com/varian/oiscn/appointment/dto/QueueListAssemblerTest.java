package com.varian.oiscn.appointment.dto;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.vo.QueueListVO;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
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
 * Created by gbt1220 on 11/22/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({QueueListAssembler.class,StatusIconPool.class})
public class QueueListAssemblerTest {
    private QueueListAssembler queueListAssembler;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    private EncounterServiceImp encounterServiceImp;
    @Before
    public void setup(){
        try {
            patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
            flagAntiCorruptionServiceImp = PowerMockito.mock(FlagAntiCorruptionServiceImp.class);
            appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
            confirmPaymentServiceImp = PowerMockito.mock(ConfirmPaymentServiceImp.class);
            encounterServiceImp = PowerMockito.mock(EncounterServiceImp.class);
            PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(patientAntiCorruptionServiceImp);
            PowerMockito.whenNew(FlagAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(flagAntiCorruptionServiceImp);
            PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(appointmentAntiCorruptionServiceImp);
            PowerMockito.whenNew(ConfirmPaymentServiceImp.class).withAnyArguments().thenReturn(confirmPaymentServiceImp);
            PowerMockito.whenNew(EncounterServiceImp.class).withAnyArguments().thenReturn(encounterServiceImp);

            List<QueuingManagementVO> queue = Arrays.asList(new QueuingManagementVO(){{
                setAppointmentId("121");
                setDeviceId("3444");
                setActivityCode("DoCT");
                setHisId("H20180314001");
                setPatientSer(1213L);
                setStartTime(new Date());
                setCheckInStatus(CheckInStatusEnum.CALLING);
            }});
            UserContext userContext = PowerMockito.mock(UserContext.class);
            Configuration configuration = PowerMockito.mock(Configuration.class);
            PowerMockito.when( configuration.getAlertPatientLabelDesc()).thenReturn("alertflag");
            Map<String, PatientDto>  map = new HashMap<>();
            map.put(queue.get(0).getPatientSer().toString(),new PatientDto(){{
                setPatientSer(queue.get(0).getPatientSer().toString());
                setHisId(queue.get(0).getHisId());
            }});
            PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(org.mockito.Matchers.anyList())).thenReturn(map);
            PowerMockito.when(configuration.getUrgentStatusIconDesc()).thenReturn("urgent");
            PowerMockito.mockStatic(StatusIconPool.class);
            PowerMockito.when(StatusIconPool.get("urgent")).thenReturn("dsafsad");
            Map<String,Boolean> urgentMap = new HashMap<>();
            urgentMap.put(queue.get(0).getPatientSer().toString(),true);
            PowerMockito.when(flagAntiCorruptionServiceImp.queryPatientListFlag(Matchers.anyList(),Matchers.anyString())).thenReturn(urgentMap);
            AppointmentDto appointmentDto = new AppointmentDto(){{
               setAppointmentId(queue.get(0).getAppointmentId());
               setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
            }};
            PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentById(queue.get(0).getAppointmentId())).thenReturn(appointmentDto);
            Map<String,Boolean> confirmMap = new HashMap<>();
            confirmMap.put(queue.get(0).getPatientSer().toString(),false);
            PowerMockito.when(confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(Matchers.anyList(),Matchers.anyString(),Matchers.anyString())).thenReturn(confirmMap);
            queueListAssembler = new QueueListAssembler(queue,configuration,userContext);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetQueue(){
        List<QueueListVO> list = queueListAssembler.getQueue();
        Assert.assertNotNull(list);
    }

}
