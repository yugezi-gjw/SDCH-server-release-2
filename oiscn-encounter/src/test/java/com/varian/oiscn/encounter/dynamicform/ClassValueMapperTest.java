package com.varian.oiscn.encounter.dynamicform;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.varian.oiscn.core.patient.PatientDto;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 10/18/2017.
 */
public class ClassValueMapperTest {

    @Test
    public void givenPatientDataWhenMapperThenReturnNewInstanceWithValue() {
        ClassValueMapper<PatientDto> classValueMapper = new ClassValueMapper<>();
        Date curDate = new Date();
        String ariaId = "testAriaId";
        Map<String, Object> values = new HashMap<>();
        values.put("ariaId", ariaId);
        values.put("asdfsd", "slkdf");
        values.put("birthday", curDate);
        PatientDto patientDto = classValueMapper.newClassInstanceWithValues(PatientDto.class, values);
        Assert.assertEquals(ariaId, patientDto.getAriaId());
        Assert.assertEquals(curDate, patientDto.getBirthday());
    }
}
