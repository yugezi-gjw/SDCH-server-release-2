package com.varian.oiscn.cache;

import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppointmentCache.class,CacheFactory.class})
public class AppointmentCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testPutGet() {
		AppointmentDto dto = new AppointmentDto();
		dto.setAppointmentId("appointmentId");
		dto.setStartTime(new Date());
		List<ParticipantDto> participants = new ArrayList<>();
		ParticipantDto device = new ParticipantDto();
		device.setParticipantId("deviceId");
		device.setType(ParticipantTypeEnum.DEVICE);
		participants.add(device);
		ParticipantDto xxx = new ParticipantDto();
		xxx.setParticipantId("participantId");
		participants.add(xxx);
		dto.setParticipants(participants);
		
		AppointmentCache.put(dto);
		AppointmentCache.put(dto);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = dateFormat.format(new Date());
		Assert.assertNotNull(AppointmentCache.get("deviceId", date));
		
		AppointmentCache.remove("deviceId", date);
		Assert.assertEquals(0, AppointmentCache.allKeys().size());
	}
	@Test
	public void testRemoveByAppointmentDto(){
		AppointmentDto dto = new AppointmentDto(){{
			setAppointmentId("1111");
			setStartTime(new Date());
			setParticipants(Arrays.asList(new ParticipantDto(){{
				setParticipantId("1212");
				setType(ParticipantTypeEnum.DEVICE);
			}}));
		}};
		CacheInterface<String, List<AppointmentDto>> cache = PowerMockito.mock(ConcurrentHashMapCacheImpl.class);
		PowerMockito.mockStatic(CacheFactory.class);
		PowerMockito.when(CacheFactory.getCache(CacheFactory.APPOINTMENT)).thenReturn(cache);
		List<AppointmentDto> appointmentDtoList = new ArrayList<AppointmentDto>(){{
			add(dto);
		}};
		PowerMockito.when(cache.get(Matchers.anyString())).thenReturn(appointmentDtoList);
		AppointmentCache.remove(dto);
//		Assert.assertTrue(appointmentDtoList.isEmpty());
	}
}
