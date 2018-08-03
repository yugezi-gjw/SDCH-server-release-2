package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.oiscn.core.common.KeyValuePair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by fmk9441 on 2017-08-24.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TreatmentRecordAntiCorruptionServiceImp.class)
public class TreatmentRecordAntiCorruptionServiceImpTest {
    private TreatmentRecordAntiCorruptionServiceImp treatmentRecordAntiCorruptionServiceImp;

    @Before
    public void Setup() {
        treatmentRecordAntiCorruptionServiceImp = new TreatmentRecordAntiCorruptionServiceImp();
    }

    @Test
    public void givenAPatientIdWhenQueryThenReturnTxRecordList() throws Exception {
        List<KeyValuePair> lstComment = treatmentRecordAntiCorruptionServiceImp.queryTxRecordCommentList("PatientId");
        Assert.assertThat(0, is(lstComment.size()));
    }
}
