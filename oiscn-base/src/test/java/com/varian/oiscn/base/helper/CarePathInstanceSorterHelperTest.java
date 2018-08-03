package com.varian.oiscn.base.helper;

import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.core.carepath.CarePathInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 7/20/2017.
 */
public class CarePathInstanceSorterHelperTest {

    @InjectMocks
    private CarePathInstanceSorterHelper helper;

    @Test
    public void givenCarePathInstanceWhenSortThenReturnActivities() {
        CarePathInstance instance = MockDtoUtil.givenACarePathInstance();
        CarePathInstanceSorterHelper.sortActivities(instance);
        Assert.assertEquals(1, instance.getActivityInstances().get(0).getIndex().intValue());
    }
}
