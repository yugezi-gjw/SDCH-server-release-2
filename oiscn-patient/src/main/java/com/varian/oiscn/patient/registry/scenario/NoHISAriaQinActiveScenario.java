package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.patient.registry.AbstractRegistryScenario;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

public class NoHISAriaQinActiveScenario extends AbstractRegistryScenario {
    public NoHISAriaQinActiveScenario(Patient patient, Encounter encounter) {
        super(patient, encounter);
        this.scenarioFlag = PatientRegistrationVO.N5_NOHIS_ARIA_QIN_ACTIVE;
    }

    @Override
    public Long saveOrUpdate(Configuration configuration, UserContext userContext) {
        //Do nothing.
        return null;
    }
}
