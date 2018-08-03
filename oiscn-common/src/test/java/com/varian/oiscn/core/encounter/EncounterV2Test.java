package com.varian.oiscn.core.encounter;

import com.varian.oiscn.core.patient.Diagnosis;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class EncounterV2Test {

    @Test
    public void testFields(){
        EncounterV2 encounter = new EncounterV2();
        encounter.setId("id");
        Assert.assertEquals("id", encounter.getId());
        encounter.setStatus(StatusEnum.FINISHED);
        Assert.assertEquals(StatusEnum.FINISHED, encounter.getStatus());
        encounter.setPrimaryPhysicianGroupID("primaryPhysicianGroupID");
        Assert.assertEquals("primaryPhysicianGroupID", encounter.getPrimaryPhysicianGroupID());
        encounter.setPrimaryPhysicianGroupName("primaryPhysicianGroupName");
        Assert.assertEquals("primaryPhysicianGroupName", encounter.getPrimaryPhysicianGroupName());
        encounter.setPrimaryPhysicianID("primaryPhysicianID");
        Assert.assertEquals("primaryPhysicianID", encounter.getPrimaryPhysicianID());
        encounter.setPhysicianBId("physicianBId");
        Assert.assertEquals("physicianBId", encounter.getPhysicianBId());
        encounter.setPhysicianBName("physicianBName");
        Assert.assertEquals("physicianBName", encounter.getPhysicianBName());
        encounter.setPhysicianCId("physicianCId");
        Assert.assertEquals("physicianCId", encounter.getPhysicianCId());
        encounter.setPhysicianCName("physicianCName");
        Assert.assertEquals("physicianCName", encounter.getPhysicianCName());
        encounter.setPatientSer("patientSer");
        Assert.assertEquals("patientSer", encounter.getPatientSer());
        encounter.setAge("age");
        Assert.assertEquals("age", encounter.getAge());
        encounter.setAlert("alert");
        Assert.assertEquals("alert", encounter.getAlert());
        encounter.setUrgent(true);
        Assert.assertEquals(true, encounter.isUrgent());
        encounter.setEcogScore("ecogScore");
        Assert.assertEquals("ecogScore", encounter.getEcogScore());
        encounter.setEcogDesc("ecogDesc");
        Assert.assertEquals("ecogDesc", encounter.getEcogDesc());
        encounter.setPositiveSign("positiveSign");
        Assert.assertEquals("positiveSign", encounter.getPositiveSign());
        encounter.setInsuranceType("insuranceType");
        Assert.assertEquals("insuranceType", encounter.getInsuranceType());
        encounter.setInsuranceTypeCode("insuranceTypeCode");
        Assert.assertEquals("insuranceTypeCode", encounter.getInsuranceTypeCode());
        encounter.setPatientSource("patientSource");
        Assert.assertEquals("patientSource", encounter.getPatientSource());
        encounter.setDiagnoses(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), encounter.getDiagnoses());
        encounter.setPhysicianComment("physicianComment");
        Assert.assertEquals("physicianComment", encounter.getPhysicianComment());
        encounter.setAllergyInfo("allergyInfo");
        Assert.assertEquals("allergyInfo", encounter.getAllergyInfo());
        encounter.setEncounterCarePathList(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), encounter.getEncounterCarePathList());
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setRecurrence(false);
        encounter.addDiagnosis(diagnosis);
        Assert.assertEquals(false, encounter.getDiagnoses().get(0).getRecurrence());
    }
}
