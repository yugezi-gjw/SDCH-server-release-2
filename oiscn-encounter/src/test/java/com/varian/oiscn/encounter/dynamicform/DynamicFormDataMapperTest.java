package com.varian.oiscn.encounter.dynamicform;

import java.util.ArrayList;
import java.util.List;

import com.varian.oiscn.core.common.KeyValuePair;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 10/18/2017.
 */
public class DynamicFormDataMapperTest {

    @Test
    public void givenDynamicFormItemsWithPatientWhenMapperThenReturnPatientData() {
        String ariaId = "testAriaId";
        List<KeyValuePair> dynamicFormItems = new ArrayList<>();
        dynamicFormItems.add(new KeyValuePair("Patient.ariaId", ariaId));
        DynamicFormDataMapper mapper = new DynamicFormDataMapper(dynamicFormItems);
        Assert.assertEquals(ariaId, mapper.getPatientInfo().getAriaId());
        Assert.assertNull(mapper.getPatientInfo().getHisId());
    }
}
