package com.varian.oiscn.core.encounter;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncounterCarePathTest {

    @Test
    public void testFromCode() throws ParseException{
        EncounterCarePath encounterCarePath = new EncounterCarePath();
        EncounterCarePath.EncounterCarePathCategoryEnum thisEnum = EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL;
        encounterCarePath.setCategory(thisEnum);
        Assert.assertEquals(thisEnum, encounterCarePath.getCategory());
        encounterCarePath.setEncounterId(1000L);
        Assert.assertEquals(new Long(1000L), encounterCarePath.getEncounterId());
        encounterCarePath.setCpInstanceId(2000L);
        Assert.assertEquals(new Long(2000L), encounterCarePath.getCpInstanceId());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse("2018-05-10");
        encounterCarePath.setCrtTime(date);
        Assert.assertEquals(date, encounterCarePath.getCrtTime());
        encounterCarePath.setCrtUser("user");
        Assert.assertEquals("user", encounterCarePath.getCrtUser());
        Assert.assertEquals(EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL, EncounterCarePath.EncounterCarePathCategoryEnum.fromCode("OPTIONAL"));
        Assert.assertEquals(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY, EncounterCarePath.EncounterCarePathCategoryEnum.fromCode("PRIMARY"));
        Assert.assertEquals(EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY, EncounterCarePath.EncounterCarePathCategoryEnum.fromCode("OTHER"));
    }
}
