package com.varian.oiscn.patient.workflowprogress;

import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.patient.view.WorkflowProgressNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by gbt1220 on 8/17/2017.
 */
public class PatientWorkflowProgressHelperTest {

    private String keyActivityType = "keyActivityByLane";

    private String keyActivityTypeValue = "KeyActivity";

    @Test
    public void givenCarePathInstanceWhenKeyTypeIsByLaneThenReturnTheKeyWorkflow() {
        CarePathInstance carePathInstance = givenACarePathInstance();
        PatientWorkflowProgressHelper helper = new PatientWorkflowProgressHelper(carePathInstance, keyActivityType, keyActivityTypeValue);
        List<WorkflowProgressNode> keyNodes = helper.getKeyActivityWorkflowOfPatient();
        Assert.assertEquals(4, keyNodes.size());
    }

    @Test
    public void givenCarePathInstanceWhenKeyTypeIsByCategoryThenReturnTheKeyWorkflow() {
        keyActivityType = "keyActivityByCategory";
        CarePathInstance carePathInstance = givenACarePathInstance();
        PatientWorkflowProgressHelper helper = new PatientWorkflowProgressHelper(carePathInstance, keyActivityType, keyActivityTypeValue);
        List<WorkflowProgressNode> keyNodes = helper.getKeyActivityWorkflowOfPatient();
        Assert.assertEquals(4, keyNodes.size());
    }

    private CarePathInstance givenACarePathInstance() {
        CarePathInstance instance = new CarePathInstance();
        instance.setId("1");
        instance.setPatientID("patientID");
        instance.setEncounterID("1");

        ActivityInstance one = new ActivityInstance();
        one.setId("1");
        one.setActivityCode("code1");
        one.setCarePathLane("KeyActivity");
        one.setActivityCategory("KeyActivity");
        one.setPrevActivities(null);
        one.setNextActivities(Arrays.asList("2", "3"));
        one.setStatus(CarePathStatusEnum.COMPLETED);
        instance.addActivityInstance(one);

        ActivityInstance two = new ActivityInstance();
        two.setId("2");
        two.setActivityCode("code2");
        two.setPrevActivities(Arrays.asList("1"));
        two.setNextActivities(Arrays.asList("5"));
        two.setStatus(CarePathStatusEnum.COMPLETED);
        instance.addActivityInstance(two);

        ActivityInstance three = new ActivityInstance();
        three.setId("3");
        three.setActivityCode("code3");
        three.setPrevActivities(Arrays.asList("1"));
        three.setNextActivities(Arrays.asList("4"));
        three.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(three);

        ActivityInstance four = new ActivityInstance();
        four.setId("4");
        four.setActivityCode("code4");
        four.setPrevActivities(Arrays.asList("3"));
        four.setNextActivities(Arrays.asList("5"));
        four.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(four);

        ActivityInstance five = new ActivityInstance();
        five.setId("5");
        five.setActivityCode("code5");
        five.setPrevActivities(Arrays.asList("2", "4"));
        five.setNextActivities(Arrays.asList("6"));
        five.setStatus(CarePathStatusEnum.DRAFT);
        five.setCarePathLane("KeyActivity");
        five.setActivityCategory("KeyActivity");
        instance.addActivityInstance(five);

        ActivityInstance six = new ActivityInstance();
        six.setId("6");
        six.setActivityCode("code6");
        six.setPrevActivities(Arrays.asList("5"));
        six.setNextActivities(null);
        six.setStatus(CarePathStatusEnum.DRAFT);
        instance.addActivityInstance(six);

        return instance;
    }
}
