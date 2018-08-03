package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.carepath.vo.ActivityInstanceVO;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/8/2017.
 */
@Slf4j
public class ActivityInstanceForAppointmentAssembler extends AbstractActivityInstanceAssembler {

    private List<AppointmentDto> appointmentDtoList;

    private Map<String, PatientDto> patientDtoMap;

    private Map<String, Boolean> urgentMap;

    private Map<String, Boolean> paymentMap;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private ConfirmPaymentServiceImp confirmPaymentServiceImp;

    public ActivityInstanceForAppointmentAssembler(List<AppointmentDto> appointmentDtoList, Configuration configuration, UserContext userContext) {
        super(configuration, userContext);
        this.appointmentDtoList = appointmentDtoList;
        this.patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        this.confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
    }

    @Override
    public List<ActivityInstanceVO> getActivityInstances() {
        List<ActivityInstanceVO> result = new ArrayList<>();
        ActivityInstanceVO instanceVO;
        List<String> patientIdList = getPatientIdList();
        patientDtoMap = getPatientDtoMapByPatientIdList(patientIdList);
        urgentMap = getUrgentMap();
        paymentMap = getConfirmedPaymentMap();
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            instanceVO = new ActivityInstanceVO();
            instanceVO.setScheduleTime(DateUtil.formatDate(appointmentDto.getStartTime(), DateUtil.HOUR_MINUTE_AM_FORMAT));
            instanceVO.setStartTime(appointmentDto.getStartTime());
            assemblerActivityDataFromAppointment(instanceVO, appointmentDto);
            for (ParticipantDto participantDto : appointmentDto.getParticipants()) {
                if (participantDto.getType() == ParticipantTypeEnum.PATIENT) {
                    assemblerPatientData(instanceVO, patientDtoMap.get(participantDto.getParticipantId()), urgentMap, paymentMap);
                    break;
                }
            }
            result.add(instanceVO);
        }
        return result;
    }

    private Map<String, PatientDto> getPatientDtoMapByPatientIdList(List<String> patientIdList) {
        return patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(patientIdList);
    }

    @Override
    public List<String> getPatientIdList() {
        List<String> patientIdList = new ArrayList<>();
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            appointmentDto.getParticipants().forEach(participantDto -> {
                if (participantDto.getType() == ParticipantTypeEnum.PATIENT) {
                    patientIdList.add(participantDto.getParticipantId());
                }
            });
        }
        return patientIdList;
    }

    private void assemblerActivityDataFromAppointment(ActivityInstanceVO instanceVO, AppointmentDto appointmentDto) {
        instanceVO.setInstanceId(appointmentDto.getAppointmentId());
        instanceVO.setActivityType(ActivityTypeEnum.APPOINTMENT.name());
        instanceVO.setActivityCode(appointmentDto.getReason());
        instanceVO.setActiveInWorkflow(true);

        instanceVO.setNextAction(ActivityCodesReader.getActivityCode(appointmentDto.getReason()).getContent());
        instanceVO.setProgressState(StringUtils.EMPTY);
        instanceVO.setWorkspaceType(ActivityCodesReader.getActivityCode(instanceVO.getActivityCode()).getWorkspaceType());
    }

    @Override
    List<String> getPatientSerList() {
        List<String> hisIdList = new ArrayList<>();
        patientDtoMap.values().forEach(patientDto -> hisIdList.add(patientDto.getPatientSer()));
        return hisIdList;
    }

    @Override
    protected Map<String, Boolean> getConfirmedPaymentMap() {
        if (this.appointmentDtoList.isEmpty()) {
            return new HashMap<>();
        }
        String code = this.appointmentDtoList.get(0).getReason();
        List<ParticipantDto> participantDtoList = this.appointmentDtoList.get(0).getParticipants();
        String deviceId = null;
        for(ParticipantDto dto :participantDtoList){
            if(dto.getType().equals(ParticipantTypeEnum.DEVICE)){
                deviceId = dto.getParticipantId();
                break;
            }
        }
        List<String> hisIdList = this.getPatientSerList();
        return this.confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(hisIdList,code,deviceId);
    }
}
