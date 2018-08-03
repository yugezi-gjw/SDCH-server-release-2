package com.varian.oiscn.appointment.service;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.cache.AppointmentCache;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentCache.class, DevicesReader.class, RefreshAppointmentCacheService.class, DateUtil.class})
public class RefreshAppointmentCacheServiceTest {

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception{
        PowerMockito.mockStatic(RefreshAppointmentCacheService.class);
        PowerMockito.mockStatic(DevicesReader.class);
        PowerMockito.mockStatic(AppointmentCache.class);
        PowerMockito.mockStatic(DateUtil.class);
        appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(appointmentAntiCorruptionServiceImp);
        deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(deviceAntiCorruptionServiceImp);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRunMethod(){
        try{
            DeviceDto deviceDto = new DeviceDto();
            deviceDto.setId("deviceId");

            PowerMockito.when(DevicesReader.getAllDeviceDto()).thenReturn(Arrays.asList(deviceDto));
            PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByCode(anyString())).thenReturn(deviceDto);

            String dateString = "yyyy-mm-dd";
            PowerMockito.when(DateUtil.formatDate(anyObject(), eq(DateUtil.DATE_FORMAT))).thenReturn(dateString);

            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setParticipants(Arrays.asList(new ParticipantDto(){{
                setParticipantId("deviceId");
                setType(ParticipantTypeEnum.DEVICE);
            }}));
//            appointmentDto.setStartTime(startDate);
            List<String> allkeysList = Arrays.asList(deviceDto.getId()+":2018-02-03");
            PowerMockito.when(AppointmentCache.allKeys()).thenReturn(allkeysList);

            Pagination<AppointmentDto> pagination = new Pagination<>();
            pagination.setLstObject(Arrays.asList(appointmentDto));
            pagination.setTotalCount(1);
            PowerMockito.when(appointmentAntiCorruptionServiceImp.syncAppointmentListByDeviceIdAndDateRangeAndPagination(Arrays.asList("deviceId"), dateString, dateString,
                    Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), Integer.MAX_VALUE, 1, Integer.MAX_VALUE))
                    .thenReturn(pagination);
            PowerMockito.when(appointmentAntiCorruptionServiceImp.syncAppointmentListByDeviceIdAndDateRangeAndPagination(Arrays.asList("deviceId"), dateString, null,
                    Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED),AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)),Integer.MAX_VALUE,1,Integer.MAX_VALUE))
                    .thenReturn(pagination);
            RefreshAppointmentCacheService refreshAppointmentCacheService = new RefreshAppointmentCacheService();
            refreshAppointmentCacheService.run();
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
}
