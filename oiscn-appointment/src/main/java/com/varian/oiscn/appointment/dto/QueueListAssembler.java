package com.varian.oiscn.appointment.dto;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.FlagAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.appointment.vo.QueueListVO;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 10/23/2017.
 */
public class QueueListAssembler {
    private List<QueuingManagementVO> queue;

    private Map<String, PatientDto> patientDtoMap;

    private Map<String, Boolean> urgentMap;

    private Map<String,AppointmentDto> appointmentDtoMap;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp;

    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    private EncounterServiceImp encounterServiceImp;

    private Map<String, Encounter> encounterMap;

    private Map<String,Boolean> confirmPaymentMap;

    private String alertTag;

    public QueueListAssembler(List<QueuingManagementVO> queue, Configuration configuration, UserContext userContext) {
        this.queue = queue;
        this.alertTag = configuration.getAlertPatientLabelDesc();
        patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        encounterServiceImp = new EncounterServiceImp(userContext);
        appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
        List<String> patientIdList = getPatientIdList();
        patientDtoMap = getPatientDtoMapByPatientIdList(patientIdList);
        urgentMap = getUrgentMap(patientIdList, StatusIconPool.get(configuration.getUrgentStatusIconDesc()));
        appointmentDtoMap = getAppointmentDtoMapByInstanceId(queue);
        confirmPaymentMap = getConfirmPaymentMap(queue);
    }

   private Map<String,Boolean> getConfirmPaymentMap(List<QueuingManagementVO> queue){
        if(!queue.isEmpty()) {
            List<String> patientSerList = new ArrayList<>();
            queue.forEach(queuingManagementVO -> patientSerList.add(String.valueOf(queuingManagementVO.getPatientSer())));
            return confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(patientSerList, queue.get(0).getActivityCode(),queue.get(0).getDeviceId());
        }
        return new HashMap<>();
    }

    private Map<String,AppointmentDto> getAppointmentDtoMapByInstanceId(List<QueuingManagementVO> queue){
         Map<String,AppointmentDto> map = new HashMap<>();
        queue.forEach(queuingManagementVO -> {
            String appointmentId = queuingManagementVO.getAppointmentId();
            if(StringUtils.isNumeric(appointmentId)){
                map.put(appointmentId,appointmentAntiCorruptionServiceImp.queryAppointmentById(appointmentId));
            }else{
                map.put(appointmentId,new AppointmentDto(){{
                    setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
                }});
            }
        });
        return map;
    }

    public List<QueueListVO> getQueue() {
        List<QueueListVO> result = new ArrayList<>();
        QueueListVO instanceVO;
        for (QueuingManagementVO appointmentDto : queue) {
            instanceVO = new QueueListVO();
            instanceVO.setScheduleTime(DateUtil.formatDate(appointmentDto.getStartTime(), DateUtil.HOUR_MINUTE_AM_FORMAT));
            instanceVO.setStartTime(appointmentDto.getStartTime());
            assemblerActivityDataFromAppointment(instanceVO, appointmentDto);
            assemblerPatientData(instanceVO, patientDtoMap.get(String.valueOf(appointmentDto.getPatientSer())), urgentMap);
            assemblerAppointmentStatus(instanceVO,appointmentDtoMap);
            instanceVO.setConfirmedPayment(confirmPaymentMap.get(instanceVO.getPatientSer()));
            result.add(instanceVO);
        }
        return result;
    }

    private void assemblerPatientData(QueueListVO instanceVO, PatientDto patientDto, Map<String, Boolean> urgentMap) {
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
        instanceVO.setInsuranceType(patientDto.getInsuranceType());
        instanceVO.setPatientSource(patientDto.getPatientSource());

        List<PatientDto.PatientLabel> labels = patientDto.getLabels();
        if (labels != null) {
            Optional<PatientDto.PatientLabel> alertLabel = labels.stream().filter(patientLabel -> StringUtils.equals(alertTag, patientLabel.getLabelTag())).findAny();
            if (alertLabel.isPresent()) {
                instanceVO.setWarningText(alertLabel.get().getLabelText());
            }
        }
        instanceVO.setUrgent(urgentMap.containsKey(patientDto.getPatientSer()) ? urgentMap.get(patientDto.getPatientSer()) : false);
        instanceVO.setPhysicianComment(getEncounterMap().get(patientDto.getHisId()) != null? getEncounterMap().get(patientDto.getHisId()).getPhysicianComment():null);
        instanceVO.setAge(getEncounterMap().get(patientDto.getHisId()) != null? getEncounterMap().get(patientDto.getHisId()).getAge():null);
    }

    private Map<String, Encounter> getEncounterMap() {
        if (encounterMap == null) {
            List<String> hisIdList = getHisIdList();
            encounterMap = this.encounterServiceImp.queryPatientSerEncounterMapByPatientSerList(hisIdList);
        }
        return encounterMap;
    }

    private void assemblerAppointmentStatus(QueueListVO instanceVO, Map<String,AppointmentDto> appointmentDtoMap){
        AppointmentDto appointmentDto = appointmentDtoMap.get(instanceVO.getInstanceId());
        if(appointmentDto != null){
            instanceVO.setStatus(appointmentDto.getStatus().toLowerCase());
        }
    }

    private void assemblerActivityDataFromAppointment(QueueListVO instanceVO, QueuingManagementVO appointmentDto) {
        instanceVO.setInstanceId(appointmentDto.getAppointmentId());
        instanceVO.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        instanceVO.setActivityCode(appointmentDto.getActivityCode());
        instanceVO.setActiveInWorkflow(true);
        instanceVO.setCheckInStatus(appointmentDto.getCheckInStatus().name());
        instanceVO.setCheckInIdx(appointmentDto.getCheckInIdx());

        instanceVO.setNextAction(ActivityCodesReader.getActivityCode(appointmentDto.getActivityCode()).getContent());
        instanceVO.setProgressState(StringUtils.EMPTY);
        instanceVO.setWorkspaceType(ActivityCodesReader.getActivityCode(instanceVO.getActivityCode()).getWorkspaceType());
    }

    private List<String> getPatientIdList() {
        List<String> patientIdList = new ArrayList<>();
        queue.forEach(queuingManagementDTO -> patientIdList.add(String.valueOf(queuingManagementDTO.getPatientSer())));
        return patientIdList;
    }

    private List<String> getHisIdList() {
        List<String> hisIdList = new ArrayList<>();
        queue.forEach(queuingManagementDTO -> hisIdList.add(String.valueOf(queuingManagementDTO.getPatientSer())));
        return hisIdList;
    }

    private Map<String, Boolean> getUrgentMap(List<String> patientIdList, String urgentFlagCode) {
        if (isNotEmpty(urgentFlagCode)) {
            return flagAntiCorruptionServiceImp.queryPatientListFlag(patientIdList, urgentFlagCode);
        } else {
            return new HashMap<>();
        }
    }

    private Map<String, PatientDto> getPatientDtoMapByPatientIdList(List<String> patientIdList) {
        return patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(patientIdList);
    }
}
