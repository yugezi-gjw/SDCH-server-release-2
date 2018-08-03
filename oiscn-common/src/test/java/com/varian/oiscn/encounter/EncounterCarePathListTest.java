package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterCarePath;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bhp9696 on 2018/4/11.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({EncounterCarePathList.class})
public class EncounterCarePathListTest {

    @Test
    public void testGetMasterCarePathInstanceId() throws ParseException {
        EncounterCarePathList encounterCarePathList = new EncounterCarePathList();
        encounterCarePathList.setEncounterId(111L);
        encounterCarePathList.setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
            setCpInstanceId(100L);
            setCrtTime(DateUtils.parseDate("2018-04-03 10:10:10", "yyyy-MM-dd HH:mm:ss"));
            setCategory(EncounterCarePathCategoryEnum.PRIMARY);
        }},new EncounterCarePath(){{
            setCpInstanceId(200L);
            setCrtTime(DateUtils.parseDate("2018-04-03 10:11:10", "yyyy-MM-dd HH:mm:ss"));
            setCategory(EncounterCarePathCategoryEnum.PRIMARY);
        }},new EncounterCarePath(){{
            setCpInstanceId(300L);
            setCrtTime(DateUtils.parseDate("2018-04-03 10:12:10", "yyyy-MM-dd HH:mm:ss"));
            setCategory(EncounterCarePathCategoryEnum.OPTIONAL);
        }}));

        Long cpInstanceId = encounterCarePathList.getMasterCarePathInstanceId();
        Assert.assertTrue(cpInstanceId == 200L);
    }

    @Test
    public void testGetOptionalCarePathInstanceId(){
        EncounterCarePathList encounterCarePathList = new EncounterCarePathList();
        List<Long> longList = encounterCarePathList.getOptionalCarePathInstanceId();
        Assert.assertEquals(new ArrayList<Long>(), longList);
    }
}
