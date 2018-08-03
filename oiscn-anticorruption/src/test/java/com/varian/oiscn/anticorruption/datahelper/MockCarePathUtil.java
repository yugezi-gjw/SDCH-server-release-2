package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.CarePath;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-04-20.
 */
public class MockCarePathUtil {
    private MockCarePathUtil() {

    }

    public static Bundle givenACarePathBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenACarePath());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponent = new ArrayList<>();
        lstBundleEntryComponent.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponent);
        return bundle;
    }

    public static CarePath givenACarePath() {
        CarePath carePath = new CarePath();
        carePath.setId("1234");
        carePath.setTemplateName(new StringType("TemplateName"));
        carePath.setDepartment(new Reference("DepartmentID"));
        carePath.setPatient(new Reference("PatientID"));
        carePath.setPlannedActivities(Arrays.asList(givenAPlanedActivity()));
        carePath.setComments(new StringType("Comments"));
        return carePath;
    }

    public static CarePath.PlannedActivity givenAPlanedActivity() {
        CarePath.PlannedActivity activity = new CarePath.PlannedActivity();
        activity.setId("ID");
        activity.setActivityInstance(new Reference("ActivityInstanceID"));
        activity.setCausing(Arrays.asList(new Reference("CausingID")));
        activity.setResulting(Arrays.asList(new Reference("ResultingID")));
        activity.setIsAvailableInWorkflow(new BooleanType(true));
        activity.setActivityDefinition(new Reference("ActivityDefinition/ActivityDefinitionID").setDisplay("ActivityCode"));
        activity.setActivityCategory(new StringType("ActivityCategory"));
        activity.setLane(new StringType("Lane"));
        activity.setResources(Arrays.asList(new Reference("Device/DeviceID"), new Reference("Practitioner/PractitionerID")));
        activity.setResourceGroups(Arrays.asList(new Reference("Group/GroupID")));
        activity.setStatus(CarePath.CarePathStatus.ACTIVE);
        Duration lagTime = new Duration();
        lagTime.setValue(60);
        activity.setLagAfterPreviousActivity(lagTime);
        activity.setLastModified(new DateTimeType(new Date()));
        activity.setActivityType(new StringType("Appointment"));
        activity.setDuration(new Period().setStart(new Date()).setEnd(new Date()));
        activity.setAutoAssignOncologist(new BooleanType(true));
        return activity;
    }

    public static CarePathInstance givenACarePathInstance() {
        CarePathInstance instance = new CarePathInstance();
        instance.setId("1");
        instance.setPatientID("patientID");
        instance.setEncounterID("encounterId");

        ActivityInstance one = new ActivityInstance();
        one.setId("1");
        one.setActivityCode("activityCode1");
        one.setActivityType(ActivityTypeEnum.TASK);
        one.setDefaultGroupID("1");
        one.setDepartmentID("1");
        one.setInstanceID("1");
        one.setIsActiveInWorkflow(true);
        one.setPrevActivities(null);
        one.setNextActivities(Arrays.asList("2"));
        one.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(one);

        ActivityInstance two = new ActivityInstance();
        two.setId("2");
        two.setActivityCode("activityCode2");
        two.setActivityType(ActivityTypeEnum.TASK);
        two.setDefaultGroupID("1");
        two.setDepartmentID("1");
        two.setInstanceID("");
        two.setIsActiveInWorkflow(false);
        two.setPrevActivities(Arrays.asList("1"));
        two.setNextActivities(Arrays.asList("3"));
        instance.addActivityInstance(two);

        ActivityInstance three = new ActivityInstance();
        three.setId("3");
        three.setActivityCode("activityCode3");
        three.setActivityType(ActivityTypeEnum.TASK);
        three.setDefaultGroupID("1");
        three.setDepartmentID("1");
        three.setInstanceID("");
        three.setIsActiveInWorkflow(false);
        three.setPrevActivities(Arrays.asList("2"));
        three.setNextActivities(null);
        instance.addActivityInstance(three);

        return instance;
    }
}
