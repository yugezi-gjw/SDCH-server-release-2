package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

public class NoHISAriaOnlyScenario extends HISAriaScenario {
    public NoHISAriaOnlyScenario(Patient patient, Encounter encounter) {
        super(patient, encounter);
        this.scenarioFlag = PatientRegistrationVO.N7_NOHIS_ARIA_ONLY;
    }
}
