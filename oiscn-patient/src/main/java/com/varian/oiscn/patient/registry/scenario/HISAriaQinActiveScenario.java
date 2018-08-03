package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

public class HISAriaQinActiveScenario extends NoHISAriaQinActiveScenario {
    public HISAriaQinActiveScenario(Patient patient, Encounter encounter) {
        super(patient, encounter);
        this.scenarioFlag = PatientRegistrationVO.N3_HIS_ARIA_QIN_ACTIVE;
    }
}
