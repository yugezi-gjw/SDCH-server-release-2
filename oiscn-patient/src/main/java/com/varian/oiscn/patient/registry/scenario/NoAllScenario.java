package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

public class NoAllScenario extends HISOnlyScenario {
    public NoAllScenario(Patient patient, Encounter encounter) {
        super(patient, encounter);
        this.scenarioFlag = PatientRegistrationVO.N9_NOALL;
    }
}
