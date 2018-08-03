package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

public final class NoHISAriaQinInactiveScenario extends HISAriaQinInactiveScenario {
    public NoHISAriaQinInactiveScenario(Patient patient, Encounter encounter) {
        super(patient, encounter);
        this.scenarioFlag = PatientRegistrationVO.N6_NOHIS_ARIA_QIN_INACTIVE;
    }
}
