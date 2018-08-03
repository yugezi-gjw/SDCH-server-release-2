package com.varian.oiscn.carepath.service;


import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.carepath.vo.AppointmentFormDataVO;
import com.varian.oiscn.carepath.vo.AppointmentFormTimeDataVO;
import com.varian.oiscn.core.activity.ActivityCodeEnum;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 2/1/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ActivityServiceImp.class, SystemConfigPool.class})
public class ActivityServiceImpTest {

    private ActivityServiceImp activityServiceImp;

    private TreatmentAppointmentService treatmentAppointmentService;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;


    @Before
    public void setup() throws Exception {
        treatmentAppointmentService = PowerMockito.mock(TreatmentAppointmentService.class);
        PowerMockito.whenNew(TreatmentAppointmentService.class).withAnyArguments().thenReturn(treatmentAppointmentService);
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withNoArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        PowerMockito.mockStatic(SystemConfigPool.class);
        activityServiceImp = new ActivityServiceImp(new UserContext());
    }

    @Test
    public void testCheckMultiAppointmentsConflict() throws Exception {
        AppointmentFormDataVO vo = givenAppointmentFormData();
        String deviceId = "deviceId";
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        Pagination<AppointmentDto> pagination = new Pagination<>();
        pagination.setLstObject(appointmentDtoList);
        int size = vo.getAppointTimeList().size();
        String startDate = vo.getAppointTimeList().get(0).getStartTime().substring(0, vo.getAppointTimeList().get(0).getStartTime().indexOf(' '));
        String endDate = vo.getAppointTimeList().get(size - 1).getStartTime().substring(0, vo.getAppointTimeList().get(0).getStartTime().indexOf(' '));
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(Arrays.asList(deviceId), startDate,
                endDate, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED),
                        AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), Integer.MAX_VALUE, 1, Integer.MAX_VALUE)).thenReturn(pagination);
        PowerMockito.when(SystemConfigPool.queryTimeSlotCount()).thenReturn("3");
        List<AppointmentFormTimeDataVO> failToScheduleList = activityServiceImp.checkMultiAppointmentsConflict(vo);
        Assert.assertNotNull(failToScheduleList);
    }
    private AppointmentFormDataVO givenAppointmentFormData() {
        AppointmentFormDataVO rvo = new AppointmentFormDataVO();
        rvo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        rvo.setDeviceId("1106");
        rvo.setAppointTimeList(Arrays.asList(new AppointmentFormTimeDataVO("", "2018-02-05 10:30", "2018-02-05 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-06 10:30", "2018-02-06 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-07 10:30", "2018-02-07 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-08 10:30", "2018-02-08 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-09 10:30", "2018-02-09 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-10 10:30", "2018-02-10 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-11 10:30", "2018-02-11 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-12 10:30", "2018-02-12 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-13 10:30", "2018-02-13 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-14 10:30", "2018-02-14 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-15 10:30", "2018-02-15 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-16 10:30", "2018-02-16 10:45", 0),
                new AppointmentFormTimeDataVO("", "2018-02-17 10:30", "2018-02-17 10:45", 1)));
        rvo.setPatientSer(1202L);
        return rvo;
    }

    private List<AppointmentFormTimeDataVO> givenAppointmentFormTimeDateList() {
        List<AppointmentFormTimeDataVO> vos = new ArrayList<>();
        AppointmentFormTimeDataVO vo1 = new AppointmentFormTimeDataVO("", "2017-05-12 11:00:00", "2017-05-12 11:20:00",0);
        AppointmentFormTimeDataVO vo2 = new AppointmentFormTimeDataVO("", "2017-05-13 11:00:00", "2017-05-13 11:20:00",0);
        vos.add(vo1);
        vos.add(vo2);
        return vos;
    }

    private AppointmentDto givenAnAppointment() {
        AppointmentDto dto = new AppointmentDto();
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            dto.setStartTime(simpleDateFormat.parse("2018-02-05 10:30"));
            dto.setEndTime(simpleDateFormat.parse("2018-02-05 10:45"));
        } catch (ParseException e){

        }

        dto.setEndTime(new Date());
        dto.setOrderId("orderId");
        dto.setComment("");
        dto.setReason(ActivityCodeEnum.getDisplay(ActivityCodeEnum.IMMOBILIZATION_APPOINTMENT));
        dto.setStatus("");
        dto.setParticipants(new ArrayList<>());
        return dto;
    }

    private List<AppointmentDto> givenAnAppointmentList() {
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        AppointmentDto dto = givenAnAppointment();
        String participantId = "participantId";
        dto.getParticipants().add(new ParticipantDto(ParticipantTypeEnum.PATIENT, participantId));
        appointmentDtoList.add(dto);
        return appointmentDtoList;
    }

    private Map<String, List<AppointmentDto>> givenAppointmentDtoMap() {
        Map<String, List<AppointmentDto>> map = new HashMap<>();
        List<AppointmentDto> appointmentDtoList = givenAnAppointmentList();
        map.put(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm"), appointmentDtoList);
        return map;
    }

}
