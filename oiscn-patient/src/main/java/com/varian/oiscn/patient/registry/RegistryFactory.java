package com.varian.oiscn.patient.registry;

import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.assembler.RegistrationVOAssembler;
import com.varian.oiscn.base.extend.ImplementationExtensionService;
import com.varian.oiscn.base.group.GroupTreeNode;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfiguration;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.patient.assembler.PatientRegistryAssembler;
import com.varian.oiscn.patient.integration.HISPatientQuery;
import com.varian.oiscn.patient.integration.IPatientQuery;
import com.varian.oiscn.patient.registry.scenario.HISAriaQinActiveScenario;
import com.varian.oiscn.patient.registry.scenario.HISAriaQinInactiveScenario;
import com.varian.oiscn.patient.registry.scenario.HISAriaScenario;
import com.varian.oiscn.patient.registry.scenario.HISOnlyScenario;
import com.varian.oiscn.patient.registry.scenario.NoAllScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaOnlyScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaQinActiveScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISAriaQinInactiveScenario;
import com.varian.oiscn.patient.registry.scenario.NoHISQinOnlyScenario;
import com.varian.oiscn.patient.service.PatientServiceImp;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RegistryFactory {
    private RegistryFactory() {
    }

    /**
     * Get patient registry scenario by hisId in patient registry page.
     * N1_HIS_ONLY - Patient only retrieved from HIS.
     * N2_HIS_ARIA - Patient exists in HIS and ARIA.
     * N3_HIS_ARIA_QIN_ACTIVE - Patient exists in HIS, ARIA and Qin, and the patient is in-treatment.
     * N4_HIS_ARIA_QIN_INACTIVE - Patient exists in HIS, ARIA and Qin, and the patient is not in-treatment.
     * N5_NOHIS_ARIA_QIN_ACTIVE - Patient exists in ARIA and Qin, and the patient is in-treatment.
     * N6_NOHIS_ARIA_QIN_INACTIVE - Patient exists in ARIA and Qin, and the patient is not in-treatment.
     * N7_NOHIS_ARIA_ONLY - Patient only exists in ARIA.
     * N8_NOHIS_QIN_ONLY - Patient only exists in Qin.
     * N9_NOALL - The new patient, not exists anywhere.
     *
     * @param hisId         his id
     * @param configuration configuration
     * @param userContext   user context
     * @return scenario
     */
    public static AbstractRegistryScenario getScenario(String hisId, Configuration configuration, UserContext userContext) {
        PatientDto ariaPatient = getAriaPatient(hisId);
        Patient qinPatient = getQinPatient(hisId, userContext, ariaPatient);
        RegistrationVO hisPatient = getHISPatient(hisId);

        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = getEncounterWithLoginPractitioner(userContext);
        Patient patient;
        if (qinPatient == null && ariaPatient == null && hisPatient == null) {
            return new NoAllScenario(null, encounter);
        } else if (qinPatient == null && ariaPatient != null && hisPatient == null) {
            patient = PatientRegistryAssembler.getPatientFromARIA(ariaPatient);
            return new NoHISAriaOnlyScenario(patient, encounter);
        } else if (qinPatient == null && ariaPatient != null && hisPatient != null) {
            patient = RegistrationVOAssembler.getPatient(hisPatient);
            patient.setPatientSer(Long.valueOf(ariaPatient.getPatientSer()));
            patient.setRadiationId(ariaPatient.getAriaId());
            encounter = RegistrationVOAssembler.getEncounter(encounter, hisPatient);
            return new HISAriaScenario(patient, encounter, hisPatient.getDynamicFormItems());
        } else if (qinPatient == null && ariaPatient == null && hisPatient != null) {
            patient = RegistrationVOAssembler.getPatient(hisPatient);
            encounter = RegistrationVOAssembler.getEncounter(encounter, hisPatient);
            return new HISOnlyScenario(patient, encounter, hisPatient.getDynamicFormItems());
        } else if (qinPatient != null && ariaPatient == null && hisPatient == null) {
            //The scenario should not occur.
            patient = qinPatient;
            encounter = encounterServiceImp.queryByPatientSer(patient.getPatientSer());
            return new NoHISQinOnlyScenario(patient, encounter);
        } else if (qinPatient != null && ariaPatient != null && hisPatient == null) {
            FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
            boolean activeFlag = flagAntiCorruptionServiceImp.checkPatientStatusIcon(
                    String.valueOf(qinPatient.getPatientSer()),
                    StatusIconPool.get(configuration.getActiveStatusIconDesc()));
            if (activeFlag) {
                patient = qinPatient;
                encounter = encounterServiceImp.queryByPatientSer(qinPatient.getPatientSer());
                return new NoHISAriaQinActiveScenario(patient, encounter);
            } else {
                patient = qinPatient;
                return new NoHISAriaQinInactiveScenario(patient, encounter);
            }
        } else if (qinPatient != null && ariaPatient != null && hisPatient != null) {
            FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
            boolean activeFlag = flagAntiCorruptionServiceImp.checkPatientStatusIcon(
                    String.valueOf(qinPatient.getPatientSer()),
                    StatusIconPool.get(configuration.getActiveStatusIconDesc()));
            if (activeFlag) {
                patient = qinPatient;
                encounter = encounterServiceImp.queryByPatientSer(qinPatient.getPatientSer());
                return new HISAriaQinActiveScenario(patient, encounter);
            } else {
                patient = RegistrationVOAssembler.getPatient(hisPatient);
                patient.setPatientSer(Long.valueOf(ariaPatient.getPatientSer()));
                patient.setRadiationId(ariaPatient.getAriaId());
                encounter = RegistrationVOAssembler.getEncounter(encounter, hisPatient);
                return new HISAriaQinInactiveScenario(patient, encounter, hisPatient.getDynamicFormItems());
            }
        } else if (qinPatient != null && ariaPatient == null && hisPatient != null) {
            //The scenario should not occur.
            patient = RegistrationVOAssembler.getPatient(hisPatient);
            encounter = RegistrationVOAssembler.getEncounter(encounter, hisPatient);
            return new NoHISQinOnlyScenario(patient, encounter, hisPatient.getDynamicFormItems());
        }
        return new NoAllScenario(null, encounter);
    }

    private static Encounter getEncounterWithLoginPractitioner(UserContext userContext) {
        Encounter encounter = new Encounter();
        String primaryPhysicianID = String.valueOf(userContext.getLogin().getResourceSer());
        GroupTreeNode groupTreeNode = GroupPractitionerHelper.getRegisterGroupByPractitionerId(
                GroupPractitionerHelper.getOncologyGroupTreeNode(),
                String.valueOf(primaryPhysicianID));
        String primaryPhysicianGroupID = groupTreeNode != null ? groupTreeNode.getId() : null;
        encounter.setPrimaryPhysicianGroupID(primaryPhysicianGroupID);
        encounter.setPrimaryPhysicianID(primaryPhysicianID);
        return encounter;
    }

    private static RegistrationVO getHISPatient(String hisId) {
        IPatientQuery iPatientQuery = hisPatientQuery();
        if (iPatientQuery == null) {
            return null;
        } else {
            return iPatientQuery.queryByHisId(hisId);
        }
    }

    private static IPatientQuery hisPatientQuery() {
        IPatientQuery iPatientQuery = null;
        HisPatientInfoConfiguration configuration = HisPatientInfoConfigService.getConfiguration();
        if (configuration != null && configuration.isHisPatientQueryEnable()) {
            iPatientQuery = newPatientExtendQuery();
            if (iPatientQuery == null) {
                iPatientQuery = new HISPatientQuery();
            }
        }
        return iPatientQuery;
    }

    private static PatientDto getAriaPatient(String hisId) {
        PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        return patientAntiCorruptionServiceImp.queryPatientWithPhotoByHisId(hisId);
    }

    private static Patient getQinPatient(String hisId, UserContext userContext, PatientDto patientInARIA) {
        Patient patientInQin;
        PatientServiceImp patientServiceImp = new PatientServiceImp(userContext);
        if (patientInARIA == null) {
            patientInQin = patientServiceImp.queryPatientByHisId(hisId);
        } else {
            patientInQin = patientServiceImp.queryPatientByPatientSer(patientInARIA.getPatientSer());
            if (patientInQin != null) {
                patientInQin.setPatientSer(Long.valueOf(patientInARIA.getPatientSer()));
            }
        }
        return patientInQin;
    }

    private static IPatientQuery newPatientExtendQuery() {
        IPatientQuery iPatientQuery = null;

        String extendClass = ImplementationExtensionService.getImplementationClassOf(IPatientQuery.class.getName());
        try {
            iPatientQuery = StringUtils.isBlank(extendClass) ? null : (IPatientQuery) Class.forName(extendClass).newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return iPatientQuery;
    }

    /**
     * Create patient registry scenario by scenario flag.
     *
     * @param vo patient registration vo
     * @return patient registry scenario
     */
    public static IPatientRegistry getRegistry(PatientRegistrationVO vo) {
        if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N1_HIS_ONLY)) {
            return new HISOnlyScenario(vo.getPatient(), vo.getEncounter(), vo.getDynamicFormItems());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N2_HIS_ARIA)) {
            return new HISAriaScenario(vo.getPatient(), vo.getEncounter(), vo.getDynamicFormItems());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N3_HIS_ARIA_QIN_ACTIVE)) {
            return new HISAriaQinActiveScenario(vo.getPatient(), vo.getEncounter());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N4_HIS_ARIA_QIN_INACTIVE)) {
            return new HISAriaQinInactiveScenario(vo.getPatient(), vo.getEncounter(), vo.getDynamicFormItems());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N5_NOHIS_ARIA_QIN_ACTIVE)) {
            return new NoHISAriaQinActiveScenario(vo.getPatient(), vo.getEncounter());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N6_NOHIS_ARIA_QIN_INACTIVE)) {
            return new NoHISAriaQinInactiveScenario(vo.getPatient(), vo.getEncounter());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N7_NOHIS_ARIA_ONLY)) {
            return new NoHISAriaOnlyScenario(vo.getPatient(), vo.getEncounter());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N8_NOHIS_QIN_ONLY)) {
            return new NoHISQinOnlyScenario(vo.getPatient(), vo.getEncounter(), vo.getDynamicFormItems());
        } else if (StringUtils.equals(vo.getScenarioFlag(), PatientRegistrationVO.N9_NOALL)) {
            return new NoAllScenario(vo.getPatient(), vo.getEncounter());
        } else {
            return null;
        }
    }
}
