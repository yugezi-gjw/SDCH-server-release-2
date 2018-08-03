package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.CoverageAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.util.I18nReader;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 6/8/2017.
 */
public abstract class AbstractActivityInstanceAssembler implements ActivityInstanceAssembled {

    protected static final String REGISTERED = I18nReader.getLocaleValueByKey("AbstractActivityInstanceAssembler.registered");

    private String urgentFlagCode;

    private String alertTag;

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    private EncounterServiceImp encounterServiceImp;
    /**
     * key:hisId,value: encounter
     */
    private Map<String, Encounter> encounterMap;

    private Map<String,CoverageDto> insuranceMap;

    protected AbstractActivityInstanceAssembler(Configuration configuration, UserContext userContext) {
        flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        this.urgentFlagCode = StatusIconPool.get(configuration.getUrgentStatusIconDesc());
        this.alertTag = configuration.getAlertPatientLabelDesc();
        this.confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
        this.encounterServiceImp = new EncounterServiceImp(userContext);
        coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
    }

    protected Map<String, Boolean> getConfirmedPaymentMap() {
        List<String> hisIdList = getPatientSerList();
        return confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(hisIdList);
    }

    abstract List<String> getPatientIdList();

    abstract List<String> getPatientSerList();

    protected Map<String, Boolean> getUrgentMap() {
        if (isNotEmpty(urgentFlagCode)) {
            return flagAntiCorruptionServiceImp.queryPatientListFlag(getPatientIdList(), urgentFlagCode);
        } else {
            return new HashMap<>();
        }
    }

    protected void assemblerPatientData(ActivityInstanceVO instanceVO, PatientDto patientDto, Map<String, Boolean> urgentMap, Map<String, Boolean> paymentMap) {
        instanceVO.setPatientSer(patientDto.getPatientSer());
        instanceVO.setAriaId(patientDto.getAriaId());
        instanceVO.setHisId(patientDto.getHisId());
        instanceVO.setNationalId(patientDto.getNationalId());
        instanceVO.setChineseName(patientDto.getChineseName());
        instanceVO.setEnglishName(patientDto.getEnglishName());
        instanceVO.setGender(patientDto.getGender());
        instanceVO.setBirthday(patientDto.getBirthday());
        instanceVO.setTelephone(patientDto.getTelephone());
        instanceVO.setContactPerson(patientDto.getContactPerson());
        instanceVO.setContactPhone(patientDto.getContactPhone());
        instanceVO.setPhysicianGroupId(patientDto.getPhysicianGroupId());
        instanceVO.setPhysicianId(patientDto.getPhysicianId());
        instanceVO.setPhysicianName(patientDto.getPhysicianName());
        instanceVO.setPhysicianPhone(patientDto.getPhysicianPhone());
        // Add new items (Map Should Be Better way)...Insurance Type,
//        CoverageDto coverageDto = this.getInsuranceMap().get(patientDto.getPatientSer());
//        if(coverageDto!=null) {
//            instanceVO.setInsuranceType(coverageDto.getInsuranceTypeDesc());
//            patientDto.setInsuranceType(coverageDto.getInsuranceTypeDesc());
//        }
        instanceVO.setPatientSource(patientDto.getPatientSource());
        
        if (StringUtils.equals(REGISTERED, instanceVO.getProgressState())) {
            instanceVO.setPreActivityCompletedTime(patientDto.getCreatedDT());
        }
        List<PatientDto.PatientLabel> labels = patientDto.getLabels();
        if (labels != null) {
            Optional<PatientDto.PatientLabel> alertLabel = labels.stream().filter(patientLabel -> StringUtils.equals(alertTag, patientLabel.getLabelTag())).findAny();
            if (alertLabel.isPresent()) {
                instanceVO.setWarningText(alertLabel.get().getLabelText());
            }
        }
        instanceVO.setUrgent(urgentMap.containsKey(patientDto.getPatientSer()) ? urgentMap.get(patientDto.getPatientSer()) : false);
        instanceVO.setConfirmedPayment(
                paymentMap.containsKey(patientDto.getPatientSer()) ? paymentMap.get(patientDto.getPatientSer()) : false);
        Encounter encounter = this.getPatientSerEncounterMap().get(patientDto.getPatientSer());
        if(encounter != null) {
            instanceVO.setInsuranceType(encounter.getInsuranceType());
            patientDto.setInsuranceType(encounter.getInsuranceType());
            instanceVO.setPhysicianComment(encounter.getPhysicianComment());
            instanceVO.setAge(encounter.getAge());
            instanceVO.setPhysicianBId(encounter.getPhysicianBId());
            instanceVO.setPhysicianBName(encounter.getPhysicianBName());
            instanceVO.setPhysicianCId(encounter.getPhysicianCId());
            instanceVO.setPhysicianCName(encounter.getPhysicianCName());
            if(StringUtils.isEmpty(instanceVO.getInsuranceType())){
                instanceVO.setInsuranceType(encounter.getInsuranceType());
                patientDto.setInsuranceType(encounter.getInsuranceType());
            }
        }
    }

    private Map<String, Encounter> getPatientSerEncounterMap() {
        if (encounterMap == null) {
            List<String> patientSerList = getPatientSerList();
            encounterMap = this.encounterServiceImp.queryPatientSerEncounterMapByPatientSerList(patientSerList);
        }
        return encounterMap;
    }

//    private Map<String,CoverageDto> getInsuranceMap(){
//        if(insuranceMap == null){
//            List<String> serList = getPatientSerList();
//            int countPerPage = Integer.MAX_VALUE;
//            int pageNumberTo = Integer.MAX_VALUE;
//            List<String> patientSerList = new ArrayList<>();
//            serList.forEach(patientSer ->patientSerList.add(patientSer));
//            List<CoverageDto> rlist = new ArrayList<>();
//            Pagination<CoverageDto> pagination = coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(patientSerList, countPerPage, pageNumberTo);
//            rlist.addAll(pagination.getLstObject());
//            insuranceMap = new HashMap<>();
//            rlist.forEach(coverageDto -> insuranceMap.put(coverageDto.getPatientSer(),coverageDto));
//        }
//        return insuranceMap;
//    }
}
