package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Condition;
import com.varian.oiscn.anticorruption.datahelper.MockDiagnosisUtil;
import com.varian.oiscn.core.patient.Diagnosis;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.*;

/**
 * Created by fmk9441 on 2017-05-17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ConditionAssembler.class)
public class CondtionAssemblerTest {
    @InjectMocks
    private ConditionAssembler conditionAssembler;

    @Test
    public void givenADiagnosisWhenConvertThenReturnCondition() throws Exception {
        Diagnosis diagnosis = MockDiagnosisUtil.givenADiagnosis();
        Condition condition = ConditionAssembler.getCondition(diagnosis);
        Assert.assertThat(condition, is(not(nullValue())));
        Assert.assertTrue(condition.getRecurrence().getValue());
    }

    @Test
    public void givenAConditionWhenConvertThenReturnDiagnosis() throws Exception {
        Condition condition = MockDiagnosisUtil.givenACondtion();
        Diagnosis diagnosis = ConditionAssembler.getDiagnosis(condition);
        Assert.assertThat(diagnosis, is(not(nullValue())));
        Assert.assertFalse(diagnosis.getRecurrence());
    }

    @Test
    public void givenAConditionAndDiagnosisWhenUpdateReturnNothing() {
        Condition condition = MockDiagnosisUtil.givenACondtion();
        Diagnosis diagnosis = MockDiagnosisUtil.givenADiagnosis();
        ConditionAssembler.updateCondition(condition, diagnosis);
        Assert.assertTrue(condition.getRecurrence().getValue());
        Assert.assertEquals(condition.getBodySiteFirstRep().getCodingFirstRep().getCode(), "BodypartCode2");
    }
}
