package com.varian.oiscn.encounter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PatientEncounterHelper.class)
public class PatientEncounterHelperTest {

    private EncounterCarePathServiceImpl encounterCarePathServiceImpl;

    private EncounterEndPlanServiceImpl encounterEndPlanServiceImpl;

    @Before
    public void setup() {
        encounterCarePathServiceImpl = PowerMockito.mock(EncounterCarePathServiceImpl.class);
        encounterEndPlanServiceImpl = PowerMockito.mock(EncounterEndPlanServiceImpl.class);
        PatientEncounterHelper.encounterCarePathServiceImpl = encounterCarePathServiceImpl;
        PatientEncounterHelper.encounterEndPlanServiceImpl = encounterEndPlanServiceImpl;
    }
    @Test
    public void testAddToPatientSerEncounterCarePathCache(){
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath();
        patientEncounterCarePath.setPatientSer("10000");
        PatientEncounterHelper.addToPatientSerEncounterCarePathCache(patientEncounterCarePath);
        Assert.assertEquals(patientEncounterCarePath, PatientEncounterHelper.patientEncounterCarePathInstanceIdMap.get("10000"));
    }

    @Test
    public void testAddToPatientSerEncounterEndPlanCache() {
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan();
        patientEncounterEndPlan.setPatientSer("10000");
        PatientEncounterHelper.addToPatientSerEncounterEndPlanCache(patientEncounterEndPlan);
        Assert.assertEquals(patientEncounterEndPlan, PatientEncounterHelper.patientEncounterEndPlanMap.get("10000"));
    }

    @Test
    public void testGetEncounterCarePathByPatientSer(){
        String patientSer = "10000";
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath();
        patientEncounterCarePath.setPatientSer("10000");
        PowerMockito.when(encounterCarePathServiceImpl.queryEncounterCarePathByPatientSer(patientSer)).thenReturn(patientEncounterCarePath);
        Assert.assertEquals(patientEncounterCarePath, PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer));
    }

    @Test
    public void testGetEncounterEndPlanByPatientSer() {
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan();
        String patientSer = "10000";
        patientEncounterEndPlan.setPatientSer(patientSer);
        PowerMockito.when(encounterEndPlanServiceImpl.queryEncounterEndPlanListByPatientSer(patientSer)).thenReturn(null);
        Assert.assertEquals(patientSer, PatientEncounterHelper.getEncounterEndPlanByPatientSer(patientSer).getPatientSer());
    }

    @Test
    public void testSyncEncounterCarePathByPatientSer() {
        String patientSer = "10000";
        PatientEncounterCarePath patientEncounterCarePath = new PatientEncounterCarePath();
        patientEncounterCarePath.setPatientSer(patientSer);
        PowerMockito.when(encounterCarePathServiceImpl.queryEncounterCarePathByPatientSer(patientSer)).thenReturn(patientEncounterCarePath);
        PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer);
        Assert.assertEquals(patientEncounterCarePath, PatientEncounterHelper.patientEncounterCarePathInstanceIdMap.get(patientSer));
    }

    @Test
    public void testSyncEncounterEndPlanByPatientSer(){
        PatientEncounterEndPlan patientEncounterEndPlan = new PatientEncounterEndPlan();
        String patientSer = "10000";
        patientEncounterEndPlan.setPatientSer(patientSer);
        PatientEncounterHelper.addToPatientSerEncounterEndPlanCache(patientEncounterEndPlan);
        PowerMockito.when(encounterEndPlanServiceImpl.queryEncounterEndPlanListByPatientSer(patientSer)).thenReturn(new ArrayList<>());
        Assert.assertEquals(new ArrayList<>(), PatientEncounterHelper.patientEncounterEndPlanMap.get(patientSer).getCompletedPlan());
    }
}
