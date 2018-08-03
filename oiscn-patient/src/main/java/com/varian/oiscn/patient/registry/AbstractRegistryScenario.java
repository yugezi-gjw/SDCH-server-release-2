package com.varian.oiscn.patient.registry;

import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.patient.assembler.CoverageAssembler;
import com.varian.oiscn.patient.assembler.PatientRegistryAssembler;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public abstract class AbstractRegistryScenario implements IPatientRegistry {
    protected Patient patient;
    protected Encounter encounter;
    protected String scenarioFlag;
    protected Map<String, String> dynamicFormItems;

    public AbstractRegistryScenario(Patient patient, Encounter encounter) {
        this(patient, encounter, null);
    }

    public AbstractRegistryScenario(Patient patient, Encounter encounter, Map<String, String> dynamicFormItems) {
        this.patient = patient;
        this.encounter = encounter;
        this.dynamicFormItems = dynamicFormItems;
    }

    @Override
    public RegistryVerifyStatusEnum verifyRegistry() {
        return RegistryVerifyStatusEnum.PASS;
    }

    public PatientRegistrationVO getPatientRegistrationVO() {
        PatientRegistrationVO vo = new PatientRegistrationVO();
        vo.setPatient(patient);
        vo.setEncounter(encounter);
        vo.setScenarioFlag(scenarioFlag);
        vo.setDynamicFormItems(dynamicFormItems);
        return vo;
    }

    protected Long createPatient2ARIA(Configuration configuration) {
        PatientDto patientDto = PatientRegistryAssembler.getPatientDto(patient, encounter, configuration);
        PatientAntiCorruptionServiceImp antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        String id = antiCorruptionServiceImp.createPatient(patientDto);
        if (isNotEmpty(id)) {
            return new Long(id);
        }
        return null;
    }

    protected boolean updatePatient2ARIA(Configuration configuration) {
        PatientDto patientDto = PatientRegistryAssembler.getPatientDto(patient, encounter, configuration);
        PatientAntiCorruptionServiceImp antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        return isNotEmpty(antiCorruptionServiceImp.update(patient.getPatientSer(), patientDto));
    }

    protected boolean createDiagnosis2ARIA(Long patientSer, Configuration configuration) {
        if (encounter.getDiagnoses() != null && !encounter.getDiagnoses().isEmpty()) {
            Diagnosis diagnosis = encounter.getDiagnoses().get(0);
            diagnosis.setSystem(configuration.getDiagnosisCodeScheme());
            diagnosis.setPatientID(patientSer.toString());

            Diagnosis.Staging staging = diagnosis.getStaging();
            if (staging != null && isNotEmpty(staging.getTcode())
                    && isNotEmpty(staging.getMcode())
                    && isNotEmpty(staging.getNcode())) {
                diagnosis.getStaging().setSchemeName(configuration.getStagingCodeScheme());
            } else {
                diagnosis.setStaging(null);
            }

            DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp = new DiagnosisAntiCorruptionServiceImp();
            return !isEmpty(diagnosisAntiCorruptionServiceImp.createDiagnosis(diagnosis));
        }
        return true;
    }

    protected boolean markUrgent2ARIA(Long patientSer, Configuration configuration) {
        String urgentStatusCode = StatusIconPool.get(configuration.getUrgentStatusIconDesc());
        if (isEmpty(urgentStatusCode)) {
            log.error("Can't get the urgent status code, please check the urgent config.");
            return false;
        }
        if (encounter.isUrgent()) {
            FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
            return flagAntiCorruptionServiceImp.markPatientStatusIcon(patientSer.toString(), urgentStatusCode);
        }
        return true;
    }

    protected boolean updateUrgent2ARIA(Long patientSer, Configuration configuration) {
        //update urgent
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        boolean isUrgentInDb = flagAntiCorruptionServiceImp.checkPatientStatusIcon(patientSer.toString(),
                StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        if (isUrgentInDb && !encounter.isUrgent()) {
            flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(patientSer.toString(),
                    StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        } else if (!isUrgentInDb && encounter.isUrgent()) {
            flagAntiCorruptionServiceImp.markPatientStatusIcon(patientSer.toString(),
                    StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        }
        return true;
    }

    protected boolean markActiveStatus2ARIA(Long patientSer, Configuration configuration) {
        String activeStatusCode = StatusIconPool.get(configuration.getActiveStatusIconDesc());
        if (isEmpty(activeStatusCode)) {
            log.error("Can't get the active status code, please check the active config.");
            return false;
        }
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        boolean isActive = flagAntiCorruptionServiceImp.checkPatientStatusIcon(patientSer.toString(),
                StatusIconPool.get(configuration.getActiveStatusIconDesc()));
        if (!isActive) {
            return flagAntiCorruptionServiceImp.markPatientStatusIcon(patientSer.toString(), activeStatusCode);
        }
        return true;
    }

    protected boolean createCoverage2ARIA(Long patientSer) {
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
        CoverageDto coverageDto = CoverageAssembler.getCoverageDto(patientSer.toString(), encounter.getInsuranceTypeCode());
        return coverageDto == null || isNotEmpty(coverageAntiCorruptionServiceImp.createCoverage(coverageDto));
    }

    protected boolean updateCoverage2ARIA(Long patientSer) {
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
        CoverageDto coverageDto = CoverageAssembler.getCoverageDto(patientSer.toString(), encounter.getInsuranceTypeCode());
        coverageAntiCorruptionServiceImp.updateCoverage(coverageDto);
//      TODO  when update coverage,Fhir throw Exception,but save the coverage success,so return true forever
//        return coverageDto == null || isNotEmpty(coverageAntiCorruptionServiceImp.updateCoverage(coverageDto));
        return true;
    }

    protected String linkCarePathAndAdd2Encounter(Long patientSer, Configuration configuration) {
        String cpTemplateId = isNotEmpty(encounter.getCpTemplateId()) ? encounter.getCpTemplateId() : configuration.getDefaultCarePathTemplateName();
        String carePathId = linkCarePath2ARIA(patientSer, SystemConfigPool.queryDefaultDepartment(), cpTemplateId);
        if (isNotEmpty(carePathId)) {
            encounter.addEncounterCarePath(carePathId);
        }
        return carePathId;
    }

    protected String linkCarePath2ARIA(Long patientSer, String departmentId, String carePathTemplateId) {
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        return carePathAntiCorruptionServiceImp.linkCarePath(patientSer.toString(), departmentId, carePathTemplateId);
    }

    protected void putPatient2Cache(Long patientSer) {
        PatientAntiCorruptionServiceImp antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        PatientDto createdPatientDto = antiCorruptionServiceImp.queryPatientByPatientId(patientSer.toString());
        PatientCache.put(patientSer.toString(), createdPatientDto);
    }
}
