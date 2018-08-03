package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.TreatmentSummary;
import com.varian.oiscn.anticorruption.assembler.TreatmentSummaryAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTreatmentSummaryInterface;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.treatmentsummary.PlanStatusEnum;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterEndPlan;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import org.joda.time.DateTimeComparator;

import java.util.*;

/**
 * Treatment Summary Service Implementation.<br>
 */
public class TreatmentSummaryAntiCorruptionServiceImp {
    private FHIRTreatmentSummaryInterface fhirTreatmentSummaryInterface;

    /**
     * Default Constructor.<br>
     */
    public TreatmentSummaryAntiCorruptionServiceImp() {
        this.fhirTreatmentSummaryInterface = new FHIRTreatmentSummaryInterface();
    }

    /**
     * Return Treatment Summary DTO.<br>
     *
     * @param patientId Patient Id
     * @return Treatment Summary DTO
     */
    public Optional<TreatmentSummaryDto> getTxSummaryByPatientId(String patientId) {
        TreatmentSummary treatmentSummary = fhirTreatmentSummaryInterface.getTreatmentSummary(patientId);
        if (treatmentSummary == null || treatmentSummary.getCourses() == null || treatmentSummary.getCourses().size() == 0) {
            return Optional.empty();
        }
        TreatmentSummaryDto treatmentSummaryDto = TreatmentSummaryAssembler.getTreatmentSummaryDto(treatmentSummary);
        if(treatmentSummaryDto != null && treatmentSummaryDto.getPlans() != null){
            Collections.sort(treatmentSummaryDto.getPlans(), Comparator.comparing(PlanSummaryDto::getPlanSetupId));
        }
        return Optional.of(treatmentSummaryDto);
    }
    /**
     * Return Approved Treatment Summary DTO.<br>
     *
     * @param patientId Patient Id
     * @return Treatment Summary DTO
     */
    public Optional<TreatmentSummaryDto> getApproveTxSummaryByPatientId(String patientId) {
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = this.getTxSummaryByPatientId(patientId);
        if(treatmentSummaryDtoOptional.isPresent()){
            treatmentSummaryDtoOptional.get().setLastTreatmentDate(null);
        }
        if(treatmentSummaryDtoOptional.isPresent() && treatmentSummaryDtoOptional.get().getPlans() != null){
//              需要将未审批的计划过滤掉
            Iterator<PlanSummaryDto> it = treatmentSummaryDtoOptional.get().getPlans().iterator();
            while(it.hasNext()){
                PlanSummaryDto dto = it.next();
                if(!PlanStatusEnum.PlanApproval.equals(dto.getStatus()) && !PlanStatusEnum.TreatApproval.equals(dto.getStatus())
                        && !PlanStatusEnum.Completed.equals(dto.getStatus())){
                    it.remove();
                }
            }
//        计算最后治疗日期
          Optional<Date> lastTreatmentDate = treatmentSummaryDtoOptional.get().getPlans().stream().filter(i -> i.getLastTreatmentTime()!=null).map(i -> i.getLastTreatmentTime()).max(DateTimeComparator.getInstance());
          if(lastTreatmentDate.isPresent()){
              treatmentSummaryDtoOptional.get().setLastTreatmentDate(lastTreatmentDate.get());
          }
        }
        return treatmentSummaryDtoOptional;
    }

    /**
     *
     * @param patientId
     * @param encounterId
     * @return
     */
    public Optional<TreatmentSummaryDto> getApproveTxSummaryByPatientIdAndEncounterId(String patientId,String encounterId){
        //判断是否是当前EncounterId
        PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(patientId);
        if(patientEncounterCarePath != null) {
            EncounterCarePathList encounterCarePathList = patientEncounterCarePath.getPlannedCarePath();
            if (encounterCarePathList != null) {
                String currentEncounterId = String.valueOf(encounterCarePathList.getEncounterId());
                if (encounterId.equals(currentEncounterId)) {
                    return getActivityEncounterTxSummaryByPatientSer(patientId);
                }
            }
        }
        Optional<TreatmentSummaryDto> treatmentSummaryDto = this.getApproveTxSummaryByPatientId(patientId);
        if (treatmentSummaryDto.isPresent()) {
            List<PlanSummaryDto> planSummaryDtoList = treatmentSummaryDto.get().getPlans();
            if(planSummaryDtoList != null){
                PatientEncounterEndPlan patientEncounterEndPlan = PatientEncounterHelper.getEncounterEndPlanByPatientSer(patientId);
                List<EncounterEndPlan> completedPlanList = patientEncounterEndPlan.getCompletedPlan();
                Set<String> currentEncounterPlanSet = new HashSet<>();
                if(completedPlanList != null && !completedPlanList.isEmpty()) {
                        List<PlanSummaryDto> tmpList = new ArrayList<>();
                        completedPlanList.forEach(encounterEndPlan -> {
                            if (encounterEndPlan.getEncounterId().compareTo(new Long(encounterId)) == 0) {
                                currentEncounterPlanSet.add(encounterEndPlan.getPlanSetupId() + "_" + encounterEndPlan.getPlanCreatedDt());
                            }
                        });
                        planSummaryDtoList.forEach(planSummaryDto -> {
                            if (currentEncounterPlanSet.contains(planSummaryDto.getPlanSetupId() + "_" + planSummaryDto.getCreatedDt())) {
                                tmpList.add(planSummaryDto);
                            }
                        });
                        treatmentSummaryDto.get().setPlans(tmpList);
                    }
                }
            }
            return treatmentSummaryDto;
        }




    /**
     * Return Activity Encounter Approved Treatment Summary DTO.<br>
     * @param patientSer
     * @return
     */
    public Optional<TreatmentSummaryDto> getActivityEncounterTxSummaryByPatientSer(String patientSer){
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = this.getApproveTxSummaryByPatientId(patientSer);
        if(treatmentSummaryDtoOptional.isPresent()){
            treatmentSummaryDtoOptional.get().setLastTreatmentDate(null);
        }
        if(treatmentSummaryDtoOptional.isPresent() && treatmentSummaryDtoOptional.get().getPlans() != null){
            PatientEncounterEndPlan patientEncounterEndPlan = PatientEncounterHelper.getEncounterEndPlanByPatientSer(patientSer);
            if(patientEncounterEndPlan != null && !patientEncounterEndPlan.getCompletedPlan().isEmpty()){
                Iterator<PlanSummaryDto> it = treatmentSummaryDtoOptional.get().getPlans().iterator();
                while(it.hasNext()){
                    PlanSummaryDto dto = it.next();
                    for(EncounterEndPlan encounterEndPlan :patientEncounterEndPlan.getCompletedPlan()){
                        if(dto.getPlanSetupId().equals(encounterEndPlan.getPlanSetupId())
                                && dto.getCreatedDt().equals(encounterEndPlan.getPlanCreatedDt())){
                            it.remove();
                            break;
                        }
                    }
                }
            }
//        计算最后治疗日期
            Optional<Date> lastTreatmentDate = treatmentSummaryDtoOptional.get().getPlans().stream().filter(i -> i.getLastTreatmentTime()!=null).map(i -> i.getLastTreatmentTime()).max(DateTimeComparator.getInstance());
            if(lastTreatmentDate.isPresent()){
                treatmentSummaryDtoOptional.get().setLastTreatmentDate(lastTreatmentDate.get());
            }
        }
        return treatmentSummaryDtoOptional;
    }
}
