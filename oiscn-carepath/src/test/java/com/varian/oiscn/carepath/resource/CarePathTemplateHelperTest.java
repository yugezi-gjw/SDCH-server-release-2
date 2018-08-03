package com.varian.oiscn.carepath.resource;

import com.varian.oiscn.carepath.util.MockDtoUtil;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Created by gbt1220 on 6/22/2017.
 */
public class CarePathTemplateHelperTest {

    @InjectMocks
    private CarePathTemplateHelper helper;

    @Test
    public void givenTemplateWhenSortThenReturnSortedActivities() {
        CarePathTemplate template = MockDtoUtil.givenCarePathTemplate();
        CarePathTemplateHelper.sortActivities(template);
        Assert.assertEquals("1", template.getActivities().get(0).getId());
    }
}
