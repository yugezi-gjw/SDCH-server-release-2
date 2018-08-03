package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.patient.registry.AbstractRegistryScenario;
import com.varian.oiscn.patient.registry.RegistryService;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class HISAriaQinInactiveScenario extends AbstractRegistryScenario {
    public HISAriaQinInactiveScenario(Patient patient, Encounter encounter, Map<String, String> dynamicFormItems) {
        super(patient, encounter, dynamicFormItems);
        this.scenarioFlag = PatientRegistrationVO.N4_HIS_ARIA_QIN_INACTIVE;
    }

    public HISAriaQinInactiveScenario(Patient patient, Encounter encounter) {
        this(patient, encounter, null);
    }

    @Override
    public Long saveOrUpdate(Configuration configuration, UserContext userContext) {
        if (patient.getPatientSer() == null) {
            log.error("PatientSer is null in HISAriaQinInactiveScenario interface.");
            return null;
        }
        boolean result;
        result = updatePatient2ARIA(configuration);
        if (result) {
            result = updateUrgent2ARIA(patient.getPatientSer(), configuration);
        }
//        if (result) {
//            result = updateDiagnosis2ARIA(encounter, patientSer);
//        }
        if (result) {
            result = updateCoverage2ARIA(patient.getPatientSer());
        }
        if (result) {
            String carePathId = linkCarePathAndAdd2Encounter(patient.getPatientSer(), configuration);
            result = isNotEmpty(carePathId);
        }
        if (result) {
            result = markActiveStatus2ARIA(patient.getPatientSer(), configuration);
        }
        if (result) {
            putPatient2Cache(patient.getPatientSer());
        }

        //create patient and encounter in local db.
        if (result) {
            RegistryService registryService = new RegistryService(userContext);
            registryService.updateWithNewEncounter(patient, encounter, patient.getPatientSer());

            PatientEncounterHelper.syncEncounterCarePathByPatientSer(patient.getPatientSer().toString());
        }
        return patient.getPatientSer();
    }
}
