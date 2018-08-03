package com.varian.oiscn.anticorruption.base;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

/**
 * Created by bhp9696 on 2018/3/13.
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(PatientIdMapper.class)
public class PatientIdMapperTest {
    @Test
    public void testPatientIdMapper(){
        PatientIdMapper.init(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID,PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID);
        String id1 = PatientIdMapper.getPatientId1Mapper();
        String id2 = PatientIdMapper.getPatientId2Mapper();
        Assert.assertTrue(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID.equals(id1));
        Assert.assertTrue(PatientIdMapper.IDENTIFIER_MAPPER_TO_ARIA_ID.equals(id2));
    }
}
