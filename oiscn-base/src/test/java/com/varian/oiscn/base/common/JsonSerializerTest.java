package com.varian.oiscn.base.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

/**
 * Created by gbt1220 on 3/29/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(JsonSerializer.class)
public class JsonSerializerTest {

    private JsonSerializer<PatientDto> jsonSerializer;

    @Test
    public void givenAPatientDtoWhenGetJsonThenReturnJson() {
        jsonSerializer = new JsonSerializer<>();
        Assert.assertNotNull(jsonSerializer.getJson(giveAPatientDto()));
    }

    @Test
    public void givenAPatientDtoWhenGetJsonThrowExceptionThenReturnEmpty() throws Exception {
        PatientDto t = giveAPatientDto();
        ObjectMapper objectMapper = PowerMockito.mock(ObjectMapper.class);
        PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(objectMapper);
        PowerMockito.when(objectMapper.writeValueAsString(t)).thenThrow(JsonProcessingException.class);
        jsonSerializer = new JsonSerializer<>();
        Assert.assertEquals("", jsonSerializer.getJson(t));
    }

    @Test
    public void givenAJsonWhenGetObjectThenReturnPatient() {
        jsonSerializer = new JsonSerializer<>();
        Assert.assertNotNull(jsonSerializer.getObject(givenAJson(), PatientDto.class));
    }

    @Test
    public void givenAJsonWhenGetObjectThrowExceptionThenReturnEmpty() throws Exception {
        String t = givenAJson();
        ObjectMapper objectMapper = PowerMockito.mock(ObjectMapper.class);
        PowerMockito.whenNew(ObjectMapper.class).withNoArguments().thenReturn(objectMapper);
        Class<PatientDto> patientDtoClass = PatientDto.class;
        PowerMockito.when(objectMapper.readValue(t, patientDtoClass)).thenThrow(IOException.class);
        jsonSerializer = new JsonSerializer<>();
        Assert.assertNull(jsonSerializer.getObject(t, patientDtoClass));
    }

    private PatientDto giveAPatientDto() {
        return MockDtoUtil.givenAPatient();
    }

    private String givenAJson() {
        return "{\"ariaId\":\"\",\"hisId\":\"\",\"nationalId\":\"\",\"chineseName\":\"\",\"englishName\":\"\",\"gender\":\"\",\"birthday\":1490929020905,\"contactPerson\":\"\",\"contactPhone\":\"\",\"patientSer\":\"\",\"physicianGroupId\":\"\",\"physicianId\":\"\",\"physicianName\":\"\",\"physicianPhone\":\"\"}";
    }
}
