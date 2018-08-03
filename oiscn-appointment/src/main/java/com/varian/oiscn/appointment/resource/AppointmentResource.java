package com.varian.oiscn.appointment.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.appointment.calling.*;
import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueueListAssembler;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.dto.TreatmentAppointmentDTO;
import com.varian.oiscn.appointment.service.QueuingManagementServiceImpl;
import com.varian.oiscn.appointment.service.TreatmentAppointmentService;
import com.varian.oiscn.appointment.view.*;
import com.varian.oiscn.appointment.vo.*;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.appointment.calling.DeviceGuide;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.resource.BaseResponse;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeComparator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created by gbt1220 on 2/23/2017.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AppointmentResource extends AbstractResource {

    public static final String AUTO_CHECK_IN_PATIENT_NOT_FOUND = "PATIENT_NOT_FOUND";
    public static final String AUTO_CHECK_IN_NO_APPOINTMENT = "NO_APPOINTMENT";
    public static final String AUTO_CHECK_IN_NO_APPOINTMENT_FOR_TODAY = "NO_APPOINTMENT_FOR_TODAY";
    public static final String AUTO_CHECK_IN_SUCCESS = "SUCCESS";
    public static final String AUTO_CHECK_IN_INCORRECT_DATE = "INCORRECT_DATE";
    public static final String AUTO_CHECK_IN_INCORRECT_DEVICE = "INCORRECT_DEVICE";
    public static final String AUTO_CHECK_IN_INCORRECT_DATE_AND_DEVICE = "INCORRECT_DATE_AND_DEVICE";
    public static final String AUTO_CHECK_IN_ALREADY_CHECKED_IN = "ALREADY_CHECKED_IN";
    public static final String AUTO_CHECK_IN_INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final int AUTO_CHECK_IN_DEFAULT_INDEX = 0;

    private Lock manualCheckInLock = new ReentrantLock();

    private AppointmentAntiCorruptionServiceImp antiCorruptionServiceImp;

    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    private PatientCacheService patientCacheService;

    public AppointmentResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        this.antiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        this.patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        this.patientCacheService = new PatientCacheService();
    }

    @Path("/appointment")
    @POST
    public Response createAppointment(@Auth UserContext userContext, AppointmentDto dto) {
        String appointmentId = antiCorruptionServiceImp.createAppointment(dto);
        if (StringUtils.isNoneEmpty(appointmentId)) {
            return Response.status(Response.Status.CREATED).entity(appointmentId).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(StringUtils.EMPTY).build();
        }
    }

    @PUT
    @Path("/appointment/{id}")
    public synchronized Response updateAppointment(@Auth UserContext userContext,
                                      @PathParam("id") String id,
                                      AppointmentDto dto) {
        if (StringUtils.isEmpty(id)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(id).build();
        } else {
            AppointmentDto appointmentDto = antiCorruptionServiceImp.queryAppointmentById(id);
            AppointmentDataVO vo = new AppointmentDataVO();
            vo.setInstanceId(id);
            List<ParticipantDto> participantDtoList = appointmentDto.getParticipants();
            participantDtoList.forEach(participantDto -> {
                if(participantDto.getType().equals(ParticipantTypeEnum.PATIENT)){
                    vo.setPatientSer(participantDto.getParticipantId());
                }
                if(participantDto.getType().equals(ParticipantTypeEnum.DEVICE)){
                    vo.setDeviceId(participantDto.getParticipantId());
                }
            });
            vo.setActivityCode(appointmentDto.getReason());
            vo.setActivityType(ActivityTypeEnum.APPOINTMENT.name());

            vo.setAppointTimeList(new ArrayList<>());
            vo.getAppointTimeList().add(new AppointmentDataTimeSlotVO(){{
                setAppointmentId(appointmentDto.getAppointmentId());
                setStartTime(DateUtil.formatDate(dto.getStartTime(),DateUtil.DATE_TIME_FORMAT));
                setEndTime(DateUtil.formatDate(dto.getEndTime(),DateUtil.DATE_TIME_FORMAT));
                setAction(3);
            }});
            BaseResponse res = checkUpdateAppointmentTime(vo);
            if(res.getErrors().isEmpty()) {
                dto.setAppointmentId(id);
                antiCorruptionServiceImp.updateAppointment(dto);
            }
            return Response.status(Response.Status.OK).entity(res).build();
        }
    }

    @Path("/appointment/schedule/view/{date}")
    @GET
    public Response scheduleViews(@Auth UserContext userContext,@PathParam("date") String date){
        DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
        List<DeviceDto> deviceDtoList = DevicesReader.getAllDeviceDto();
        List<DeviceDto> deviceDtos = new ArrayList<>();
//      获取device 的 id
        List<String> deviceIdList = new ArrayList<>();
        deviceDtoList.forEach(deviceDto -> {
            DeviceDto dto = deviceAntiCorruptionServiceImp.queryDeviceByCode(deviceDto.getId());
            if(dto != null) {
                DeviceDto deviceDto1 = new DeviceDto(){{
                    setId(dto.getId());
                    setCode(dto.getCode());
                    setName(dto.getName());
                    setCapacity(deviceDto.getCapacity());
                }};
                deviceIdList.add(dto.getId());
                deviceDtos.add(deviceDto1);
            }
        });
        Date startDate;
        try {
            startDate = DateUtil.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage());
            startDate = new Date();
        }

        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        if(appointmentStoredToLocal){
            CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
//            获取治疗节点的设备
            String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
            ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getSourceActivityCodeByRelativeCode(treatmentActivityCode);
//          存储所有CarePathTemplate中配置的治疗设备ID
            Set<String> treatmentDeviceIdSet = new HashSet<>();
            getTreatmentDeviceIdListFromAllCarePathTemplate(carePathAntiCorruptionServiceImp, activityCodeConfig, treatmentDeviceIdSet);

            TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
            Pagination<TreatmentAppointmentDTO> pagination;
                Date endDate = startDate;
                List<String> treatmentDeviceIdList = new ArrayList<>();
                treatmentDeviceIdList.addAll(treatmentDeviceIdSet);
                pagination = treatmentAppointmentService.queryByDeviceIdListAndDatePagination(treatmentDeviceIdList,startDate,endDate,Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED),"asc",Integer.MAX_VALUE+"",1+"");
                if(pagination != null && pagination.getLstObject() != null){
                    pagination.getLstObject().forEach(treatmentAppointmentDTO -> {
                        appointmentDtoList.add(treatmentAppointmentService.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO));
                    });
                }
//              除了治疗节点的deviceId
            List<String> noTreatmentDeviceIdList = new ArrayList<>();
            deviceIdList.forEach(deviceId ->{
                if(!treatmentDeviceIdSet.contains(deviceId)){
                    noTreatmentDeviceIdList.add(deviceId);
                }
            });
            queryAllAppointmentByPagination(date, noTreatmentDeviceIdList, appointmentDtoList);
        }else{
            queryAllAppointmentByPagination(date, deviceIdList, appointmentDtoList);
        }
//      将预约按照设备ID分开
        Map<String,List<AppointmentDto>> deviceIdAppointmentListMap = new HashMap<>();
        appointmentDtoList.forEach(appointmentDto -> {
            List<ParticipantDto> list = appointmentDto.getParticipants();
            for(ParticipantDto participantDto :list){
                if(participantDto.getType().equals(ParticipantTypeEnum.DEVICE)){
                    String deviceId = participantDto.getParticipantId();
                    if(deviceIdAppointmentListMap.containsKey(deviceId)){
                        deviceIdAppointmentListMap.get(deviceId).add(appointmentDto);
                    }else{
                        deviceIdAppointmentListMap.put(deviceId,new ArrayList<AppointmentDto>(){{
                            add(appointmentDto);
                        }});
                    }
                    break;
                }
            }
        });
        List<DeviceScheduleViewVO> deviceScheduleViewVOList= new ArrayList<>();
        statisticsForenoonAndAfternoonNum(deviceDtos, deviceScheduleViewVOList, startDate, deviceIdAppointmentListMap);
        addNotAppointDevice2List(deviceDtos, deviceScheduleViewVOList);
//        按照配置文件中的设备顺序排序结果
        List<DeviceScheduleViewVO> deviceScheduleViewVOSortList= new ArrayList<>();
        deviceIdList.forEach(deviceId->{
            deviceScheduleViewVOList.forEach(deviceScheduleViewVO -> {
                if(deviceScheduleViewVO.getDeviceId().equals(deviceId)){
                    deviceScheduleViewVOSortList.add(deviceScheduleViewVO);
                }
            });
        });
        return Response.ok(deviceScheduleViewVOSortList).build();
    }

    /**
     * 将没有预约数据的设备添加到返回结果的list中
     * @param deviceDtos 所有可预约的设备
     * @param deviceScheduleViewVOList 返回结果list
     */
    private void addNotAppointDevice2List(List<DeviceDto> deviceDtos, List<DeviceScheduleViewVO> deviceScheduleViewVOList) {
        deviceDtos.forEach(deviceDto -> {
             boolean[] has = {false};
            deviceScheduleViewVOList.forEach(deviceScheduleViewVO -> {
                if(deviceScheduleViewVO.getDeviceId().equals(deviceDto.getId())){
                    has[0] = true;
                }
            });
            if(!has[0]){
                deviceScheduleViewVOList.add(new DeviceScheduleViewVO(){{
                    setDeviceId(deviceDto.getId());
                    setCode(deviceDto.getCode());
                    setName(deviceDto.getName());
                    setCapacity(deviceDto.getCapacity());
                }});
            }
        });
    }

    private void getTreatmentDeviceIdListFromAllCarePathTemplate(CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp, ActivityCodeConfig activityCodeConfig, Set<String> treatmentDeviceIdSet) {
        CarePathConfig carePathConfig = configuration.getCarePathConfig();
        List<CarePathConfigItem> carePathConfigItemList = carePathConfig.getCarePath();
        carePathConfigItemList.forEach(carePathConfigItem -> {
//                根据carePathTemplateName获取模板
            CarePathTemplate carePathTemplate = carePathAntiCorruptionServiceImp.queryCarePathByTemplateName(carePathConfigItem.getTemplateId());
            if(carePathTemplate != null) {
//                根据activityCode获取设备Id
                carePathTemplate.getActivities().forEach(plannedActivity -> {
                    if (plannedActivity.getActivityCode().equals(activityCodeConfig.getName())) {
                        if(plannedActivity.getDeviceIDs() != null) {
                            treatmentDeviceIdSet.addAll(plannedActivity.getDeviceIDs());
                        }
                    }
                });
            }
        });
    }

    /**
     * 统计每个设备上下午的预约情况
     * @param deviceDtos
     * @param deviceScheduleViewVOList
     * @param startDate
     * @param deviceIdAppointmentListMap
     */
    private void statisticsForenoonAndAfternoonNum(List<DeviceDto> deviceDtos, List<DeviceScheduleViewVO> deviceScheduleViewVOList, Date startDate, Map<String, List<AppointmentDto>> deviceIdAppointmentListMap) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY,12);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        Date splitDate = calendar.getTime();
        deviceDtos.forEach(deviceDto -> {
            DeviceScheduleViewVO vo = new DeviceScheduleViewVO();
            vo.setDeviceId(deviceDto.getId());
            vo.setName(deviceDto.getName());
            vo.setCapacity(deviceDto.getCapacity());
            vo.setCode(deviceDto.getCode());
            List<AppointmentDto> list = deviceIdAppointmentListMap.get(deviceDto.getId());
            if(list != null){
                vo.setOccupied(list.size());
//                根据预约开始时间，统计上午下午数量,上午下午的分界线是12:00
                int forenoonAppointTotal = (int)list.stream().filter(appointmentDto -> {
                    Date sdate = appointmentDto.getStartTime();
                    return sdate.before(splitDate);
                }).count();
                vo.setForenoonOccupied(forenoonAppointTotal);
                vo.setAfternoonOccupied(vo.getOccupied() - forenoonAppointTotal);
                deviceScheduleViewVOList.add(vo);
            }
        });
    }

    private void queryAllAppointmentByPagination( String date, List<String> deviceIdList, List<AppointmentDto> appointmentDtoList) {
        int countPerPage = Integer.MAX_VALUE;
        Pagination<AppointmentDto> pagination;
        pagination = antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(deviceIdList, date, date, Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED),
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), countPerPage, 1, Integer.MAX_VALUE);
        appointmentDtoList.addAll(pagination.getLstObject());
    }

    @Path("/appointment/status")
    @POST
    public Response searchAppointmentStatusById(@Auth UserContext userContext, List<KeyValuePair> keyValuePairs) {
        List<KeyValuePair> result = new ArrayList<>();
        if (keyValuePairs == null || keyValuePairs.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(keyValuePairs).build();
        } else {
            List<String> appointmentIdList = new ArrayList<>();
            keyValuePairs.forEach(keyValuePair -> appointmentIdList.add(keyValuePair.getKey()));
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (appointmentStoredToLocal) {
                TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
                for (String appointmentId : appointmentIdList) {
                    TreatmentAppointmentDTO treatmentAppointmentDTO = treatmentAppointmentService.queryByUidOrAppointmentId(appointmentId);
                    if (treatmentAppointmentDTO != null) {
                        if (StringUtils.isNotEmpty(treatmentAppointmentDTO.getAppointmentId())) {
                            AppointmentDto appointmentDto = antiCorruptionServiceImp.queryAppointmentById(treatmentAppointmentDTO.getAppointmentId());
                            result.add(new KeyValuePair(appointmentId, appointmentDto.getStatus()));
                        }else{
                            String sts = treatmentAppointmentDTO.getStatus();
                            sts = sts.substring(0,1).toUpperCase()+ sts.substring(1);
                            result.add(new KeyValuePair(appointmentId, sts));
                        }
                    } else if (StringUtils.isNumeric(appointmentId)) {
                        AppointmentDto appointmentDto = antiCorruptionServiceImp.queryAppointmentById(appointmentId);
                        if (appointmentDto != null) {
                            result.add(new KeyValuePair(appointmentId, appointmentDto.getStatus()));
                        }
                    }
                }
            } else {
                for (String appointmentId : appointmentIdList) {
                    AppointmentDto appointmentDto = antiCorruptionServiceImp.queryAppointmentById(appointmentId);
                    if (appointmentDto != null) {
                        result.add(new KeyValuePair(appointmentId, appointmentDto.getStatus()));
                    }
                }
            }
        }
        return Response.ok(result).build();
    }

    @Path("/appointments/search")
    @GET
    public Response searchAppointments(@Auth UserContext userContext,
                                       @QueryParam("deviceId") String deviceId,
                                       @QueryParam("startDate") String startDate,
                                       @QueryParam("endDate") String endDate,
                                       @QueryParam("orderId") String orderId,
                                       @QueryParam("patientSer") Long patientSer) {
        if (log.isDebugEnabled()) {
            log.debug("Input Parameter: deviceId:[{}], startDate:[{}], endDate:[{}], orderId:[{}], patientSer:[{}]",
                    deviceId, startDate, endDate, orderId, patientSer);
        }
        int countPerPage = Integer.MAX_VALUE;
        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>(){{
            setLstObject(new ArrayList());
        }};
        List<AppointmentListVO> result = new ArrayList<>();
//        List<AppointmentDto> appointmentDtoList = searchAppointmentFromLocal(deviceId, startDate, endDate, orderId, hisId);
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
        treatmentAppointmentService.searchAppointmentFromLocal(deviceId, startDate, endDate, orderId, patientSer, appointmentDtoList);
        if (StringUtils.isNotEmpty(deviceId)) {
            if (log.isDebugEnabled()) {
                log.debug("deviceId: [{}]", deviceId);
            }
            String start;
            String end;
            if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
                start = startDate;
                end = endDate;
            } else {
                start = DateUtil.getCurrentDate();
                end = DateUtil.getCurrentDate();
            }

            try {
                Date tmpStartDate = DateUtil.parse(start);
                Date tmpEndDate = DateUtil.parse(end);
                for(;tmpStartDate.compareTo(tmpEndDate)<=0;tmpStartDate=DateUtil.addDay(tmpStartDate,1)){
                    Pagination<AppointmentDto> pag = antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndDateRangeAndStatusWithPagination(Arrays.asList(deviceId),
                            DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT), DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT), Arrays.asList(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED),
                                    AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)), countPerPage, 1, Integer.MAX_VALUE);
                    if(pag!=null && pag.getLstObject()!=null){
                        pagination.getLstObject().addAll(pag.getLstObject());
                    }
                }
            } catch (ParseException e) {
                log.error("Parse dateStr [{},{}] to Date Exception",start,end);
            }
            if (pagination != null && pagination.getLstObject().size() > 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Paging TotalCount: [{}]", pagination.getTotalCount());
                    int debugIndex = 0;
                    for (AppointmentDto object : pagination.getLstObject()) {
                        log.debug("AppointmentDto[{}]: {}", debugIndex++, object.toString());
                    }
                }
                appointmentDtoList.addAll(pagination.getLstObject());
            }
        } else if (StringUtils.isNotEmpty(orderId)) {
            if (log.isDebugEnabled()) {
                log.debug("orderId: [{}]", orderId);
            }
            appointmentDtoList = antiCorruptionServiceImp.queryAppointmentByOrderId(orderId);
            if (log.isDebugEnabled()) {
                log.debug("appointmentDtoList: [{}]", appointmentDtoList);
            }
        } else if (patientSer != null) {
            if (log.isDebugEnabled()) {
                log.debug("patientSer: [{}]", patientSer);
            }
            String start;
            String end;
            if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
                start = startDate;
                end = endDate;
            } else {
                start = DateUtil.getCurrentDate();
                end = DateUtil.getCurrentDate();
            }
            Date tmpStartDate = null;
            Date tmpEndDate = null;
            try {
                tmpStartDate = DateUtil.parse(start);
                tmpEndDate = DateUtil.parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for(;tmpStartDate.compareTo(tmpEndDate)<=0;tmpStartDate=DateUtil.addDay(tmpStartDate,1)) {
                Pagination<AppointmentDto> pag = antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientSer), DateUtil.formatDate(tmpStartDate,DateUtil.DATE_FORMAT), null, Arrays.asList(
                        AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)),
                        countPerPage, 1, Integer.MAX_VALUE);
                if(pag != null && pag.getLstObject() != null){
                    pagination.getLstObject().addAll(pag.getLstObject());
                }
            }
            if (pagination != null && pagination.getLstObject().size() > 0) {
                if (log.isDebugEnabled()) {
                    log.debug("Paging TotalCount: [{}]", pagination.getTotalCount());
                    int debugIndex = 0;
                    for (AppointmentDto object : pagination.getLstObject()) {
                        log.debug("AppointmentDto[{}]: {}", debugIndex++, object.toString());
                    }
                }
                appointmentDtoList.addAll(pagination.getLstObject());
            }
        }

        assembleResult(appointmentDtoList, result);
        return Response.status(Response.Status.OK).entity(result).build();
    }

//    private List<AppointmentDto> searchAppointmentFromLocal(String deviceId, String startDate, String endDate, String orderId, String hisId) {
//        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
//        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
//        if (!appointmentStoredToLocal) {
//            return new ArrayList<>();
//        }
//
//        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
//        Pagination<TreatmentAppointmentDTO> pagination = null;
//
//        try {
//            if (StringUtils.isNotEmpty(deviceId)) {
//                String start;
//                String end;
//                if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
//                    start = startDate;
//                    end = endDate;
//                } else {
//                    start = DateUtil.getCurrentDate();
//                    end = DateUtil.getCurrentDate();
//                }
//
//                pagination = treatmentAppointmentService.queryByDeviceIdListAndDatePagination(Arrays.asList(deviceId), DateUtil.parse(start), DateUtil.parse(end), Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED),"asc", Integer.MAX_VALUE + "", "1");
//                if (log.isDebugEnabled()) {
//                    log.debug("searchAppointmentFromLocal- Paging TotalCount: [{}]", pagination.getTotalCount());
//                    int debugIndex = 0;
//                    for (TreatmentAppointmentDTO dto : pagination.getLstObject()) {
//                        log.debug("searchAppointmentFromLocal - TreatmentAppointmentDTO[{}]: {}", debugIndex++, dto.toString());
//                    }
//                }
//
//            } else if (StringUtils.isNotEmpty(orderId)) {
//
//            } else if (StringUtils.isNotEmpty(hisId)) {
//                pagination = treatmentAppointmentService.queryByPatientSerListAndDatePagination(Arrays.asList(hisId), DateUtil.parse(startDate), DateUtil.parse(endDate), "asc", Integer.MAX_VALUE + "", "1");
//            }
//        } catch (Exception e) {
//
//        }
//        if (pagination != null) {
//            if (pagination.getLstObject() != null) {
//                pagination.getLstObject().forEach(treatmentAppointmentDTO -> {
//                    if (StringUtils.isEmpty(treatmentAppointmentDTO.getAppointmentId())) {
//                        appointmentDtoList.add(treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO));
//                    }
//                });
//            }
//        }
//        return appointmentDtoList;
//    }
//
//    private AppointmentDto treatmentAppointmentDTO2AppointmentDto(TreatmentAppointmentDTO appointmentDTO) {
//        AppointmentDto dto = new AppointmentDto();
//        dto.setStartTime(appointmentDTO.getStartTime());
//        dto.setEndTime(appointmentDTO.getEndTime());
//        if (StringUtils.isEmpty(appointmentDTO.getAppointmentId())) {
//            dto.setAppointmentId(appointmentDTO.getUid());
//        } else {
//            dto.setAppointmentId(appointmentDTO.getAppointmentId());
//        }
//        dto.setStatus(appointmentDTO.getStatus());
//        dto.setReason(appointmentDTO.getActivityCode());
//        dto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, appointmentDTO.getPatientId()),
//                new ParticipantDto(ParticipantTypeEnum.DEVICE, appointmentDTO.getDeviceId())));
//        Date nowDate = new Date();
//        dto.setCreatedDT(nowDate);
//        dto.setLastModifiedDT(nowDate);
//        return dto;
//    }
//    private List<AppointmentDto> searchAppointmentFromLocal(String deviceId, String startDate, String endDate, String orderId, String hisId, List<AppointmentDto> appointmentDtoList) {
//        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
//        if (!appointmentStoredToLocal) {
//            return new ArrayList<>();
//        }
//        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
//        Pagination<TreatmentAppointmentDTO> pagination = null;
//
//        try {
//            if (StringUtils.isNotEmpty(deviceId)) {
//                String start;
//                String end;
//                if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
//                    start = startDate;
//                    end = endDate;
//                } else {
//                    start = DateUtil.getCurrentDate();
//                    end = DateUtil.getCurrentDate();
//                }
//
//                pagination = treatmentAppointmentService.queryByDeviceIdListAndDatePagination(Arrays.asList(deviceId), DateUtil.parse(start), DateUtil.parse(end), Arrays.asList(AppointmentStatusEnum.BOOKED,AppointmentStatusEnum.FULFILLED),"asc", Integer.MAX_VALUE + "", "1");
//                if (log.isDebugEnabled()) {
//                    log.debug("searchAppointmentFromLocal- Paging TotalCount: [{}]", pagination.getTotalCount());
//                    int debugIndex = 0;
//                    for (TreatmentAppointmentDTO dto : pagination.getLstObject()) {
//                        log.debug("searchAppointmentFromLocal - TreatmentAppointmentDTO[{}]: {}", debugIndex++, dto.toString());
//                    }
//                }
//
//            } else if (StringUtils.isNotEmpty(orderId)) {
//
//            } else if (StringUtils.isNotEmpty(hisId)) {
//                pagination = treatmentAppointmentService.queryByPatientSerListAndDatePagination(Arrays.asList(hisId), DateUtil.parse(startDate), DateUtil.parse(endDate), "asc", Integer.MAX_VALUE + "", "1");
//            }
//        } catch (Exception e) {
//
//        }
//        if (pagination != null) {
//            if (pagination.getLstObject() != null) {
//                pagination.getLstObject().forEach(treatmentAppointmentDTO -> {
//                    if (StringUtils.isEmpty(treatmentAppointmentDTO.getAppointmentId())) {
//                        appointmentDtoList.add(treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO));
//                    }
//                });
//            }
//        }
//        return appointmentDtoList;
//    }
//
//    private AppointmentDto treatmentAppointmentDTO2AppointmentDto(TreatmentAppointmentDTO appointmentDTO) {
//        AppointmentDto dto = new AppointmentDto();
//        dto.setStartTime(appointmentDTO.getStartTime());
//        dto.setEndTime(appointmentDTO.getEndTime());
//        if (StringUtils.isEmpty(appointmentDTO.getAppointmentId())) {
//            dto.setAppointmentId(appointmentDTO.getUid());
//        } else {
//            dto.setAppointmentId(appointmentDTO.getAppointmentId());
//        }
//        dto.setStatus(appointmentDTO.getStatus());
//        dto.setReason(appointmentDTO.getActivityCode());
//        dto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, appointmentDTO.getPatientId()),
//                new ParticipantDto(ParticipantTypeEnum.DEVICE, appointmentDTO.getDeviceId())));
//        Date nowDate = new Date();
//        dto.setCreatedDT(nowDate);
//        dto.setLastModifiedDT(nowDate);
//        return dto;
//    }

    @Path("/appointments/searchByPatientId")
    @GET
    public Response searchAppointmentsByPatientId(@Auth UserContext userContext,
                                                  @QueryParam("patientId") Long patientId) {
        List<AppointmentVO> result = new ArrayList<>();
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        AppointmentVO appointmentVO;
        if (patientId != null) {
            appointmentDtoList = antiCorruptionServiceImp.queryAppointmentListByPatientId(String.valueOf(patientId));
        }
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            appointmentVO = new AppointmentVO(appointmentDto.getReason(),
                    DateUtil.formatDate(appointmentDto.getStartTime(), DateUtil.SHORT_DATE_TIME_FORMAT) +
                            "-" + DateUtil.formatDate(appointmentDto.getEndTime(), DateUtil.HOUR_MINUTE_TIME_FORMAT));
            result.add(appointmentVO);
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @GET
    @Path("/appointment/searchByPatient")
    public Response searchByPatient(@Auth UserContext userContext,
                                    @QueryParam("patientSer") Long patientSer,
                                    @QueryParam("deviceId") String deviceId) {
        if (patientSer == null || Strings.isNullOrEmpty(deviceId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (patientSer == null) {
            // No patient with this HisId.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // read local DoTreatment appointment
        List<TreatmentAppointmentDTO> localDtoList = queryPatientLocalTreatmentAppointment(userContext, new Long(patientSer), deviceId);

        List<AppointmentDto> ariaDtoList = antiCorruptionServiceImp.queryByPatientIdAndDeviceId(String.valueOf(patientSer), deviceId);

        // response vo list
        List<PatientAppointmentVO> voList = new ArrayList<>();
        ariaDtoList.stream().forEach(ariaDto -> {
            PatientAppointmentVO vo = new PatientAppointmentVO();
            vo.setAppointmentId(ariaDto.getAppointmentId());
            vo.setStartTime(ariaDto.getStartTime());
            vo.setEndTime(ariaDto.getEndTime());
            vo.setStatus(ariaDto.getStatus());
            voList.add(vo);
            Iterator<TreatmentAppointmentDTO> iterator = localDtoList.iterator();
            TreatmentAppointmentDTO localDto;
            while (iterator.hasNext()) {
                localDto = iterator.next();
                if (vo.getStartTime().equals(localDto.getStartTime())
                        && vo.getEndTime().equals(localDto.getEndTime())) {
                    iterator.remove();
                    // break; // if no appointment in same time.
                }
            }
        });

        // compound local appointment and Aria appointment
        localDtoList.stream().forEach(localDto -> {
            PatientAppointmentVO vo = new PatientAppointmentVO();
            String appointmentId = localDto.getAppointmentId();
            if (StringUtils.isBlank(appointmentId)) {
                appointmentId = localDto.getUid();
            }
            vo.setAppointmentId(appointmentId);
            vo.setStartTime(localDto.getStartTime());
            vo.setEndTime(localDto.getEndTime());
            vo.setStatus(localDto.getStatus());
            voList.add(vo);
        });

        return Response.status(Response.Status.OK).entity(voList).build();
    }

    @GET
    @Path("/appointment/searchByPatientInCurrentEncounter")
    public Response searchByPatientInCurrentEncounter(@Auth UserContext userContext,
                                    @QueryParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            // No patient with this patientSer.
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        //获取当前Encounter中所有的carepath
        int currentEncounterId = -1;
        // bad progress
        PatientEncounterCarePath ecp = PatientEncounterHelper.getEncounterCarePathByPatientSer(String.valueOf(patientSer));
        if (ecp == null || ecp.getPlannedCarePath() == null) {
            log.error("Patient information Broken! No Encounter, or PatientEncounter, or PatientEncounterCarePath!!! {}", patientSer);
            return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();
        }
        EncounterCarePathList encounterCarePathList = ecp.getPlannedCarePath();
        currentEncounterId = encounterCarePathList.getEncounterId().intValue();
        // read local DoTreatment appointment
        TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(userContext);
        List<TreatmentAppointmentDTO> localDtoList = treatmentAppointmentService.queryAppointmentListByPatientSerAndEncounterId(patientSer, currentEncounterId);
        List<AppointmentDto> localAppointmentDtoList = new ArrayList<>();
        for(TreatmentAppointmentDTO treatmentAppointmentDTO : localDtoList){
           localAppointmentDtoList.add(treatmentAppointmentService.treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO));
        }

        Pagination<AppointmentDto> pagination = antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(String.valueOf(patientSer), null, null, Arrays.asList(
                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED), AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.FULFILLED)),
                Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        List<AppointmentDto> ariaDtoList = pagination.getLstObject();

        //对结束治疗的患者，对预约作出筛选，只保留当前encounter内的所有预约。
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(String.valueOf(patientSer));
        Set<String> currentEncounterInstanceIdSet = new HashSet<>();
        Set<Long> currentCarePathInstanceIdSet = new HashSet<>();
        encounterCarePathList.getEncounterCarePathList().forEach(encounterCarePath -> currentCarePathInstanceIdSet.add(encounterCarePath.getCpInstanceId()));
        Long currentMasterCarePathInstanceId = encounterCarePathList.getMasterCarePathInstanceId();
        List<Long> currentOptionalCarePathInstanceIdList = encounterCarePathList.getOptionalCarePathInstanceId();
        boolean beginTreatment = false;
        String deviceId = "";
        String treatmentCode = SystemConfigPool.queryTreatmentActivityCode();
        List<ActivityInstance> activityInstanceList = new ArrayList<>();
        for(Iterator<CarePathInstance> iterator = carePathInstanceList.iterator(); iterator.hasNext(); ){
            CarePathInstance carePathInstance = iterator.next();
            if(!currentCarePathInstanceIdSet.contains(Long.valueOf(carePathInstance.getId()))){
                iterator.remove();
            } else {
                carePathInstance.getOriginalActivityInstances().forEach(activityInstance -> currentEncounterInstanceIdSet.add(activityInstance.getInstanceID()));
//            首先判断是否进入了治疗流程
                if(!beginTreatment) {
                    Long tempCarePathInstanceId = Long.parseLong(carePathInstance.getId());
                    if(tempCarePathInstanceId.equals(currentMasterCarePathInstanceId) || currentOptionalCarePathInstanceIdList.contains(tempCarePathInstanceId)){
                        Optional<ActivityInstance> activityInstanceOptional = carePathInstance.getOriginalActivityInstances().stream().filter(activityInstance ->
                                activityInstance.getActivityCode().equals(treatmentCode)
                                        && (StringUtils.isNotEmpty(activityInstance.getInstanceID()))).findFirst();
                        if (activityInstanceOptional.isPresent()) {
                            beginTreatment = true;
                            deviceId = activityInstanceOptional.get().getDeviceIDs().get(0);
                        }
                    }
                }
                //获取已经Schedule的治疗预约
                List<ActivityInstance> tmpList = carePathInstance.getOriginalActivityInstances().stream().filter(activityInstance ->
                        activityInstance.getActivityCode().equals(treatmentCode)
                                && CarePathStatusEnum.ACTIVE.equals(activityInstance.getStatus())).collect(Collectors.toList());
                if(tmpList != null && !tmpList.isEmpty()){
                    activityInstanceList.addAll(tmpList);
                }
            }
        }
        for(Iterator<AppointmentDto> iterator = ariaDtoList.iterator(); iterator.hasNext();){
            AppointmentDto appointmentDto = iterator.next();
            if(StringUtils.isEmpty(appointmentDto.getAppointmentId())){
                continue;
            } else {
                if(!currentEncounterInstanceIdSet.contains(appointmentDto.getAppointmentId())){
                    iterator.remove();
                }
            }
        }

        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        appointmentDtoList.addAll(ariaDtoList);
        appointmentDtoList.addAll(localAppointmentDtoList);
        Collections.sort(appointmentDtoList);
        Set<String> appointmentIdSet = new HashSet<>();
//      去重
        for(Iterator<AppointmentDto> iterator = appointmentDtoList.iterator(); iterator.hasNext();){
            AppointmentDto appointmentDto = iterator.next();
            if(!appointmentIdSet.contains(appointmentDto.getAppointmentId())){
                appointmentIdSet.add(appointmentDto.getAppointmentId());
            } else {
                iterator.remove();
            }
        }

        List<AppointmentListVO> result = new ArrayList<>();
        assembleResult(appointmentDtoList, result);

        Map<String, Object> resultHashMap = new HashMap<>();
        Map<String, Object> treatmentSchedulable = new HashMap<>();

        if(beginTreatment && activityInstanceList.isEmpty()){
            treatmentSchedulable.put("deviceId", deviceId);
            treatmentSchedulable.put("patientSer", patientSer);
            treatmentSchedulable.put("reason", treatmentCode);
            ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getActivityCode(treatmentCode);
            if(activityCodeConfig != null) {
                treatmentSchedulable.put("reasonContent", activityCodeConfig.getContent());
            }
        }

        resultHashMap.put("treatmentSchedulable", treatmentSchedulable);
        resultHashMap.put("appointmentList", result);
        return Response.status(Response.Status.OK).entity(resultHashMap).build();
    }

//    @Path("/appointments/checktime")
//    @POST
//    public Response checkAppointmentTime(@Auth UserContext userContext, AppointmentDataVO appointmentDataVO) {
//        final BaseResponse res = new BaseResponse();
//        String patientSer = appointmentDataVO.getPatientSer();
//        if (StringUtils.isEmpty(patientSer) || StringUtils.isEmpty(appointmentDataVO.getInstanceId())) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("patientSer should not be empty").build();
//        }
//        CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(patientSer,appointmentDataVO.getInstanceId(), ActivityTypeEnum.TASK);
//        List<ActivityInstance> activityInstanceList = carePathInstance.getOriginalActivityInstances();
////      get appointment from activityInstanceList
//        List<ActivityInstance> appointmentAi = new ArrayList<>();
//        activityInstanceList.forEach(activityInstance -> {
//            if(activityInstance.getActivityType().equals(ActivityTypeEnum.APPOINTMENT)){
//                appointmentAi.add(activityInstance);
//            }
//        });
//
//        Pagination<AppointmentDto> pagination;
//        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
//
//        int countPerPage = Integer.MAX_VALUE;
//
//        pagination = antiCorruptionServiceImp.queryAppointmentListByPatientIdAndDateRangeAndPagination(patientSer, null, null, Arrays.asList(
//                AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED)),
//                countPerPage, 1, Integer.MAX_VALUE);
//        if (pagination != null && pagination.getLstObject().size() > 0) {
//            pagination.getLstObject().forEach(appointmentDto -> {
//                appointmentAi.forEach(activityInstance -> {
//                    if(appointmentDto.getAppointmentId().equals(activityInstance.getInstanceID())){
//                        appointmentDtoList.add(appointmentDto);
//                    }
//                });
//            });
//        }
//        Iterator<AppointmentDataTimeSlotVO> it = appointmentDataVO.getAppointTimeList().iterator();
//        while(it.hasNext()){
//            if(StringUtils.isNotEmpty(it.next().getAppointmentId())){
//                it.remove();
//            }
//        }
//        List<AppointmentDataTimeSlotVO> appointmentDataTimeSlotVOList = appointmentDataVO.getAppointTimeList();
//        Collections.sort(appointmentDataTimeSlotVOList);
//        // 获取预约时间最近的一个预约
//        AppointmentDataTimeSlotVO firstTimeSlotVO = appointmentDataTimeSlotVOList.get(0);
//        // 如果firstTimeSlotVO 的开始时间小于等于latestAppointmentDto的结束时间，则返回预约时间错误的信息
//        Date starTime;
//        String startTimeStr = firstTimeSlotVO.getStartTime();
//        try {
//            starTime = DateUtil.parse(startTimeStr);
//        } catch (ParseException e) {
//            log.error("Date ParseException: {}, Item: {}", e.getMessage(), startTimeStr);
//            res.addError("error-099", startTimeStr);
//            return Response.ok(res).build();
//        }
//
//        if (!appointmentDtoList.isEmpty()) {
//            appointmentDtoList.sort(Comparator.comparing(AppointmentDto::getEndTime));
//            AppointmentDto latestAppointmentDto = appointmentDtoList.get(appointmentDtoList.size() - 1);
//
//            if (starTime.compareTo(latestAppointmentDto.getEndTime()) <= 0) {
//                firstTimeSlotVO.setActName(ActivityCodesReader.getActivityCode(appointmentDataVO.getActivityCode()).getEntryContent());
//                firstTimeSlotVO.setConflictActName(ActivityCodesReader.getSourceActivityCodeByRelativeCode(latestAppointmentDto.getReason()).getEntryContent());
//                // 预约时间早于最近一个预约
//                res.addError("error-030", firstTimeSlotVO);
//            }
//        }
//        if (starTime.before(new Date())) {
//            // 预约时间早于当前时间，不可预约
//            res.addError("error-031", firstTimeSlotVO);
//        }
//        return Response.ok(res).build();
//    }

//    @Path("/appointments/checkUpdateTime")
//    @POST
//    public Response checkUpdateTime(@Auth UserContext userContext, AppointmentDataVO appointmentDataVO) {
//        BaseResponse res = null;
//        String patientSer = appointmentDataVO.getPatientSer();
//        String actCode = appointmentDataVO.getActivityCode();
//        String deviceId = appointmentDataVO.getDeviceId();
//        if (StringUtils.isEmpty(patientSer) || StringUtils.isBlank(actCode) || StringUtils.isBlank(deviceId)) {
//            log.debug("BAD_REQUEST {}", appointmentDataVO);
//            return Response.status(Response.Status.BAD_REQUEST).entity(appointmentDataVO).build();
//        }
//        res = checkUpdateAppointmentTime(appointmentDataVO);
//        return Response.ok(res).build();
//    }

    private BaseResponse checkUpdateAppointmentTime(AppointmentDataVO appointmentDataVO){
        BaseResponse res = new BaseResponse();
        List<AppointmentDataTimeSlotVO> timeList = appointmentDataVO.getAppointTimeList();
        Date now = new Date();
        Date startTime = null;
        Date endTime = null;
        Date firstStartTime = null;
        Date lastEndTime = null;

        for (AppointmentDataTimeSlotVO vo: timeList) {
            if (vo.getAction() == 1) {
                // remove action, no need handle
                continue;
            }
            try {
                startTime = DateUtil.parse(vo.getStartTime());
                endTime = DateUtil.parse(vo.getEndTime());
            } catch (ParseException e) {
                log.debug("ParseException {}", e.getMessage());
            }
            if (startTime.before(now)) {
                // 预约时间早于系统时间，不可预约
                log.debug("now: {}, startTime: {}", now, startTime);
                log.info("error-031: {}", vo);
                res.addError("error-031", vo);
                return res;
            }
            // find the first StartTime
            if (firstStartTime == null) {
                firstStartTime = startTime;
            } else if (startTime.before(firstStartTime)) {
                firstStartTime = startTime;
            }
            // find the last endTime
            if (lastEndTime == null) {
                lastEndTime = endTime;
            } else if (endTime.after(lastEndTime)) {
                lastEndTime = endTime;
            }
        }

        // query current device / activity all the appointment
        List<AppointmentDto> deviceAppointmentList = antiCorruptionServiceImp.queryAppointmentListByDeviceIdAndActivityNameAndDateRange(
                Arrays.asList(appointmentDataVO.getDeviceId()),
                appointmentDataVO.getActivityCode(),
                DateUtil.formatDate(firstStartTime, DateUtil.DATE_FORMAT),
                DateUtil.formatDate(lastEndTime, DateUtil.DATE_FORMAT),
                null);
        // query the slot limit number.
        int slotLimit = Integer.parseInt(SystemConfigPool.queryTimeSlotCount());
        for (AppointmentDataTimeSlotVO vo: timeList) {
            if (vo.getAction() == 1) {
                // remove action, no need handle
                continue;
            }
            try {
                startTime = DateUtil.parse(vo.getStartTime());
                endTime = DateUtil.parse(vo.getEndTime());
            } catch (ParseException e) {
            }
            // check if the time-slot has been filled
            int slotFilled = 0;
            for (AppointmentDto dto: deviceAppointmentList) {
                if (!(startTime.after(dto.getStartTime()))
                        && !(endTime.before(dto.getEndTime()))) {
                    // startTime <= dto.startTime && endTime >= dto.endTime
                    slotFilled++;
                }
            }
            // error-033 current time slot has been filled.
            if (slotFilled >= slotLimit) {
                log.debug("slotLimit: {}, slotFilled: {}", slotLimit, slotFilled);
                log.info("error-033: {}", vo);
                res.addError("error-033", vo);
                return res;
            }
        }

        // query all appointments to check if activity has been done.
        List<AppointmentDto> appointmentList = antiCorruptionServiceImp.queryAllByPatientSer(appointmentDataVO.getPatientSer());
        CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
        List<ActivityInstance> curCpAppointmentList =  new ArrayList<>();
        CarePathInstance carePathInstance = carePathAntiCorruptionServiceImp.queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(appointmentDataVO.getPatientSer(),
                appointmentDataVO.getInstanceId(),ActivityTypeEnum.valueOf(appointmentDataVO.getActivityType()));
        curCpAppointmentList.addAll(carePathInstance.getActivityInstances().stream().filter(activityInstance -> ActivityTypeEnum.APPOINTMENT.equals(activityInstance.getActivityType()))
                .collect(Collectors.toList()));
        if(!curCpAppointmentList.isEmpty()){
            List<AppointmentDto> curInstanceAppointmentList = new ArrayList<>();
            if(appointmentList!= null && !appointmentList.isEmpty()){
                curCpAppointmentList.forEach(activityInstance -> {
                    appointmentList.forEach(appointmentDto -> {
                        if(appointmentDto.getAppointmentId().equals(activityInstance.getInstanceID())){
                            curInstanceAppointmentList.add(appointmentDto);
                        }
                    });
                });
            }
            appointmentList.clear();
            appointmentList.addAll(curInstanceAppointmentList);
            Collections.sort(appointmentList);
        }
        for (AppointmentDataTimeSlotVO vo: timeList) {
            // loop the appointment time list.
            try {
                startTime = DateUtil.parse(vo.getStartTime());
                endTime = DateUtil.parse(vo.getEndTime());
            } catch (ParseException e) {
            }

            // find the same activity appointment
            for (int index = 0; index < appointmentList.size(); index++) {
                AppointmentDto dto = appointmentList.get(index);
                if (dto.getStatus().equalsIgnoreCase(AppointmentStatusEnum.FULFILLED.toString())) {
                    // activity done.
                    if (appointmentDataVO.getActivityCode().equals(dto.getReason())
                            && dto.getAppointmentId().equals(vo.getAppointmentId())) {
                        // error-032 current activity has been done.
                        res.addError("error-032", vo);
                        return res;
                    }
                } else if (dto.getStatus().equalsIgnoreCase(AppointmentStatusEnum.BOOKED.toString())
                        && vo.getAction() != 1) { // not remove action.
                    // booked
                    if (appointmentDataVO.getActivityCode().equals(dto.getReason())) {
                        // find the current appointment/ check previous appointment
                        if ((index - 1) >= 0 && !startTime.after(appointmentList.get(index - 1).getEndTime())) {
                            vo.setActName(ActivityCodesReader.getActivityCode(appointmentDataVO.getActivityCode()).getContent());
                            vo.setConflictActName(ActivityCodesReader.getSourceActivityCodeByRelativeCode(appointmentList.get(index - 1).getReason()).getContent());
                            res.addError("error-030", vo);
                            return res;
                        }

                        if ((index + 1) < appointmentList.size() && !endTime.before(appointmentList.get(index + 1).getStartTime())) {
                            vo.setActName(ActivityCodesReader.getActivityCode(appointmentDataVO.getActivityCode()).getContent());
                            vo.setConflictActName(ActivityCodesReader.getSourceActivityCodeByRelativeCode(appointmentList.get(index + 1).getReason()).getContent());
                            res.addError("error-034", vo);
                            return res;
                        }
                    }
                }
            }
        }
            return res;
    }


    /**
     * 自助到检
     *
     * @param hisId
     * @param deviceMacAddress
     * @return
     */
    @Path("/appointments/autoCheckIn")
    @POST
    public Response autoCheckIn(@QueryParam("hisId") String hisId, @QueryParam("deviceId") String deviceMacAddress) {
        //Case 1-1，没有找到该用户。
        if (StringUtils.isEmpty(hisId)) {
            return Response.ok(new AutoCheckInVO("", "", "", "", "", "", "N", AUTO_CHECK_IN_PATIENT_NOT_FOUND, new DeviceGuide())).build();
        }
        PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(new UserContext());
        PatientDto patientDto = patientAntiCorruptionServiceImp.queryPatientByHisId(hisId);
        //Case 1-2，没有找到该用户。
        if (patientDto == null) {
            return Response.ok(new AutoCheckInVO("", "", "", "", "", "", "N", AUTO_CHECK_IN_PATIENT_NOT_FOUND, new DeviceGuide())).build();
        }
        String patientSer = patientDto.getPatientSer();
        List<AppointmentVO> result = new ArrayList<>();
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(patientDto.getPatientSer())) {
            appointmentDtoList = antiCorruptionServiceImp.queryAppointmentListByPatientId(patientDto.getPatientSer());
        }

        //如果没有预约，则查看本地治疗预约是否存在。
        if (appointmentDtoList.isEmpty()) {
            TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
            String treatmentActivityCode;
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (appointmentStoredToLocal) {
                treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
                List<TreatmentAppointmentDTO> treatmentAppointmentDTOList = treatmentAppointmentService.queryTreatmentsAppointmentByPatientId(new Long(patientDto.getPatientSer()), treatmentActivityCode);
                if (treatmentAppointmentDTOList == null) {
                    return Response.ok(new AutoCheckInVO(hisId, patientDto.getChineseName(), null, null, null, null, "N", AUTO_CHECK_IN_INTERNAL_ERROR, new DeviceGuide())).build();
                } else {
                    List<AppointmentDto> appointmentDtoListForTreatment = new ArrayList<>();
                    treatmentAppointmentDTOList.forEach(treatmentAppointmentDTO -> appointmentDtoListForTreatment.add(treatmentAppointmentDTO2AppointmentDto(treatmentAppointmentDTO, true)));
                    appointmentDtoList = appointmentDtoListForTreatment;
                }
            }
        }

        appointmentDtoList = filterAppointmentsBeforeToday(appointmentDtoList);

        //Case 2-1，该用户没有预约。
        if (appointmentDtoList.isEmpty()) {
            return Response.ok(new AutoCheckInVO(hisId, patientDto.getChineseName(), "", "", "", "", "N", AUTO_CHECK_IN_NO_APPOINTMENT, new DeviceGuide())).build();
        }

        sortAppointmentList(appointmentDtoList);
        //获取今天的appointments
        List<AppointmentDto> appointmentsToday = getAppointmentsOfToday(appointmentDtoList);

        //Case 3 如果deviceMacAddress为空，则认为是集中到检。
        if (StringUtils.isEmpty(deviceMacAddress)) {
            AppointmentDto nextAppointmentDto = appointmentDtoList.get(0);
            String ariaDeviceIdOfNextAppointment = getDeviceIdOfAnAppointment(nextAppointmentDto);
            DeviceGuide deviceGuide = CallingGuideHelper.getDeviceGuideByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment);
            //Case 3-1 如果今天没有预约，则提示用户下一次预约的时间和设备。
            if (appointmentsToday.isEmpty()) {
                return Response.ok(
                        new AutoCheckInVO(
                                hisId,
                                patientDto.getChineseName(),
                                "",
                                DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                        DateUtil.SHORT_DATE_TIME_FORMAT),
                                CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                "N",
                                AUTO_CHECK_IN_NO_APPOINTMENT_FOR_TODAY,
                                deviceGuide)
                ).build();
            } else {//Case 3-2 如果今天有预约
                QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
                int isCheckedIn = queuingManagementService.ifAlreadyCheckedIn(nextAppointmentDto.getAppointmentId());
                //Case 3-2-1 如果当前预约已经到检完成，则提示已到检。
                if (isCheckedIn == 1) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDto.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    "N",
                                    AUTO_CHECK_IN_ALREADY_CHECKED_IN,
                                    deviceGuide)
                    ).build();
                }
                //Case 3-2-2 如果查看是否已到检的时候出错，则返回internal_error。
                if (isCheckedIn == -1) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDto.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    "N",
                                    AUTO_CHECK_IN_INTERNAL_ERROR,
                                    deviceGuide)
                    ).build();
                }
                //Case 3-2-3 到检成功，返回用户今天第一次的预约信息。
                QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
                queuingManagementDTO.setActivityCode(nextAppointmentDto.getReason());
                queuingManagementDTO.setAppointmentId(nextAppointmentDto.getAppointmentId());
                queuingManagementDTO.setDeviceId(getDeviceIdOfAnAppointment(nextAppointmentDto));
                queuingManagementDTO.setCheckInIdx(AUTO_CHECK_IN_DEFAULT_INDEX);
                queuingManagementDTO.setCheckInStatus(CheckInStatusEnum.WAITING);
                queuingManagementDTO.setCheckInTime(new Date());
                queuingManagementDTO.setStartTime(DateUtil.formatDate(nextAppointmentDto.getStartTime(), DateUtil.DATE_TIME_FORMAT));
                queuingManagementDTO.setHisId(hisId);
                queuingManagementDTO.setEncounterId(encounterServiceImp.queryByPatientSer(Long.parseLong(patientSer)).getId());
                queuingManagementDTO.setPatientSer(Long.parseLong(patientSer));
                boolean checkInResult = queuingManagementService.checkIn(queuingManagementDTO);
                if (checkInResult) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDto.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    "Y",
                                    AUTO_CHECK_IN_SUCCESS,
                                    deviceGuide)
                    ).build();
                } else {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDto.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceIdOfNextAppointment),
                                    "N",
                                    AUTO_CHECK_IN_INTERNAL_ERROR,
                                    deviceGuide)
                    ).build();
                }
            }
        } else {//Case 4 如果deviceMacAddress不为空，则认为是按设备到检。
            DeviceGuide deviceGuide = CallingGuideHelper.getDeviceGuideByDeviceMacAddress(configuration, deviceMacAddress);
            List<AppointmentDto> appointmentDtoListForThisDevice = getAppointmentsForDevice(appointmentDtoList,
                    CallingGuideHelper.getAriaDeviceIdByDeviceMacAddress(configuration, deviceMacAddress));
            List<AppointmentDto> appointmentDtoListForThisDeviceForToday = getAppointmentsForDevice(appointmentsToday,
                    CallingGuideHelper.getAriaDeviceIdByDeviceMacAddress(configuration, deviceMacAddress));
            //Case 4-1 如果当前设备有预约，但今天没有预约，则返回信息提示当前设备下一次预约的信息。
            if (!appointmentDtoListForThisDevice.isEmpty() && appointmentsToday.isEmpty()) {
                AppointmentDto nextAppointmentDtoOnThisDevice = appointmentDtoListForThisDevice.get(0);
                return Response.ok(
                        new AutoCheckInVO(
                                hisId,
                                patientDto.getChineseName(),
                                ActivityCodesReader.getActivityCode(nextAppointmentDtoOnThisDevice.getReason()).getContent(),
                                DateUtil.formatDate(nextAppointmentDtoOnThisDevice.getStartTime(),
                                        DateUtil.SHORT_DATE_TIME_FORMAT),
                                deviceMacAddress,
                                CallingGuideHelper.getDeviceRoomByDeviceMacAddress(configuration, deviceMacAddress),
                                "N",
                                AUTO_CHECK_IN_INCORRECT_DATE,
                                deviceGuide)
                ).build();
            }
            //Case 4-2 如果当前设备今天没有预约，但今天有其他预约，则返回今天的第一个预约信息。
            if (appointmentDtoListForThisDeviceForToday.isEmpty() && !appointmentsToday.isEmpty()) {
                AppointmentDto nextAppointmentDtoToday = appointmentsToday.get(0);
                return Response.ok(
                        new AutoCheckInVO(
                                hisId,
                                patientDto.getChineseName(),
                                ActivityCodesReader.getActivityCode(nextAppointmentDtoToday.getReason()).getContent(),
                                DateUtil.formatDate(nextAppointmentDtoToday.getStartTime(),
                                        DateUtil.SHORT_DATE_TIME_FORMAT),
                                CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoToday)),
                                CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoToday)),
                                "N",
                                AUTO_CHECK_IN_INCORRECT_DEVICE,
                                deviceGuide)
                ).build();
            }
            //Case 4-3 如果当前设备没有任何预约，并且今天也没有任何预约，则返回用户将来最近一次的预约信息。
            if (appointmentDtoListForThisDevice.isEmpty() && appointmentsToday.isEmpty()) {
                AppointmentDto nextAppointmentDto = appointmentDtoList.get(0);
                return Response.ok(
                        new AutoCheckInVO(
                                hisId,
                                patientDto.getChineseName(),
                                ActivityCodesReader.getActivityCode(nextAppointmentDto.getReason()).getContent(),
                                DateUtil.formatDate(nextAppointmentDto.getStartTime(),
                                        DateUtil.SHORT_DATE_TIME_FORMAT),
                                CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDto)),
                                CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDto)),
                                "N",
                                AUTO_CHECK_IN_INCORRECT_DATE_AND_DEVICE,
                                deviceGuide)
                ).build();
            }
            //Case 4-4 如果当前设备有今天的预约。
            if (!appointmentDtoListForThisDeviceForToday.isEmpty()) {
                AppointmentDto nextAppointmentDtoOnThisDeviceToday = appointmentDtoListForThisDeviceForToday.get(0);
                QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
                int isCheckedIn = queuingManagementService.ifAlreadyCheckedIn(nextAppointmentDtoOnThisDeviceToday.getAppointmentId());
                //Case 4-4-1 如果当前预约已经到检完成，则提示已到检。
                if (isCheckedIn == 1) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDtoOnThisDeviceToday.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDtoOnThisDeviceToday.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    "N",
                                    AUTO_CHECK_IN_ALREADY_CHECKED_IN,
                                    deviceGuide)
                    ).build();
                }
                //Case 4-4-2 如果查看是否已到检的时候出错，则返回internal_error。
                if (isCheckedIn == -1) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDtoOnThisDeviceToday.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDtoOnThisDeviceToday.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    "N",
                                    AUTO_CHECK_IN_INTERNAL_ERROR,
                                    deviceGuide)
                    ).build();
                }
                //Case 4-4-3 到检成功，返回用户今天第一次的预约信息。
                QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
                queuingManagementDTO.setActivityCode(nextAppointmentDtoOnThisDeviceToday.getReason());
                queuingManagementDTO.setAppointmentId(nextAppointmentDtoOnThisDeviceToday.getAppointmentId());
                queuingManagementDTO.setDeviceId(getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday));
                queuingManagementDTO.setCheckInIdx(AUTO_CHECK_IN_DEFAULT_INDEX);
                queuingManagementDTO.setCheckInStatus(CheckInStatusEnum.WAITING);
                queuingManagementDTO.setCheckInTime(new Date());
                queuingManagementDTO.setStartTime(DateUtil.formatDate(nextAppointmentDtoOnThisDeviceToday.getStartTime(), DateUtil.DATE_TIME_FORMAT));
                queuingManagementDTO.setHisId(hisId);
                queuingManagementDTO.setEncounterId(encounterServiceImp.queryByPatientSer(Long.parseLong(patientSer)).getId());
                queuingManagementDTO.setPatientSer(StringUtils.isEmpty(patientSer) ? null:Long.parseLong(patientSer));
                boolean checkInResult = queuingManagementService.checkIn(queuingManagementDTO);

                String ariaDeviceId = getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday);
                if (checkInResult) {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDtoOnThisDeviceToday.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDtoOnThisDeviceToday.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, ariaDeviceId),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, ariaDeviceId),
                                    "Y",
                                    AUTO_CHECK_IN_SUCCESS,
                                    deviceGuide)
                    ).build();
                } else {
                    return Response.ok(
                            new AutoCheckInVO(
                                    hisId,
                                    patientDto.getChineseName(),
                                    ActivityCodesReader.getActivityCode(nextAppointmentDtoOnThisDeviceToday.getReason()).getContent(),
                                    DateUtil.formatDate(nextAppointmentDtoOnThisDeviceToday.getStartTime(),
                                            DateUtil.SHORT_DATE_TIME_FORMAT),
                                    CallingGuideHelper.getDeviceMacAddressByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    CallingGuideHelper.getDeviceRoomByAriaDeviceId(configuration, getDeviceIdOfAnAppointment(nextAppointmentDtoOnThisDeviceToday)),
                                    "N",
                                    AUTO_CHECK_IN_INTERNAL_ERROR,
                                    deviceGuide)
                    ).build();
                }
            }
            return Response.status(Response.Status.OK).entity(result).build();
        }

    }

    //过滤掉今天之前的预约
    private List<AppointmentDto> filterAppointmentsBeforeToday(List<AppointmentDto> appointmentDtoList) {
        Date today = new Date();
        List<AppointmentDto> appointmentsToday = new ArrayList<>();
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            if (DateTimeComparator.getDateOnlyInstance().compare(appointmentDto.getStartTime(), today) >= 0) {
                appointmentsToday.add(appointmentDto);
            }
        }
        return appointmentsToday;
    }

    //获取今天的预约
    private List<AppointmentDto> getAppointmentsOfToday(List<AppointmentDto> appointmentDtoList) {
        Date today = new Date();
        List<AppointmentDto> appointmentsToday = new ArrayList<>();
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            if (DateTimeComparator.getDateOnlyInstance().compare(appointmentDto.getStartTime(), today) == 0) {
                appointmentsToday.add(appointmentDto);
            }
        }
        return appointmentsToday;
    }

    //对预约按照预约时间startTime排序
    private void sortAppointmentList(List<AppointmentDto> appointmentDtoList) {
        boolean swapped = true;
        int j = 0;
        AppointmentDto appointmentDtoTemp;
        while (swapped) {
            swapped = false;
            j++;
            for (int i = 0; i < appointmentDtoList.size() - j; i++) {
                if (appointmentDtoList.get(i).getStartTime().compareTo(appointmentDtoList.get(i + 1).getStartTime()) > 0) {
                    appointmentDtoTemp = appointmentDtoList.get(i);
                    appointmentDtoList.set(i, appointmentDtoList.get(i + 1));
                    appointmentDtoList.set(i + 1, appointmentDtoTemp);
                    swapped = true;
                }
            }
        }
    }

    /**
     * 根据Aria的deviceId来获取该设备上的预约
     * @param appointmentDtoList appointment Dto List
     * @param deviceId device Id
     * @return List<AppointmentDto>
     */
    private List<AppointmentDto> getAppointmentsForDevice(List<AppointmentDto> appointmentDtoList, String deviceId) {
        List<AppointmentDto> appointmentsForASingleDevice = new ArrayList<>();
        for (AppointmentDto appointmentDto : appointmentDtoList) {
            String currentDeviceId = getDeviceIdOfAnAppointment(appointmentDto);
            if (currentDeviceId != null && currentDeviceId.equals(deviceId)) {
                appointmentsForASingleDevice.add(appointmentDto);
            }
        }
        return appointmentsForASingleDevice;
    }

    /**
     * 获取一个预约中Aria里的deviceId
     * @param appointmentDto AppointmentDto
     * @return Device Id
     */
    private String getDeviceIdOfAnAppointment(AppointmentDto appointmentDto) {
        for (ParticipantDto participantDto : appointmentDto.getParticipants()) {
            if (participantDto.getType() == ParticipantTypeEnum.DEVICE) {
                return participantDto.getParticipantId();
            }
        }
        return null;
    }

    private AppointmentDto treatmentAppointmentDTO2AppointmentDto(TreatmentAppointmentDTO treatmentAppointmentDTO, boolean id2AppointmentId) {
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setAppointmentId(treatmentAppointmentDTO.getAppointmentId());
        if (StringUtils.isEmpty(appointmentDto.getAppointmentId()) && id2AppointmentId) {
            appointmentDto.setAppointmentId(treatmentAppointmentDTO.getUid());
        }
        appointmentDto.setStartTime(treatmentAppointmentDTO.getStartTime());
        appointmentDto.setEndTime(treatmentAppointmentDTO.getEndTime());
        appointmentDto.setStatus(treatmentAppointmentDTO.getStatus());
        appointmentDto.setReason(treatmentAppointmentDTO.getActivityCode());
        appointmentDto.setParticipants(Arrays.asList(new ParticipantDto(ParticipantTypeEnum.PATIENT, String.valueOf(treatmentAppointmentDTO.getPatientSer())),
                new ParticipantDto(ParticipantTypeEnum.DEVICE, treatmentAppointmentDTO.getDeviceId())));
        Date nowDate = new Date();
        appointmentDto.setCreatedDT(nowDate);
        appointmentDto.setLastModifiedDT(nowDate);
        return appointmentDto;
    }

    /**
     * 人工到检操作.<br>
     * @param userContext UserContext
     * @param queuingManagementDTO queuingManagement DTO
     * @return Response
     */
    @Path("/appointment/manualCheckInWaiting")
    @POST
    public Response manualCheckInWaiting(@Auth UserContext userContext, QueuingManagementDTO queuingManagementDTO) {
        QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl(userContext);
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        boolean result;
        try {
            manualCheckInLock.lock();
            int i = queuingManagementService.ifAlreadyCheckedIn(queuingManagementDTO.getAppointmentId());
            if (i > 0) {
                return Response.ok(true).build();
            }
            if (StringUtils.isNotEmpty(queuingManagementDTO.getStartTime())) {
                try {
                    Date hms = DateUtil.parse(queuingManagementDTO.getStartTime());
                    Calendar hmsCalendar = Calendar.getInstance();
                    hmsCalendar.setTime(hms);
                    Calendar curDate = Calendar.getInstance();
                    curDate.set(Calendar.HOUR_OF_DAY, hmsCalendar.get(Calendar.HOUR_OF_DAY));
                    curDate.set(Calendar.MINUTE, hmsCalendar.get(Calendar.MINUTE));
                    curDate.set(Calendar.SECOND, hmsCalendar.get(Calendar.SECOND));
                    queuingManagementDTO.setStartTime(DateUtil.formatDate(curDate.getTime(), DateUtil.DATE_TIME_FORMAT));
                    queuingManagementDTO.setEncounterId(encounterServiceImp.queryByPatientSer(queuingManagementDTO.getPatientSer()).getId());
                } catch (ParseException e) {
                    log.error("ParseException: {}", e.getMessage());
                }
            }
            result = queuingManagementService.checkIn(queuingManagementDTO);
        } finally {
            manualCheckInLock.unlock();
        }
        return Response.ok(result).build();
    }

    /**
     * 取消到检.<br>
     * 需要 appointmentId<br>
     * @param userContext User Context
     * @param queuingManagementDTO Queuing Management DTO
     * @return Response
     */
    @Path("/appointment/unCheckIn")
    @POST
    public Response unCheckInFromWaitingList(@Auth UserContext userContext, QueuingManagementDTO queuingManagementDTO) {
        QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl(userContext);
        if (StringUtils.isNotEmpty(queuingManagementDTO.getAppointmentId())) {
            queuingManagementDTO.setCheckInIdx(-1);
            queuingManagementDTO.setCheckInStatus(CheckInStatusEnum.DELETED);
            int del = queuingManagementService.unCheckIn(queuingManagementDTO);
            if (del > 0) {
                return Response.ok(true).build();
            } else {
                return Response.ok(false).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(queuingManagementDTO).build();
        }

    }

    /**
     * 置顶操作(需要参数：id或者appointmentId和activityCode，checkInIdx).<br>
     * @param userContext User Context
     * @param queuingManagementDTO Queuing Management DTO
     * @return Response
     */
    @Path("/appointment/stickCheckInWaiting")
    @POST
    public Response stickCheckInWaiting(@Auth UserContext userContext, QueuingManagementDTO queuingManagementDTO) {
        if ((StringUtils.isNotEmpty(queuingManagementDTO.getId()) ||
                (StringUtils.isNotEmpty(queuingManagementDTO.getAppointmentId()) && StringUtils.isNotEmpty(queuingManagementDTO.getActivityCode())))
                && queuingManagementDTO.getCheckInIdx() != null) {
            QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
            boolean result = queuingManagementService.checkInStick(queuingManagementDTO);
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(queuingManagementDTO).build();
        }
    }

    /**
     * 将calling列表中的数据转移到waiting列表中.<br>
     * @param queuingManagementDTO Queuing Management DTO
     * @return Response
     */
    @Path("/appointment/calling2waiting")
    @POST
    public Response calling2waiting(@Auth UserContext userContext, QueuingManagementDTO queuingManagementDTO) {
        if ((StringUtils.isNotEmpty(queuingManagementDTO.getId()) ||
                (StringUtils.isNotEmpty(queuingManagementDTO.getAppointmentId()) && StringUtils.isNotEmpty(queuingManagementDTO.getActivityCode())))
                && queuingManagementDTO.getCheckInIdx() != null) {
            QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
            queuingManagementDTO.setCheckInStatus(CheckInStatusEnum.WAITING);
            boolean result = queuingManagementService.checkInStick(queuingManagementDTO);
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(queuingManagementDTO).build();
        }
    }

    @Path("/appointment/callingNext")
    @POST
    public Response callingNextPatient(@Auth UserContext userContext, List<QueuingManagementDTO> queuingManagementDTOList) {
        QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
        boolean result = queuingManagementService.callingNext(queuingManagementDTOList);
        return Response.ok(result).build();
    }

    private void assembleResult(List<AppointmentDto> appointmentDtoList, List<AppointmentListVO> result) {
        AppointmentListVO appointmentListVO = null;
        Map<String, PatientDto> tmpMap = new HashMap<>();

        // 2018-01-24 HotFix start: improve performance by getting patient info within one Fhir request.
        Set<String> patientIdSet = new HashSet<>(appointmentDtoList.size());
        for (AppointmentDto dto : appointmentDtoList) {
            for (ParticipantDto participantDto : dto.getParticipants()) {
                if (ParticipantTypeEnum.PATIENT.equals(participantDto.getType())) {
                    patientIdSet.add(participantDto.getParticipantId());
                }
            }
        }
        tmpMap = patientAntiCorruptionServiceImp.queryPatientListByPatientIdList(new ArrayList<String>(patientIdSet));
        // 2018-01-24 HotFix end

        for (AppointmentDto dto : appointmentDtoList) {
            for (ParticipantDto participantDto : dto.getParticipants()) {
                if (ParticipantTypeEnum.PATIENT.equals(participantDto.getType())) {
                    PatientDto patientDto;
                    if (!tmpMap.containsKey(participantDto.getParticipantId())) {
                        patientDto = patientCacheService.queryPatientByPatientId(participantDto.getParticipantId());
                        tmpMap.put(participantDto.getParticipantId(), patientDto);
                    } else {
                        patientDto = tmpMap.get(participantDto.getParticipantId());
                    }
                    appointmentListVO = new AppointmentListVOAssembler(dto, patientDto).getViewDto();
                    appointmentListVO.setStatus(dto.getStatus());
                    result.add(appointmentListVO);
                }
            }
            if(appointmentListVO != null){
                for (ParticipantDto participantDto : dto.getParticipants()) {
                    if (ParticipantTypeEnum.DEVICE.equals(participantDto.getType())) {
                        appointmentListVO.setDeviceId(participantDto.getParticipantId());
                    }
                }
            }
        }
//      处理预约是否已经到检
        List<String> appointmentIdList = new ArrayList<>();
        result.forEach(appointmentListVO1 -> {
            if (appointmentListVO1.getStatus().equalsIgnoreCase(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED))) {
                appointmentIdList.add(appointmentListVO1.getAppointmentId());
            }
        });
//        根据appointmentIdList查询到检列表
        QueuingManagementServiceImpl queuingManagementService = new QueuingManagementServiceImpl();
        QueuingManagementDTO searchCondition = new QueuingManagementDTO() {{
            setAppointmentIdList(appointmentIdList);
            setCheckInStatusList(Arrays.asList(CheckInStatusEnum.WAITING, CheckInStatusEnum.CALLED, CheckInStatusEnum.CALLING));
        }};
        List<QueuingManagementVO> queuingManagementVOList = queuingManagementService.queryCheckInList(searchCondition);
        result.forEach(appointmentListVO1 ->
                queuingManagementVOList.forEach(queuingManagementVO -> {
                    if (queuingManagementVO.getAppointmentId().equals(appointmentListVO1.getAppointmentId())) {
                        appointmentListVO1.setCheckIn(true);
                    }
                })
        );
    }

    @Path("/appointments/searchQueue")
    @GET
    public Response searchQueue(@Auth UserContext userContext,
                                @QueryParam("activityCode") String activityCode,
                                @QueryParam("deviceId") String deviceId) {
        List<QueueListVO> queueList = new ArrayList<>();
        QueuingManagementServiceImpl service = new QueuingManagementServiceImpl();
        QueuingManagementDTO searchCondition = new QueuingManagementDTO();
        searchCondition.setActivityCode(activityCode);
        if (StringUtils.isNotEmpty(deviceId)) {
            searchCondition.setDeviceId(deviceId);
        }
        Date curDate = new Date();
        searchCondition.setCheckInStartTime(curDate);
        searchCondition.setCheckInEndTime(curDate);
        searchCondition.setCheckInStatusList(Arrays.asList(CheckInStatusEnum.WAITING, CheckInStatusEnum.CALLING, CheckInStatusEnum.CALLED));
        List<QueuingManagementVO> queue = service.queryCheckInList(searchCondition);
        if (!queue.isEmpty()) {
            queueList = new QueueListAssembler(queue, configuration, userContext).getQueue();
        }

        return Response.status(Response.Status.OK).entity(queueList).build();
    }


    /**
     * Call Patient by Device Id & Patient List.<br>
     *
     * @param userContext User Context
     * @param vo          CallPatient VO
     * @return Calling Service Status from Calling System
     */
    @Path("/appointments/callPatient")
    @POST
    public Response callPatient(@Auth UserContext userContext, CallPatientVO vo) {
        if (HisPatientInfoConfigService.getConfiguration() == null
                || !HisPatientInfoConfigService.getConfiguration().isCallingSystemEnable()) {
            log.warn("Please check the HisSystem.yaml file, and ensure the calling system switcher is enabled.");
            return Response.ok(ServerStatusEnum.NO_CONFIGURATION).build();
        }

        // default no configuration file. if calling server ready, would return status.
        ServerStatusEnum status = ServerStatusEnum.NORMAL;
        if (CallingService.isReady() && vo != null) {
            final String appointmentId = vo.getAppointmentId();
            final String ariaDeviceId = getAriaDeviceIdByAppointmentId(appointmentId);
            final String callingDeviceId = CallingService.getCallingDeviceIdByAriaDeviceId(ariaDeviceId);

            log.debug("Call patient with appointmentId[{}], ariaDeviceId[{}], callingDeviceId[{}]", appointmentId, ariaDeviceId, callingDeviceId);

            // Server send the Call request to 3rd Call Server, and waiting for Calling Status.
            CallingServiceVO callingServiceVO = new CallingServiceVO();
            // calling system device id.
            callingServiceVO.setDeviceId(callingDeviceId);
            // room name
            final String deviceRoom = CallingService.getDeviceRoomByAriaDeviceId(ariaDeviceId);
            callingServiceVO.setDeviceRoom(deviceRoom);
            // patient name list (3 persons by default)
            callingServiceVO.addPatientList(vo.getPatientName());
            // get patient guide information by appointment id
            final CallingGuide patientGuide = getPatientGuide(ariaDeviceId);
            callingServiceVO.setGuide(patientGuide);

            try {
                String reqStr = new ObjectMapper().writeValueAsString(callingServiceVO.toMap());
                // send the message to calling server
                status = CallingService.sendMsg("Body=" + reqStr);
            } catch (JsonProcessingException e) {
                // parse map to json, would never happened.
                log.error("JsonProcessingException: {}", e.getMessage());
            }
        }

        return Response.ok(status).build();
    }

    /**
     * Return Aria Device Id by Appointment Id.<br>
     *
     * @param appointmentId Appointment Id
     * @return Aria Device Id
     */
    protected String getAriaDeviceIdByAppointmentId(String appointmentId) {
        String ariaDeviceId = StringUtils.EMPTY;
        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        if (appointmentStoredToLocal) {
            if (!StringUtils.isNumeric(appointmentId)) {
                TreatmentAppointmentService treatmentAppointmentService = new TreatmentAppointmentService(new UserContext());
                TreatmentAppointmentDTO treatmentAppointmentDTO = treatmentAppointmentService.queryByUidOrAppointmentId(appointmentId);
                if (treatmentAppointmentDTO != null) {
                    return treatmentAppointmentDTO.getDeviceId();
                }
            }
        }
        if (StringUtils.isNumeric(appointmentId)) {
            AppointmentDto aDto = antiCorruptionServiceImp.queryAppointmentById(appointmentId);
            if (aDto != null) {
                List<ParticipantDto> participantList = aDto.getParticipants();
                for (ParticipantDto pDto : participantList) {
                    if (ParticipantTypeEnum.DEVICE.equals(pDto.getType())) {
                        ariaDeviceId = pDto.getParticipantId();
                        break;
                    }
                }
            }
        }
        return ariaDeviceId;
    }

    /**
     * Return the Patient Guide by appointment Id
     *
     * @param ariaDeviceId aria device Id
     * @return Patient Guide Information
     */
    protected CallingGuide getPatientGuide(String ariaDeviceId) {
        DeviceGuide deviceGuide = CallingGuideHelper.getDeviceGuideByAriaDeviceId(configuration, ariaDeviceId);
        CallingGuide patientGuide = new CallingGuide();
        if(deviceGuide.getTexts() != null) {
            patientGuide.addTextList(deviceGuide.getTexts());
        }
        if(deviceGuide.getImageUrls() != null){
            patientGuide.addImageList(deviceGuide.getImageUrls());
        }
        if(deviceGuide.getVideoUrls() != null){
            patientGuide.addVideoList(deviceGuide.getVideoUrls());
        }
        return patientGuide;
    }

    private List<TreatmentAppointmentDTO> queryPatientLocalTreatmentAppointment(UserContext context, Long patientSer, String deviceId) {
        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
        if (!appointmentStoredToLocal) {
            return new ArrayList<>(0);
        }
        TreatmentAppointmentService service = new TreatmentAppointmentService(context);
        List<TreatmentAppointmentDTO> dtoList = service.queryAppointmentListByPatientSerAndDeviceId(patientSer, deviceId);
        return dtoList;
    }
}
