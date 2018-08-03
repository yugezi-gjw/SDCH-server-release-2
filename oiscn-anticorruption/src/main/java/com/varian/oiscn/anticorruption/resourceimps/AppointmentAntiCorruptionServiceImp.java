package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.assembler.AppointmentAssembler;
import com.varian.oiscn.anticorruption.converter.EnumAppointmentQuery;
import com.varian.oiscn.anticorruption.converter.EnumMatchQuery;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRAppointmentInterface;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRTaskInterface;
import com.varian.oiscn.cache.AppointmentCache;
import com.varian.oiscn.core.RankEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentRankEnum;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.hl7.fhir.dstu3.model.Reference;

import java.text.ParseException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by fmk9441 on 2017-02-07.
 */
@Slf4j
public class AppointmentAntiCorruptionServiceImp {
    private FHIRAppointmentInterface fhirAppointmentInterface;

    /**
     * Default Constructor.<br>
     */
    public AppointmentAntiCorruptionServiceImp() {
        fhirAppointmentInterface = new FHIRAppointmentInterface();
    }

    /**
     * Create Fhir Appointment From DTO.<br>
     *
     * @param appointmentDto DTO
     * @return new Id
     */
    public String createAppointment(AppointmentDto appointmentDto) {
        Appointment appointment = AppointmentAssembler.getAppointment(appointmentDto);
        String createdAppointmentId = fhirAppointmentInterface.create(appointment);
        if (isNotBlank(createdAppointmentId) && isNotBlank(appointmentDto.getOrderId())) {
            FHIRTaskInterface fhirTaskInterface = new FHIRTaskInterface();
            Task task = fhirTaskInterface.queryById(appointmentDto.getOrderId(), Task.class);
            if (null != task) {
                updateTaskPartOf(task, createdAppointmentId);
                fhirTaskInterface.update(task);
            }
        }
        if (isNotBlank(createdAppointmentId)) {
            appointmentDto.setAppointmentId(createdAppointmentId);
            AppointmentCache.put(appointmentDto);
        }

        return createdAppointmentId;
    }

    private void updateTaskPartOf(Task task, String appointmentId) {
        boolean existed = false;
        if (task.hasPartOf()) {
            for (Reference reference : task.getPartOf()) {
                if (reference.getReference().replace("#", "").equals(appointmentId)) {
                    existed = true;
                    break;
                }
            }
        }

        if (!existed) {
            task.addPartOf(new Reference(appointmentId).setDisplay("Appointment"));
        }
    }

    /**
     * Update Appointment by DTO.<br>
     *
     * @param appointmentDto DTO
     * @return new Id
     */
    public String updateAppointment(AppointmentDto appointmentDto) {
        String updatedAppointmentId = StringUtils.EMPTY;
        Appointment appointment = fhirAppointmentInterface.queryById(appointmentDto.getAppointmentId(), Appointment.class);
        AppointmentDto orginAppointmentDto = AppointmentAssembler.getAppointmentDto(appointment);
        if (null != appointment) {
            AppointmentAssembler.updateAppointment(appointment, appointmentDto);
            updatedAppointmentId = fhirAppointmentInterface.update(appointment);
        }
        if (isNotBlank(updatedAppointmentId)) {
            AppointmentCache.remove(orginAppointmentDto);
            AppointmentCache.put(AppointmentAssembler.getAppointmentDto(appointment));
        }

        return updatedAppointmentId;
    }

    /**
     * Return Appointment DTO by Id.<br>
     *
     * @param appointmentId Appointment Id
     * @return DTO
     */
    public AppointmentDto queryAppointmentById(String appointmentId) {
        AppointmentDto appointmentDto = null;
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, appointmentId));
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        if (!lstAppointment.isEmpty()) {
            appointmentDto = getAppointmentDtos(lstAppointment).get(0);
        }
        return appointmentDto;
    }

    /**
     * Return Appointment DTO List by Order Id.<br>
     *
     * @param orderId Id
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentByOrderId(String orderId) {
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentListByTaskId(orderId);
        return getAppointmentDtos(lstAppointment);
    }

    /**
     * Return Appointment DTO List by Device Id and Date.<br>
     *
     * @param deviceId Device Id
     * @param date     Date
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentListByDeviceIdAndDate(String deviceId, Date date) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(deviceId)));
        if (date != null) {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, date));
        }
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        return getAppointmentDtos(lstAppointment);
    }

    /**
     * Return All Appointment (booked and fulfilled) by Patient Id.<br>
     *
     * @param patientSer Patient Serial No
     * @return Appointment List
     */
    public List<AppointmentDto> queryAllByPatientSer(String patientSer) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientSer));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        Pagination<Appointment> pagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        return getAppointmentDtos(pagination.getLstObject());
    }

    /**
     * Return Appointment DTO List by Patient Id.<br>
     *
     * @param patientId Patient Id
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentListByPatientId(String patientId) {
        return queryAppointmentListByPatientIdAndActivityCode(patientId, null);
    }

    /**
     * Return Appointment DTO List by Patient Id and Activity Code.<br>
     *
     * @param patientId    Patient Id
     * @param activityCode Activity Code
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentListByPatientIdAndActivityCode(String patientId, String activityCode) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        if (StringUtils.isNotBlank(activityCode)) {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        }
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        Pagination<Appointment> appointmentList = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        return getAppointmentDtos(appointmentList.getLstObject());
    }

    /**
     * Return Pagination Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param patientId      Patient Id
     * @param activityCode   Activity Code
     * @param countPerPage   Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo   Page Number To
     * @return Pagination Appointment DTO List
     */
    public Pagination<AppointmentDto> queryAppointmentListByPatientIdAndActivityCodeWithPaging(String patientId, String activityCode, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        if (StringUtils.isNotBlank(activityCode)) {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        }
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPaginationAppointmentDto(appointmentPagination);
    }

    /**
     * Return Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param deviceId  Device Id
     * @param startDate Start Date
     * @param endDate   End Date
     * @param lstStatus Status List
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentListByDeviceIdAndDateRangeAndStatus(String deviceId, String startDate, String endDate, List<String> lstStatus) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(deviceId)));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
        if (isBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            }
        } else {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        } //appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstStatus));
        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        return getAppointmentDtos(lstAppointment);
    }

    /**
     * Return Pagination Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param patientId      Patient Id
     * @param startDate      Start Date
     * @param endDate        End Date
     * @param lstStatus      Status List
     * @param countPerPage   Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo   Page Number To
     * @return Appointment DTO List
     */
    public Pagination<AppointmentDto> queryAppointmentListByPatientIdAndDateRangeAndPagination(String patientId, String startDate, String endDate, List<String> lstStatus, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        if (StringUtils.isNotEmpty(startDate)) {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
        }
        if (isBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            }
        } else {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        }
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstStatus));
        Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPaginationAppointmentDto(appointmentPagination);
    }

    /**
     * Return Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param patientId      Patient Id
     * @param activityCode   Activity Code
     * @param startDate      Start Date
     * @param endDate        End Date
     * @param countPerPage   Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo   Page Number To
     * @return Appointment DTO List
     */
    public Pagination<AppointmentDto> queryAppointmentListByPatientIdAndActivityCodeAndDateRangeAndPagination(String patientId, String activityCode, String startDate, String endDate, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
        if (isBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            }
        } else {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        }  //appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPaginationAppointmentDto(appointmentPagination);
    }

    /**
     * Return Appointment DTO List by Patient Id, DeviceId.<br>
     *
     * @param patientId Patient Id
     * @param deviceId  Device Id
     * @return Appointment List
     */
    public List<AppointmentDto> queryByPatientIdAndDeviceId(String patientId, String deviceId) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(deviceId)));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        // FIXME page 30, from 1 to Int_max should be removed.
        Pagination<Appointment> appointmentList = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
        return getAppointmentDtos(appointmentList.getLstObject());
    }

    /**
     * Return Appointment DTO List by Patient Id, Activity Code, DeviceId.<br>
     *
     * @param patientId
     * @param activityCode
     * @param deviceId
     * @param startDate
     * @param endDate
     * @param countPerPage
     * @param pageNumberFrom
     * @param pageNumberTo
     * @return
     */
    public Pagination<AppointmentDto> queryAppointmentListByPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPagination(String patientId, String activityCode, String deviceId, String startDate, String endDate, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>() {
            {
                setLstObject(new ArrayList());
            }
        };
        List<String> dateList = splitDateRange(startDate, StringUtils.isNotEmpty(endDate) ? endDate : startDate);
        List<String> noDataDateList = new ArrayList<>();
        List<AppointmentDto> list = new ArrayList<>();
        for (String date : dateList) {
            List<AppointmentDto> tmp = AppointmentCache.get(deviceId, date);
            if (tmp != null) {
                list.addAll(tmp);
            }else{
                if(!noDataDateList.contains(date)){
                    noDataDateList.add(date);
                }
            }
        }
        if (list != null && !list.isEmpty()) {
            Iterator<AppointmentDto> appointmentDtoIterator = list.iterator();
            while (appointmentDtoIterator.hasNext()) {
                AppointmentDto dto = appointmentDtoIterator.next();
                if (!dto.getStatus().equalsIgnoreCase(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))) {
                    appointmentDtoIterator.remove();
                    continue;
                }
                List<ParticipantDto> participantDtoList = dto.getParticipants();
                for (ParticipantDto p : participantDtoList) {
                    if (p.getType().equals(ParticipantTypeEnum.PATIENT)) {
                        if (!p.getParticipantId().equals(patientId)) {
                            appointmentDtoIterator.remove();
                            break;
                        }
                    }
                }
            }
        }
        pagination.getLstObject().addAll(list);
        pagination.setTotalCount(pagination.getLstObject().size());
        if (!noDataDateList.isEmpty()) {
            startDate = noDataDateList.get(0);
            endDate = noDataDateList.get(noDataDateList.size() - 1);
            if(startDate.equals(endDate)){
                endDate = null;
            }
            Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.PATIENT_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, patientId));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(deviceId)));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            if (isBlank(endDate)) {
                if (StringUtils.isNotBlank(startDate)) {
                    appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
                }
            } else {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
            }
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
            Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
            pagination = getPaginationAppointmentDto(appointmentPagination);
        }
        return pagination;
    }

    /**
     * Return Pagination Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param lstDeviceId    Device Id List
     * @param startDate      Start Date
     * @param endDate        End Date
     * @param lstStatus      Status List
     * @param countPerPage   Count Per Page
     * @param pageNumberFrom Page Number From
     * @param pageNumberTo   Page Number To
     * @return Appointment DTO List
     */
    public Pagination<AppointmentDto> queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(List<String> lstDeviceId, String startDate, String endDate, List<String> lstStatus, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>() {{
            setLstObject(new ArrayList<>());
        }};
        List<AppointmentDto> appointmentList = new ArrayList<>();

//        处理日期key
        List<String> dateList = splitDateRange(startDate, StringUtils.isNotEmpty(endDate) ? endDate : startDate);
        List<String> noDataDateList = new ArrayList<>();
        for (String deviceId : lstDeviceId) {
            for (String date : dateList) {
                List<AppointmentDto> tmpList = AppointmentCache.get(deviceId, date);
                if (tmpList != null) {
                    appointmentList.addAll(tmpList);
                } else {
                    if(!noDataDateList.contains(date)){
                        noDataDateList.add(date);
                    }
                }
            }
        }
        Iterator<AppointmentDto> appointmentDtoIterator = appointmentList.iterator();
        while (appointmentDtoIterator.hasNext()) {
            AppointmentDto dto = appointmentDtoIterator.next();
            if (lstStatus != null && !lstStatus.isEmpty()) {
                if (!lstStatus.contains(dto.getStatus().toLowerCase())) {
                    appointmentDtoIterator.remove();
                }
            }
        }
//      MORE Fetch
        if (!noDataDateList.isEmpty()) {
            startDate = noDataDateList.get(0);
            endDate = noDataDateList.get(noDataDateList.size() - 1);
            if(startDate.equals(endDate)){
                endDate = null;
            }
            Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstDeviceId));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            if (isBlank(endDate)) {
                if (StringUtils.isNotBlank(startDate)) {
                    appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
                }
            } else {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
            }
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstStatus));
            Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
            pagination = getPaginationAppointmentDto(appointmentPagination);
//                  将查询结果回写到Cache中
            if (pagination != null && pagination.getLstObject() != null) {
                for (AppointmentDto appointmentDto : pagination.getLstObject()) {
                    AppointmentCache.put(appointmentDto);
                }
            }

        }
        // Must have duplicated appointment
        pagination.getLstObject().addAll(appointmentList);
        
        List<AppointmentDto> original = pagination.getLstObject();
        
        // clear the duplicated appointment from cache and FHIR.
        List<AppointmentDto> cleared = removeDuplicatedAppointment(original);
        
        pagination.setLstObject(cleared);
        return pagination;
    }

    /**
     * 定时同步预约到cache中
     *
     * @param deviceIdList
     * @param startDate
     * @param endDate
     * @param lstStatus
     * @param countPerPage
     * @param pageNumberFrom
     * @param pageNumberTo
     * @return
     */
    public Pagination<AppointmentDto> syncAppointmentListByDeviceIdAndDateRangeAndPagination(List<String> deviceIdList, String startDate, String endDate, List<String> lstStatus, int countPerPage, int pageNumberFrom, int pageNumberTo) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceIdList));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
        if (isBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            }
        } else {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        }
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstStatus));
        Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
        return getPaginationAppointmentDto(appointmentPagination);
    }

    /**
     * Return Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param lstDeviceId  Device Id List
     * @param activityCode Activity Code
     * @param startDate    Start Date
     * @param endDate      End Date
     * @param lstRank      Rank List
     * @return Appointment DTO List
     */
    public List<AppointmentDto> queryAppointmentListByDeviceIdAndActivityNameAndDateRange(List<String> lstDeviceId, String activityCode, String startDate, String endDate, List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank) {
        Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, lstDeviceId));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
        if (isBlank(endDate)) {
            if (StringUtils.isNotBlank(startDate)) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            }
        } else {
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        }//appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
        appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
        if (null != lstRank) {
            for (ImmutablePair<AppointmentRankEnum, RankEnum> immutablePair : lstRank) {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), AppointmentRankEnum.getDisplay(immutablePair.getLeft())));
            }
        }

        List<Appointment> lstAppointment = fhirAppointmentInterface.queryAppointmentList(appointmentQueryImmutablePairMap);
        return getAppointmentDtos(lstAppointment);
    }

    /**
     * Return Pagination Appointment DTO List by Patient Id, Activity Code.<br>
     *
     * @param deviceIds    Device Id List
     * @param activityCode Activity Code
     * @param startDate    Start Date
     * @param endDate      End Date
     * @param lstRank      Rank List
     * @param countPerPage Count Per Page
     * @return Appointment DTO List
     */
    public Pagination<AppointmentDto> queryAppointmentListByDeviceIdAndActivityNameAndDateRangeWithPagination(List<String> deviceIds, String activityCode, String startDate, String endDate, List<ImmutablePair<AppointmentRankEnum, RankEnum>> lstRank, int countPerPage, int pageNumberFrom, int pageNumberTo) {

        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>() {{
            setLstObject(new ArrayList<>());
        }};
        List<AppointmentDto> appointmentList = new ArrayList<>();
        List<String> noDataDateList = new ArrayList<>();
//       处理日期key
        List<String> dateList = splitDateRange(startDate, isBlank(endDate) ? startDate : endDate);

        for (String deviceId : deviceIds) {
            for (String date : dateList) {
                List<AppointmentDto> tmpList = AppointmentCache.get(deviceId, date);
                if (tmpList != null) {
                    appointmentList.addAll(tmpList);
                } else {
                    if(!noDataDateList.contains(date)) {
                        noDataDateList.add(date);
                    }
                }
            }
        }
//           去除非Booked和非当前activityCode的预约
        Iterator<AppointmentDto> appointmentDtoIterator = appointmentList.iterator();
        while (appointmentDtoIterator.hasNext()) {
            AppointmentDto dto = appointmentDtoIterator.next();
            if (!AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED).equals(dto.getStatus().toLowerCase())
                    || !dto.getReason().equalsIgnoreCase(activityCode)) {
                appointmentDtoIterator.remove();
            }
        }
        if (!noDataDateList.isEmpty()) {
            startDate = noDataDateList.get(0);
            endDate = noDataDateList.get(noDataDateList.size() - 1);
            if(startDate.equals(endDate)){
                endDate = null;
            }
            Map<EnumAppointmentQuery, ImmutablePair<EnumMatchQuery, Object>> appointmentQueryImmutablePairMap = new LinkedHashMap<>();
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.ACTOR_ID, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, deviceIds));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_REASON, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, activityCode));
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_START, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
            if (isBlank(endDate)) {
                if (StringUtils.isNotBlank(startDate)) {
                    appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.APPOINTMENT_DATE, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, startDate));
                }
            } else {
                appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.DATERANGE_END, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, endDate));
            }
            appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.STATUS, new ImmutablePair<>(EnumMatchQuery.MATCHESEXACTLY, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))));
            if (null != lstRank) {
                for (ImmutablePair<AppointmentRankEnum, RankEnum> immutablePair : lstRank) {
                    appointmentQueryImmutablePairMap.put(EnumAppointmentQuery.SORTING, new ImmutablePair<>(EnumMatchQuery.fromCode(immutablePair.getRight().name()), AppointmentRankEnum.getDisplay(immutablePair.getLeft())));
                }
            }

            Pagination<Appointment> appointmentPagination = fhirAppointmentInterface.queryPagingAppointmentList(appointmentQueryImmutablePairMap, countPerPage, pageNumberFrom, pageNumberTo);
            pagination = getPaginationAppointmentDto(appointmentPagination);
//                  将查询结果回写到Cache中
            if (pagination != null && pagination.getLstObject() != null) {
                for (AppointmentDto appointmentDto : pagination.getLstObject()) {
                    AppointmentCache.put(appointmentDto);
                }
            }
        }
        pagination.getLstObject().addAll(appointmentList);
        
        // possible duplicated appointment
        List<AppointmentDto> original = pagination.getLstObject();
        // clear the duplicated appointment from cache and FHIR.
        List<AppointmentDto> cleared = removeDuplicatedAppointment(original);
        pagination.setLstObject(cleared);
        
//           排序
        if (lstRank != null && !lstRank.isEmpty()) {
            for (ImmutablePair<AppointmentRankEnum, RankEnum> pair : lstRank) {
                AppointmentRankEnum appointmentRankEnum = pair.getLeft();
                RankEnum rankEnum = pair.getRight();
                if (appointmentRankEnum.equals(AppointmentRankEnum.START_TIME)) {
                    if (rankEnum.equals(RankEnum.ASC)) {
                        Collections.sort(pagination.getLstObject(), Comparator.comparing(AppointmentDto::getStartTime));
                    } else {
                        Collections.sort(pagination.getLstObject(), (o1, o2) -> o2.getStartTime().compareTo(o1.getStartTime()));
                    }
                }
            }
        }
        return pagination;
    }

    private List<String> splitDateRange(String startDate, String endDate) {
//        将开始日期至结束日期分解成单个日期
        List<String> dateList = new ArrayList<>();
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(DateUtils.parseDate(startDate, DateFormatUtils.ISO_DATE_FORMAT.getPattern()));

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(DateUtils.parseDate(endDate, DateFormatUtils.ISO_DATE_FORMAT.getPattern()));
            for (; startCalendar.compareTo(endCalendar) <= 0; startCalendar.add(Calendar.DAY_OF_MONTH, 1)) {
                dateList.add(DateFormatUtils.format(startCalendar, DateFormatUtils.ISO_DATE_FORMAT.getPattern()));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return dateList;
    }

    protected List<AppointmentDto> removeDuplicatedAppointment(List<AppointmentDto> original) {
        List<String> idList = new ArrayList<>();
        List<AppointmentDto> cleared = new ArrayList<>();
        for (AppointmentDto vo: original) {
            String appId = vo.getAppointmentId();
            if (!idList.contains(appId)) {
                // 只加入不存在的AppointmentId。
                idList.add(appId);
                cleared.add(vo);
            }
        }
        return cleared;
    }

    private List<AppointmentDto> getAppointmentDtos(List<Appointment> lstAppointment) {
        List<AppointmentDto> lstAppointmentDto = new ArrayList<>();
        if (!lstAppointment.isEmpty()) {
            lstAppointment.forEach(appointment -> lstAppointmentDto.add(AppointmentAssembler.getAppointmentDto(appointment)));
        }
        return lstAppointmentDto;
    }

    private Pagination<AppointmentDto> getPaginationAppointmentDto(Pagination<Appointment> appointmentPagination) {
        Pagination<AppointmentDto> appointmentDtoPagination = new Pagination<>();
        if (appointmentPagination != null) {
            appointmentDtoPagination.setTotalCount(appointmentPagination.getTotalCount());
            appointmentDtoPagination.setLstObject(getAppointmentDtos(appointmentPagination.getLstObject()));
        }
        return appointmentDtoPagination;
    }
}