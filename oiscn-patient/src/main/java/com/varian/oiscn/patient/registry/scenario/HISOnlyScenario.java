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
 *  Patient registry scenario: only HIS. Retrieve patient data only from HIS, none in ARIA and Qin.
 */
public class HISOnlyScenario extends AbstractRegistryScenario {

    public HISOnlyScenario(Patient patient, Encounter encounter, Map<String, String> dynamicFormItems) {
        super(patient, encounter, dynamicFormItems);
        this.scenarioFlag = PatientRegistrationVO.N1_HIS_ONLY;
    }

    public HISOnlyScenario(Patient patient, Encounter encounter) {
        this(patient, encounter, null);
    }

    @Override
    public RegistryVerifyStatusEnum verifyRegistry() {
        return RegistryUtil.verifyNewPatientRegistry(patient, encounter);
    }

    @Override
    public Long saveOrUpdate(Configuration configuration, UserContext userContext) {
        Long patientSer = createPatient2ARIA(configuration);
        if (patientSer != null) {
            boolean result = createDiagnosis2ARIA(patientSer, configuration);
            if (result) {
                result = markUrgent2ARIA(patientSer, configuration);
            }
            if (result) {
                result = createCoverage2ARIA(patientSer);
            }
            if (result) {
                String carePathId = linkCarePathAndAdd2Encounter(patientSer, configuration);
                result = isNotEmpty(carePathId);
            }
            if (result) {
                result = markActiveStatus2ARIA(patientSer, configuration);
            }
            if (result) {
                putPatient2Cache(patientSer);
            }

            //create patient and encounter in local db.
            if (result) {
                RegistryService registryService = new RegistryService(userContext);
                registryService.create(patient, encounter, patientSer);

                PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer.toString());
            }
        }
        return patientSer;
    }
}
