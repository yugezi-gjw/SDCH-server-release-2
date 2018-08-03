package com.varian.oiscn.anticorruption.datahelper;

import com.varian.fhir.resources.Condition;
import com.varian.oiscn.core.patient.Diagnosis;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.codesystems.ConditionCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fmk9441 on 2017-05-16.
 */
public class MockDiagnosisUtil {
    private MockDiagnosisUtil() {
    }

    public static Condition givenACondtion() {
        Condition condition = new Condition();
        condition.addCategory(new CodeableConcept().setText(ConditionCategory.ENCOUNTERDIAGNOSIS.toCode()));
        condition.setCode(new CodeableConcept().addCoding(new Coding().setSystem("System").setCode("Code").setDisplay("Code Description")).setText("Clinic Description"));
        condition.setSubject(new Reference().setReference("PatientID"));
        condition.setOnset(new DateTimeType(new Date()));
        condition.setVerificationStatus(Condition.ConditionVerificationStatus.CONFIRMED);
        condition.setRank(new StringType("1"));
        condition.setDiagnosisStatusDate(new DateTimeType(new Date()));
        condition.addBodySite().addCoding().setSystem("System").setCode("BodypartCode");
        condition.setRecurrence(new BooleanType(false));
        return condition;
    }

    public static Diagnosis givenADiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPatientID("PatientID");
        diagnosis.setCode("Code");
        diagnosis.setDesc("Desc");
        diagnosis.setSystem("System");
        diagnosis.setRecurrence(true);
        Diagnosis.Staging diagnosisStaging = new Diagnosis.Staging();
        diagnosisStaging.setSchemeName("SchemeName");
        diagnosisStaging.setBasisCode("BasisCode");
        diagnosisStaging.setTcode("T");
        diagnosisStaging.setNcode("N");
        diagnosisStaging.setMcode("M");
        diagnosisStaging.setDate(new Date());
        diagnosis.setStaging(diagnosisStaging);
        diagnosis.setDiagnosisDate(new Date());
        diagnosis.setBodypartCode("BodypartCode2");
        diagnosis.setBodypartDesc("BodypartDesc2");
        diagnosis.setDiagnosisNote("DiagnosisNote");
        return diagnosis;
    }

    public static List<Condition> givenAConditionList() {
        return Arrays.asList(givenACondtion());
    }

    public static Bundle givenAnDiagnosisBundle() {
        Bundle bundle = new Bundle();
        Bundle.BundleEntryComponent bundleEntryComponent = new Bundle.BundleEntryComponent();
        bundleEntryComponent.setResource(givenACondtion());
        List<Bundle.BundleEntryComponent> lstBundleEntryComponents = new ArrayList<>();
        lstBundleEntryComponents.add(bundleEntryComponent);
        bundle.setEntry(lstBundleEntryComponents);
        return bundle;
    }
}