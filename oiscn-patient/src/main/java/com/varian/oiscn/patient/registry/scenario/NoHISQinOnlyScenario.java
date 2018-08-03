package com.varian.oiscn.patient.registry.scenario;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.patient.registry.AbstractRegistryScenario;
import com.varian.oiscn.patient.registry.RegistryService;
import com.varian.oiscn.patient.registry.RegistryUtil;
import com.varian.oiscn.patient.registry.RegistryVerifyStatusEnum;
import com.varian.oiscn.patient.view.PatientRegistrationVO;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 
 */
public class NoHISQinOnlyScenario extends AbstractRegistryScenario {

    public NoHISQinOnlyScenario(Patient patient, Encounter encounter) {
        this(patient, encounter, null);
    }

    public NoHISQinOnlyScenario(Patient patient, Encounter encounter, Map<String, String> dynamicFormItems) {
        super(patient, encounter, dynamicFormItems);
        this.scenarioFlag = PatientRegistrationVO.N8_NOHIS_QIN_ONLY;
    }

    @Override
    public RegistryVerifyStatusEnum verifyRegistry() {
        return RegistryUtil.verifyNewPatientRegistry(patient, encounter);
    }

    @Override
    public Long saveOrUpdate(Configuration configuration, UserContext userContext) {
        Long newPatientSer = createPatient2ARIA(configuration);
        if (newPatientSer != null) {
            boolean result = createDiagnosis2ARIA(newPatientSer, configuration);
            if (result) {
                result = markUrgent2ARIA(newPatientSer, configuration);
            }
            if (result) {
                result = createCoverage2ARIA(newPatientSer);
            }
            if (result) {
                String carePathId = linkCarePathAndAdd2Encounter(newPatientSer, configuration);
                result = isNotEmpty(carePathId);
            }
            if (result) {
                result = markActiveStatus2ARIA(newPatientSer, configuration);
            }
            if (result) {
                putPatient2Cache(newPatientSer);
            }

            //create patient and encounter in local db.
            if (result) {
                RegistryService registryService = new RegistryService(userContext);
                patient.setPatientSer(newPatientSer);
                encounter.setPatientSer(String.valueOf(newPatientSer));
                registryService.create(patient, encounter, newPatientSer);

                //todo delete patient and encounter of the old patientSer from local db, because they have same hisId.

                PatientEncounterHelper.syncEncounterCarePathByPatientSer(newPatientSer.toString());
            }
        }
        return newPatientSer;
    }
}
