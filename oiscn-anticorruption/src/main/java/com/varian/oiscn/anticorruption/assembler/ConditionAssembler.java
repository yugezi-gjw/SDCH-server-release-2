package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Condition;
import com.varian.oiscn.core.patient.Diagnosis;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.codesystems.ConditionCategory;
import org.hl7.fhir.exceptions.FHIRException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.varian.oiscn.anticorruption.converter.DataHelper.getReferenceValue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by fmk9441 on 2017-05-16.
 */
@Slf4j
public class ConditionAssembler {
    private static final String CODE_SYSTEM_CONDITION_PRIMARY_SITE = "http://varian.com/valueset/condition-primarysite";

    private ConditionAssembler() {

    }

    /**
     * Return Fhir Condition from Diagnosis.<br>
     *
     * @param diagnosis Diagnosis
     * @return Fhir Condition
     */
    public static Condition getCondition(Diagnosis diagnosis) {
        Condition condition = new Condition();
        condition.addCategory(new CodeableConcept().setText(ConditionCategory.ENCOUNTERDIAGNOSIS.toCode()));
        condition.setVerificationStatus(Condition.ConditionVerificationStatus.CONFIRMED);
        condition.setRank(new StringType("1"));
        if (diagnosis != null) {
            setDiagnosisSubject(condition, diagnosis);
            setDiagnosisRecurrence(condition, diagnosis);
            setDiagnosisCodeAndDesc(condition, diagnosis);
            setDiagnosisDate(condition, diagnosis);
            setDiagnosisBodypart(condition, diagnosis);
            setDiagnosisStaging(condition, diagnosis);
            setDiagnosisNote(condition, diagnosis);
        }
        return condition;
    }

    /**
     * Return Diagnosis from Fhir Condition.<br>
     * @param condition Fhir Condition
     * @return Diagnosis
     */
    public static Diagnosis getDiagnosis(Condition condition) {
        Diagnosis diagnosis = null;
        if (condition != null && condition.hasCategory() && condition.getCategory().stream().filter(x -> x.getText().equals(ConditionCategory.ENCOUNTERDIAGNOSIS.toCode())).findAny().isPresent()) {
            diagnosis = new Diagnosis();
            getDiagnosisSubject(condition, diagnosis);
            getDiagnosisCodeAndDesc(condition, diagnosis);
            getDiagnosisRecurrence(condition, diagnosis);
            getDiagnosisStaging(condition, diagnosis);
            getDiagnosisDate(condition, diagnosis);
            getDiagnosisBodypart(condition, diagnosis);
            getDiagnosisNote(condition, diagnosis);
        }
        return diagnosis;
    }

    /**
     * Update Fhir Condition from Diagnosis.<br>
     * @param condition Fhir Condition
     * @param diagnosis Diagnosis
     */
    public static void updateCondition(Condition condition, Diagnosis diagnosis) {
        if (condition != null && condition != null) {
            setDiagnosisCodeAndDesc(condition, diagnosis);
            setDiagnosisRecurrence(condition, diagnosis);
            setDiagnosisBodypart(condition, diagnosis);
            setDiagnosisDate(condition, diagnosis);
            setDiagnosisStaging(condition, diagnosis);
            setDiagnosisNote(condition, diagnosis);
        }
    }

    private static void setDiagnosisSubject(Condition condition, Diagnosis diagnosis) {
        if (isNotEmpty(diagnosis.getPatientID())) {
            condition.setSubject(new Reference().setReference(diagnosis.getPatientID()));
        }
    }

    private static void getDiagnosisSubject(Condition condition, Diagnosis diagnosis) {
        if (condition.hasSubject()) {
            diagnosis.setPatientID(getReferenceValue(condition.getSubject().getReference()));
        }
    }

    private static void setDiagnosisCodeAndDesc(Condition condition, Diagnosis diagnosis) {
        CodeableConcept codeableConcept;
        if (condition.hasCode()) {
            codeableConcept = condition.getCode();
            if (!codeableConcept.hasCoding() && isNotEmpty(diagnosis.getCode()) && isNotEmpty(diagnosis.getSystem())) {
                codeableConcept.setCoding(Arrays.asList(new Coding().setSystem(diagnosis.getSystem()).setCode(diagnosis.getCode())));
            }
        } else {
            codeableConcept = new CodeableConcept();
            if (isNotEmpty(diagnosis.getCode()) && isNotEmpty(diagnosis.getSystem())) {
                codeableConcept.addCoding(new Coding().setSystem(diagnosis.getSystem()).setCode(diagnosis.getCode()));
            }
        }
        if (isNotEmpty(diagnosis.getDesc())) {
            codeableConcept.setText(diagnosis.getDesc());
        }
        condition.setCode(codeableConcept);
    }

    private static void getDiagnosisCodeAndDesc(Condition condition, Diagnosis diagnosis) {
        if (condition.hasCode()) {
            CodeableConcept codeableConcept = condition.getCode();
            if (codeableConcept.hasText()) {
                diagnosis.setDesc(condition.getCode().getText());
            }
            if (codeableConcept.hasCoding()) {
                diagnosis.setCode(codeableConcept.getCodingFirstRep().getCode());
                diagnosis.setSystem(codeableConcept.getCodingFirstRep().getSystem());
            }
        }
    }

    private static void setDiagnosisDate(Condition condition, Diagnosis diagnosis) {
        if (null != diagnosis.getDiagnosisDate()) {
            condition.setOnset(new DateTimeType(diagnosis.getDiagnosisDate()));
        }
    }

    private static void getDiagnosisDate(Condition condition, Diagnosis diagnosis) {
        if (condition.hasOnset()) {
            try {
                diagnosis.setDiagnosisDate(condition.getOnsetDateTimeType().getValue());
            } catch (FHIRException e) {
                log.error("FHIRException: {}", e.getMessage());
            }
        }
    }

    private static void setDiagnosisRecurrence(Condition condition, Diagnosis diagnosis) {
        if (diagnosis.getRecurrence() != null) {
            condition.setRecurrence(new BooleanType(diagnosis.getRecurrence()));
        } else {
            condition.setRecurrence(null);
        }
    }

    private static void getDiagnosisRecurrence(Condition condition, Diagnosis diagnosis) {
        if (condition.getRecurrence() != null) {
            diagnosis.setRecurrence(condition.getRecurrence().booleanValue());
        } else {
            diagnosis.setRecurrence(null);
        }
    }

    private static void setDiagnosisBodypart(Condition condition, Diagnosis diagnosis) {
        CodeableConcept codeableConcept;
        if (condition.hasBodySite()) {
            codeableConcept = condition.getBodySiteFirstRep();
            if (isNotEmpty(diagnosis.getBodypartCode())) {
                codeableConcept.setCoding(Arrays.asList(new Coding().setSystem(CODE_SYSTEM_CONDITION_PRIMARY_SITE).setCode(diagnosis.getBodypartCode())));
            }
        } else {
            codeableConcept = new CodeableConcept();
            if (isNotBlank(diagnosis.getBodypartCode())) {
                codeableConcept.addCoding().setSystem(CODE_SYSTEM_CONDITION_PRIMARY_SITE).setCode(diagnosis.getBodypartCode());
            }
        }
        condition.setBodySite(Arrays.asList(codeableConcept));
    }

    private static void getDiagnosisBodypart(Condition condition, Diagnosis diagnosis) {
        if (condition.hasBodySite() && condition.getBodySiteFirstRep().hasCoding()) {
            Optional<Coding> optional = condition.getBodySiteFirstRep().getCoding().stream().filter(x -> x.hasSystem() && x.getSystem().equals(CODE_SYSTEM_CONDITION_PRIMARY_SITE)).findAny();
            if (optional.isPresent()) {
                diagnosis.setBodypartCode(optional.get().getCode());
                diagnosis.setBodypartDesc(optional.get().getDisplay());
            }
        }
    }

    private static void setDiagnosisStaging(Condition condition, Diagnosis diagnosis) {
        Condition.DiagnosisStage diagnosisStage;
        if (condition.getDiagnosisStages().isEmpty()) {
            if (null != diagnosis.getStaging()) {
                diagnosisStage = new Condition.DiagnosisStage();
                setDiagnosisStage(condition, diagnosisStage, diagnosis);
            }
        } else {
            diagnosisStage = condition.getDiagnosisStages().get(0);
            setDiagnosisStage(condition, diagnosisStage, diagnosis);
        }
    }

    private static void setDiagnosisStage(Condition condition, Condition.DiagnosisStage diagnosisStage, Diagnosis diagnosis) {
        diagnosisStage.setBasis(new CodeableConcept().addCoding(new Coding().setCode("P")));
        diagnosisStage.setActive(new BooleanType(true));
        diagnosisStage.setApprovalStatus(new BooleanType(true));
        diagnosisStage.setOnset(new DateTimeType(diagnosis.getDiagnosisDate()));
        diagnosisStage.setStageScheme(new StringType(diagnosis.getStaging().getSchemeName()));

        Condition.DiagnosisStageCriteria diagnosisStageCriteria = new Condition.DiagnosisStageCriteria();
        diagnosisStageCriteria.setStageCode(new ArrayList<>());
        CodeableConcept tCode = new CodeableConcept();
        tCode.addCoding().setSystem("T").setCode(diagnosis.getStaging().getTcode());
        diagnosisStageCriteria.getStageCode().add(tCode);
        CodeableConcept nCode = new CodeableConcept();
        nCode.addCoding().setSystem("N").setCode(diagnosis.getStaging().getNcode());
        diagnosisStageCriteria.getStageCode().add(nCode);
        CodeableConcept mCode = new CodeableConcept();
        mCode.addCoding().setSystem("M").setCode(diagnosis.getStaging().getMcode());
        diagnosisStageCriteria.getStageCode().add(mCode);
        diagnosisStage.setStageCriteria(diagnosisStageCriteria);
        diagnosisStage.setWorkingIndicator(new BooleanType(true));

        condition.getDiagnosisStages().add(diagnosisStage);
    }

    private static void getDiagnosisStaging(Condition condition, Diagnosis diagnosis) {
        if (null != condition.getDiagnosisStages() && !condition.getDiagnosisStages().isEmpty()) {
            Diagnosis.Staging staging = new Diagnosis.Staging();
            Condition.DiagnosisStage diagnosisStage = condition.getDiagnosisStages().get(0);
            if (null != diagnosisStage.getOnset()) {
                staging.setDate(diagnosisStage.getOnset().getValue());
            }
            if (null != diagnosisStage.getSummary()) {
                staging.setStage(diagnosisStage.getSummary().getValue());
            }
            if (null != diagnosisStage.getBasis()) {
                staging.setBasisCode(diagnosisStage.getBasis().getCodingFirstRep().getCode());
            }
            if (null != diagnosisStage.getStageCriteria()) {
                getStagingItem(staging, diagnosisStage.getStageCriteria().getStageCode());
            }
            diagnosis.setStaging(staging);
        }
    }

    private static void getStagingItem(Diagnosis.Staging staging, List<CodeableConcept> stageCodeList) {
        if (stageCodeList != null && !stageCodeList.isEmpty()) {
            stageCodeList.forEach(codeableConcept -> {
                if (codeableConcept.hasCoding()
                        && codeableConcept.getCodingFirstRep().hasSystem()) {
                    if (codeableConcept.getCodingFirstRep().getSystem().endsWith("T")) {
                        staging.setTcode(codeableConcept.getCodingFirstRep().getCode());
                    } else if (codeableConcept.getCodingFirstRep().getSystem().endsWith("M")) {
                        staging.setMcode(codeableConcept.getCodingFirstRep().getCode());
                    } else if (codeableConcept.getCodingFirstRep().getSystem().endsWith("N")) {
                        staging.setNcode(codeableConcept.getCodingFirstRep().getCode());
                    }
                }
            });
        }
    }

    private static void setDiagnosisNote(Condition condition, Diagnosis diagnosis) {
        if (condition.hasNote()) {
            condition.getNoteFirstRep().setText(diagnosis.getDiagnosisNote());
        } else {
            condition.addNote().setText(diagnosis.getDiagnosisNote());
        }
    }

    private static void getDiagnosisNote(Condition condition, Diagnosis diagnosis) {
        if (condition.hasNote()) {
            diagnosis.setDiagnosisNote(condition.getNoteFirstRep().getText());
        }
    }
}