package com.varian.oiscn.core.participant;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 3/2/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(ParticipantTypeEnum.class)
public class ParticipantTypeEnumTest {
    @Test
    public void givenParticipantTypeWhenGetDisplayThenReturnTypeString() {
        Assert.assertEquals("DEVICE", ParticipantTypeEnum.getDisplay(ParticipantTypeEnum.DEVICE));
        Assert.assertEquals("PATIENT", ParticipantTypeEnum.getDisplay(ParticipantTypeEnum.PATIENT));
        Assert.assertEquals("LOCATION", ParticipantTypeEnum.getDisplay(ParticipantTypeEnum.LOCATION));
        Assert.assertEquals("PRACTITIONER", ParticipantTypeEnum.getDisplay(ParticipantTypeEnum.PRACTITIONER));
    }

    @Test
    public void givenParticipantTypeStringWhenFromCodeThenReturnType() {
        Assert.assertEquals(ParticipantTypeEnum.DEVICE, ParticipantTypeEnum.fromCode("DEVICE"));
        Assert.assertEquals(ParticipantTypeEnum.PATIENT, ParticipantTypeEnum.fromCode("PATIENT"));
        Assert.assertEquals(ParticipantTypeEnum.LOCATION, ParticipantTypeEnum.fromCode("LOCATION"));
        Assert.assertEquals(ParticipantTypeEnum.PRACTITIONER, ParticipantTypeEnum.fromCode("PRACTITIONER"));
    }
}
