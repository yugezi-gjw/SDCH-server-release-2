package com.varian.oiscn.encounter.resource;

import com.varian.oiscn.anticorruption.resourceimps.*;
import com.varian.oiscn.base.codesystem.PatientLabelPool;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.cache.PatientCache;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.patient.Contact;
import com.varian.oiscn.core.patient.GenderEnum;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.physciancomment.PhysicianCommentDto;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeGroupVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeVO;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.PatientEncounterEndPlan;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.assembler.EncounterAssembler;
import com.varian.oiscn.encounter.assign.AssignResourceServiceImp;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPayment;
import com.varian.oiscn.encounter.confirmpayment.ConfirmPaymentServiceImp;
import com.varian.oiscn.encounter.dynamicform.DynamicFormDataMapper;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstance;
import com.varian.oiscn.encounter.dynamicform.DynamicFormInstanceServiceImp;
import com.varian.oiscn.encounter.history.EncounterTitleItem;
import com.varian.oiscn.encounter.isocenter.ISOCenter;
import com.varian.oiscn.encounter.isocenter.ISOCenterServiceImp;
import com.varian.oiscn.encounter.isocenter.ISOPlanTretment;
import com.varian.oiscn.encounter.service.EncounterServiceImp;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoServiceImp;
import com.varian.oiscn.encounter.targetvolume.PlanTargetVolumeServiceImp;
import com.varian.oiscn.encounter.targetvolume.TargetVolumeServiceImp;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadServiceImp;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadVO;
import com.varian.oiscn.encounter.view.DynamicFormItemsAndTemplateInfo;
import com.varian.oiscn.encounter.view.EncounterVO;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 6/14/2017.
 */
@Path("/")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EncounterResource extends AbstractResource {

    /**
     * The patient anti-corruption service instance
     */
    private PatientAntiCorruptionServiceImp antiCorruptionServiceImp;

    public EncounterResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        antiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
    }

    /**
     * Search encounter of a patient
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @return the response
     */
    @Path("/encounter/search")
    @GET
    public Response search(@Auth UserContext userContext,
                           @QueryParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        EncounterVO encounterVO = new EncounterVO();
        if (encounter != null) {
            encounterVO = EncounterAssembler.getEncounterVO(encounter);
        }

        PatientCacheService patientCacheService = new PatientCacheService();
        PatientDto patientFromFhir = patientCacheService.queryPatientByPatientId(patientSer.toString());

        if (patientFromFhir == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
        CoverageDto insuranceType = coverageAntiCorruptionServiceImp.queryByPatientId(patientFromFhir.getPatientSer());
        if (insuranceType != null) {
            encounterVO.setInsuranceType(insuranceType.getInsuranceTypeDesc());
            encounterVO.setInsuranceTypeCode(insuranceType.getInsuranceTypeCode());
        }
        return Response.ok(encounterVO).build();
    }

    /**
     * Complete encounter of a patient
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @return response
     */
    @Path("/encounter/completeByPatientSer")
    @PUT
    public Response completeEncounter(@Auth UserContext userContext,
                                      @QueryParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        //1. Complete encounter in local db
        completeEncounterInLocal(userContext, patientSer);

        //2. Unmark patient active flag in ARIA and Unmark urgent flag because of the limitation of FHIR.
        unmarkActiveStatus(configuration, patientSer);
        unmarkUrgentStatus(configuration, patientSer);

        PatientDto patientDto = antiCorruptionServiceImp.queryPatientByPatientIdWithPhoto(patientSer.toString());
        if (patientDto != null) {
            //清除ARIA里的encounter信息，目前只清除Alert
            clearPatientEncounterInAria(configuration, patientDto);
            //将ARIA里最新的patient信息同步回本地
            syncPatientDemographic2LocalDB(patientDto, userContext);
        }

        //3. Cancel all available tasks
        cancelUncompletedTasks(patientSer);

        //4. Cancel all available appointments
        cancelUncompletedAppointments(patientSer);

        //5. ClearSetupPhotos
        clearSetupPhotos(userContext, patientSer);

        //7.Clear PhysicianComment
        clearPhysicianComment(patientSer);

        //6. ReleaseAssignedResource
        releaseAssignedResource(userContext, patientSer);
        //清除缓存
        PatientCache.remove(String.valueOf(patientSer));

        return Response.ok(patientSer).build();
    }

    /**
     * Sync the patient demographic data to local db when completed encounter.
     *
     * @param patientDto  the patient dto
     * @param userContext user context
     */
    private void syncPatientDemographic2LocalDB(PatientDto patientDto, UserContext userContext) {
        //TODO: 目前无法解决encounter和patient两个project互相依赖，所以只能把update patient的代码在encounter里重复一份
        if (patientDto != null && isNotEmpty(patientDto.getPatientSer())) {
            EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
            //患者既往病史只存在于本地数据库中，所以需要先从本地数据库中assembler回来
            Patient patientInLocal = encounterServiceImp.queryPatientByPatientSer(Long.parseLong(patientDto.getPatientSer()));
            patientDto.setPatientHistory(patientInLocal.getPatientHistory());
            encounterServiceImp.updatePatient(getPatient(patientDto), Long.parseLong(patientDto.getPatientSer()));
        }
    }

    /**
     * Assembler patient data from patient dto to patient
     *
     * @param patientDto patient dto
     * @return patient
     */
    private Patient getPatient(PatientDto patientDto) {
        Patient patient = new Patient();
        if (StringUtils.isNotBlank(patientDto.getPatientSer())) {
            Long patientSer = Long.parseLong(patientDto.getPatientSer());
            patient.setPatientSer(patientSer);
        }
        patient.setHisId(patientDto.getHisId());
        patient.setRadiationId(patientDto.getAriaId()); //VID
        patient.setNationalId(patientDto.getNationalId());
        patient.setChineseName(patientDto.getChineseName());
        patient.setEnglishName(patientDto.getEnglishName());
        patient.setPinyin(patientDto.getPinyin());
        patient.setGender(GenderEnum.fromCode(patientDto.getGender()));
        patient.setBirthDate(patientDto.getBirthday());
        Contact contact = new Contact();
        contact.setName(patientDto.getContactPerson());
        contact.setMobilePhone(patientDto.getContactPhone());
        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);
        patient.setContacts(contacts);
        patient.setMobilePhone(patientDto.getTelephone());
        patient.setAddress(patientDto.getAddress());
        patient.setPhoto(Base64.encodeBase64String(patientDto.getPhoto()));
        patient.setPatientHistory(patientDto.getPatientHistory());
        return patient;
    }

    /**
     * Clear physician comment of a patient when completed encounter.
     *
     * @param patientSer the patient ser
     */
    private void clearPhysicianComment(Long patientSer) {
        CommunicationAntiCorruptionServiceImp communicationAntiCorruptionServiceImp = new CommunicationAntiCorruptionServiceImp();
        communicationAntiCorruptionServiceImp.errorPhysicianComment(new PhysicianCommentDto() {{
            setPatientSer(String.valueOf(patientSer));
        }});
    }

    /**
     * Release assigned resources when completed encounter
     *
     * @param userContext user context
     * @param patientSer  the patient ser
     */
    private void releaseAssignedResource(UserContext userContext, Long patientSer) {
        AssignResourceServiceImp assignResourceServiceImp = new AssignResourceServiceImp(userContext);
        // null: the patient with all activity code
        assignResourceServiceImp.deleteAssignedResource(patientSer, null);
    }

    /**
     * Clear setup photos when completed encounter
     *
     * @param userContext user context
     * @param patientSer  the patient ser
     * @return true for success and false for fail.
     */
    private boolean clearSetupPhotos(UserContext userContext, Long patientSer) {
        SetupPhotoServiceImp setupPhotoServiceImp = new SetupPhotoServiceImp(userContext);
        return setupPhotoServiceImp.clearSetupPhotos(patientSer);
    }

    /**
     * Disconnect Current Carepath and Link to a new Carepath.<br>
     *
     * @param userContext        UserContext
     * @param patientSer         patientSer
     * @param carepathTemplateId new Carepath Template Id
     * @return Response (accepted / notModified)
     */
    @Path("/encounter/link-carepath/{patientSer}/{carepathId}")
    @POST
    public Response linkNewCarePath(@Auth UserContext userContext,
                                    @PathParam("patientSer") Long patientSer,
                                    @PathParam("carepathId") String carepathTemplateId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        boolean result = true;

        // 1. Cancel all available tasks
        if (result) {
            result = cancelUncompletedTasks(patientSer);
        }

        // 2. Cancel all available appointments
        if (result) {
            result = cancelUncompletedAppointments(patientSer);
        }

        // 3. Clear setup photos before.
        if (result) {
            result = clearSetupPhotos(userContext, patientSer);
        }

        // 4. Joint to the new carepath
        String newCarePathInstanceId = StringUtils.EMPTY;
        if (result) {
            newCarePathInstanceId = linkCarepath(patientSer, carepathTemplateId);
        }

        // 5. Update encounter with new care path instance.
        if (StringUtils.isNotBlank(newCarePathInstanceId)) {
            result = updateEncounterWithNewCarePath(
                    userContext,
                    patientSer,
                    newCarePathInstanceId,
                    EncounterCarePath.EncounterCarePathCategoryEnum.PRIMARY);
        }
//        6. Cancel  QueueManagement and Appointment
        if (result) {
            switchCarePathEncounterInLocal(userContext, patientSer);
        }
        if (result) {
            // TODO: confirm - is it useful?
            PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer.toString());
        }
        return result ? Response.accepted(new HashMap<>(0)).build() : Response.serverError().build();
    }

    /**
     * Disconnect Current Carepath and Link to a new Optional Carepath.<br>
     *
     * @param userContext        UserContext
     * @param patientSer         patientSer
     * @param carepathTemplateId new Carepath Template Id
     * @return Response (accepted / notModified)
     */
    @Path("/encounter/link-carepath/optional/{patientSer}/{carepathId}")
    @POST
    public Response linkNewOptionalCarePath(@Auth UserContext userContext,
                                            @PathParam("patientSer") Long patientSer,
                                            @PathParam("carepathId") String carepathTemplateId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        boolean result = true;
        // 1. Joint to the new carepath
        String newCarePathInstanceId = StringUtils.EMPTY;
        if (result) {
            newCarePathInstanceId = linkCarepath(patientSer, carepathTemplateId);
        }
        // 2. Update encounter with new care path instance.
        if (StringUtils.isNotBlank(newCarePathInstanceId)) {
            result = updateEncounterWithNewCarePath(userContext, patientSer, newCarePathInstanceId, EncounterCarePath.EncounterCarePathCategoryEnum.OPTIONAL);
        }
        if (result) {
            // TODO: confirm - is it useful?
            PatientEncounterHelper.syncEncounterCarePathByPatientSer(patientSer.toString());
        }
        return result ? Response.accepted(new HashMap<>(0)).build() : Response.serverError().build();
    }


    /**
     * Save physician comments
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterVO encounter vo
     * @return response
     */
    @Path("/encounter/comment/{patientSer}")
    @POST
    public Response modifyPhysicianComment(@Auth UserContext userContext,
                                           @PathParam("patientSer") Long patientSer,
                                           EncounterVO encounterVO) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }

        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        PhysicianCommentDto dto = new PhysicianCommentDto();
        dto.setPatientSer(patientSer.toString());
        dto.setComments(encounterVO.getPhysicianComment());
        dto.setLastUpdateTime(new Date());
        dto.setPractitionerId(String.valueOf(userContext.getLogin().getResourceSer()));
        boolean ok = encounterServiceImp.modifyPhysicianComment(patientSer, dto);
        return Response.ok(ok).build();
    }

    /**
     * Query physician comments
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @return response
     */
    @Path("/encounter/comment/{patientSer}")
    @GET
    public Response searchPhysicianComment(@Auth UserContext userContext,
                                           @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        String comment = encounter.getPhysicianComment();
        if (StringUtils.isEmpty(comment)) {
            comment = encounterServiceImp.queryPhysicianCommentFromAria(patientSer.toString());
        }
        return Response.ok(new KeyValuePair("physicianComment", comment)).build();
    }

    /**
     * Cancel uncompleted appointments of patient
     *
     * @param patientSer patient ser
     * @return true/false
     */
    protected boolean cancelUncompletedAppointments(Long patientSer) {
        boolean result = true;
        AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        List<AppointmentDto> appointmentDtos = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientId(patientSer.toString());
        // appointmentDtos.stream().forEach(appointmentDto -> {
        for (AppointmentDto appointmentDto : appointmentDtos) {
            //Can't use the appointmentDto directly to update because fhir will throw exception
            AppointmentDto needUpdateAppointment = new AppointmentDto();
            needUpdateAppointment.setAppointmentId(appointmentDto.getAppointmentId());
            needUpdateAppointment.setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.CANCELLED));
            String updateId = appointmentAntiCorruptionServiceImp.updateAppointment(needUpdateAppointment);
            if (StringUtils.isBlank(updateId)) {
                result = false;
                break;
            }
            // });
        }
        return result;
    }

    /**
     * Cancel uncompleted tasks of a patient.
     *
     * @param patientSer patient ser
     * @return true/false
     */
    protected boolean cancelUncompletedTasks(Long patientSer) {
        boolean result = true;
        OrderAntiCorruptionServiceImp orderAntiCorruptionServiceImp = new OrderAntiCorruptionServiceImp();
        List<OrderDto> orderDtos = orderAntiCorruptionServiceImp.queryOrderListByPatientId(patientSer.toString());
        //orderDtos.stream().forEach(orderDto -> {
        for (OrderDto orderDto : orderDtos) {
            // Can't use the taskDto directly to update because fhir will throw exception
            OrderDto needUpdateOrderDto = new OrderDto();
            needUpdateOrderDto.setOrderId(orderDto.getOrderId());
            needUpdateOrderDto.setDueDate(orderDto.getDueDate() == null ? new Date() : orderDto.getDueDate());
            // Reject在fhir配置里对应的是cancel
            needUpdateOrderDto.setOrderStatus(OrderStatusEnum.getDisplay(OrderStatusEnum.REJECTED));
            String orderId = orderAntiCorruptionServiceImp.updateOrder(needUpdateOrderDto);
            if (StringUtils.isBlank(orderId)) {
                // FIXME: FHIR update failed.
                result = false;
                break;
            }
        }
        //});
        return result;
    }

    /**
     * Complete encounter in local db.
     *
     * @param userContext user context
     * @param patientSer  patient ser
     */
    protected void completeEncounterInLocal(UserContext userContext, Long patientSer) {
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        if (encounter != null) {
            encounter.setStatus(StatusEnum.FINISHED);
            encounter.setEncounterCarePathList(null);
            encounterServiceImp.updateByPatientSer(encounter, patientSer);
//          将当前的计划存储到关系表中
            TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
            Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp
                    .getActivityEncounterTxSummaryByPatientSer(patientSer.toString());
            if (treatmentSummaryDtoOptional.isPresent() && treatmentSummaryDtoOptional.get().getPlans() != null) {
                List<EncounterEndPlan> list = new ArrayList<>();
                treatmentSummaryDtoOptional.get().getPlans().forEach(planSummaryDto -> {
                    list.add(new EncounterEndPlan() {{
                        setEncounterId(new Long(encounter.getId()));
                        setPlanSetupId(planSummaryDto.getPlanSetupId());
                        setPlanCreatedDt(planSummaryDto.getCreatedDt());
                    }});
                });
//              需要将之前添加到结束治疗的计划过滤掉
                PatientEncounterEndPlan encounterEndPlan = PatientEncounterHelper.getEncounterEndPlanByPatientSer(String.valueOf(patientSer));
                if (encounterEndPlan.getCompletedPlan() != null && !encounterEndPlan.getCompletedPlan().isEmpty()) {
                    Iterator<EncounterEndPlan> it = list.iterator();
                    while (it.hasNext()) {
                        EncounterEndPlan tmp = it.next();
                        for (EncounterEndPlan e : encounterEndPlan.getCompletedPlan()) {
                            if (tmp.getPlanSetupId().equals(e.getPlanSetupId())
                                    && tmp.getPlanCreatedDt().equals(e.getPlanCreatedDt())) {
                                it.remove();
                                break;
                            }
                        }
                    }
                }
                if (!list.isEmpty()) {
                    boolean ok = encounterServiceImp.createEncounterEndPlan(list);
                    if (ok) {
                        PatientEncounterHelper.syncEncounterEndPlanByPatientSer(patientSer.toString());
                    }
                }
            }
            PatientEncounterHelper.syncEncounterCarePathByPatientSer(String.valueOf(patientSer));
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (appointmentStoredToLocal) {
                encounterServiceImp.cancelLocalTreatmentAppointment(patientSer, encounter.getId());
            }
            encounterServiceImp.cancelQueuingManagement(patientSer, encounter.getId());
        }
    }


    /**
     * CarePath 切换后，需要将未治疗的appointment cancel掉
     *
     * @param userContext user context
     * @param patientSer patient ser
     */
    protected void switchCarePathEncounterInLocal(UserContext userContext, Long patientSer) {
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        if (encounter != null) {
            boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
            if (appointmentStoredToLocal) {
                encounterServiceImp.cancelLocalTreatmentAppointment(patientSer, encounter.getId());
            }
            encounterServiceImp.cancelQueuingManagement(patientSer, encounter.getId());
        }
    }

    /**
     * Un-mark active status of a patient.
     *
     * @param configuration configuration
     * @param patientSer    patient ser
     */
    protected void unmarkActiveStatus(Configuration configuration, Long patientSer) {
        String activeStatusCode = StatusIconPool.get(configuration.getActiveStatusIconDesc());
        if (isEmpty(activeStatusCode)) {
            log.error("Can't get the active status code, please check the active config.");
            return;
        }
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(patientSer.toString(), activeStatusCode);
    }

    /**
     * Un-mark urgent status of patient
     *
     * @param configuration configuration
     * @param patientSer    patient ser
     */
    protected void unmarkUrgentStatus(Configuration configuration, Long patientSer) {
        String urgentCode = StatusIconPool.get(configuration.getUrgentStatusIconDesc());
        if (isEmpty(urgentCode)) {
            log.error("Can't get the urgent code, please check the urgent config.");
            return;
        }
        FlagAntiCorruptionServiceImp flagAntiCorruptionServiceImp = new FlagAntiCorruptionServiceImp();
        flagAntiCorruptionServiceImp.unmarkPatientStatusIcon(patientSer.toString(), urgentCode);
    }

    /**
     * Clear patient encounter data in ARIA.
     *
     * @param configuration configuration
     * @param patientDto    patient dto
     */
    protected void clearPatientEncounterInAria(Configuration configuration, PatientDto patientDto) {

        String alertCode = PatientLabelPool.get(configuration.getAlertPatientLabelDesc());
        if (isEmpty(alertCode)) {
            log.error("Can't get the alert code, please check the alert config.");
            return;
        }

        if (patientDto == null) {
            log.error("Can't find the patient, NOT CLEAR Patient information, please check the patient information.");
            return;
        }
        PatientDto.PatientLabel alertLabel = new PatientDto.PatientLabel();
        alertLabel.setLabelId(alertCode);
        alertLabel.setLabelTag(configuration.getAlertPatientLabelDesc());
        alertLabel.setLabelText("");
        patientDto.addPatientLabel(alertLabel);
        // Clear patient encounter information before complete treatment.
        antiCorruptionServiceImp.updatePatient(patientDto);
    }

    /**
     * Save dynamic form data
     *
     * @param userContext                     user context
     * @param patientSer                      patient ser
     * @param templateId                      dynamicform template id
     * @param carePathInstanceId              carepath instance id
     * @param existingDynamicFormRecordId     dynamicform record id if existing
     * @param dynamicFormItemsAndTemplateInfo dynamic form items and template info
     * @return response
     */
    @Path("/patient/{patientSer}/dynamicformitems/")
    @POST
    public Response saveDynamicFormItems(@Auth UserContext userContext,
                                         @PathParam("patientSer") Long patientSer,
                                         @QueryParam("templateId") String templateId,
                                         @QueryParam("carePathInstanceId") String carePathInstanceId,
                                         @QueryParam("existingDynamicFormRecordId") String existingDynamicFormRecordId,
                                         DynamicFormItemsAndTemplateInfo dynamicFormItemsAndTemplateInfo) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(userContext);

        String currentCarePathInstanceId;
        if (StringUtils.isEmpty(carePathInstanceId)) {
            CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
            List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer.toString());
            if (carePathInstanceList.isEmpty()) {
                log.error("Cannot find the carepath of this patient.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Cannot find the carepath of this patient.").build();
            }
            ///TODO 这里取最后一个carePathInstance作为最新的carePathInstance
            int lastCarePathInstanceIndex = carePathInstanceList.size() - 1;
            currentCarePathInstanceId = carePathInstanceList.get(lastCarePathInstanceIndex).getId();
        } else {
            currentCarePathInstanceId = carePathInstanceId;
        }

        String saveOrUpdateId = dynamicFormInstanceServiceImp.saveOrUpdate(
                patientSer,
                dynamicFormItemsAndTemplateInfo,
                templateId,
                currentCarePathInstanceId,
                existingDynamicFormRecordId);

        PatientDto patientDto = new DynamicFormDataMapper(dynamicFormItemsAndTemplateInfo.getRecordInfo()).getPatientInfo();
        if (patientDto != null) {
            PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
            PatientCacheService patientCacheService = new PatientCacheService();
            PatientDto patientDtoInAria = patientCacheService.queryPatientByPatientIdWithPhoto(patientSer.toString());
            try {
                updateObject(patientDtoInAria, patientDto);
            } catch (IllegalAccessException exception) {
                log.error("An error occurred when accessing a field of patientDto.");
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("An error occurred when accessing a field of patientDto.").build();
            }

            patientAntiCorruptionServiceImp.updatePatient(patientDtoInAria);
        }

        return Response.status(Response.Status.OK).entity(isNotEmpty(saveOrUpdateId)).build();
    }

    /**
     * Update object
     *
     * @param toBeUpdated
     * @param value
     * @param <T>
     * @throws IllegalAccessException
     */
    private <T> void updateObject(T toBeUpdated, T value) throws IllegalAccessException {
        for (Field field : value.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(value) != null) {
                field.set(toBeUpdated, field.get(value));
            }
        }
    }

    /**
     * Search dynamic form data.
     *
     * @param userContext        user context
     * @param patientSer         patient ser
     * @param dynamicFormItemIds dynamic form item ids
     * @return response
     */
    @Path("/patient/{patientSer}/dynamicformitems/search")
    @POST
    public Response searchDynamicFormItems(@Auth UserContext userContext,
                                           @PathParam("patientSer") Long patientSer,
                                           final List<KeyValuePair> dynamicFormItemIds) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        List<KeyValuePair> result = new ArrayList<>();
        DynamicFormInstanceServiceImp dynamicFormInstanceServiceImp = new DynamicFormInstanceServiceImp(userContext);
        DynamicFormInstance dynamicFormInstance = dynamicFormInstanceServiceImp.queryByPatientSer(patientSer);
        if (dynamicFormInstance == null) {
            dynamicFormItemIds.forEach(itemId -> result.add(new KeyValuePair(itemId.getKey(), StringUtils.EMPTY)));
        } else {
            final List<String> dynamicFormItemKeyList = new ArrayList<>();
            dynamicFormItemIds.forEach(id -> dynamicFormItemKeyList.add(id.getKey()));
            List<KeyValuePair> items = dynamicFormInstance.getDynamicFormItems();
            items.forEach(item -> {
                if (dynamicFormItemKeyList.contains(item.getKey())) {
                    result.add(new KeyValuePair(item.getKey(), item.getValue()));
                    dynamicFormItemKeyList.remove(item.getKey());
                }
            });
            if (!dynamicFormItemKeyList.isEmpty()) {
                dynamicFormItemKeyList.forEach(id -> result.add(new KeyValuePair(id, StringUtils.EMPTY)));
            }
        }
        return Response.status(Response.Status.OK).entity(result).build();
    }

    /**
     * Save target volumes
     *
     * @param userContext         user context
     * @param targetVolumeGroupVO target volume data
     * @return response
     */
    @Path("/patient/targetVolume/save")
    @POST
    public Response saveTargetVolume(@Auth UserContext userContext, TargetVolumeGroupVO targetVolumeGroupVO) {
        TargetVolumeServiceImp targetVolumeServiceImp = new TargetVolumeServiceImp(userContext);
        if (StringUtils.isEmpty(targetVolumeGroupVO.getEncounterId())) {
            EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
            Encounter encounter = encounterServiceImp.queryByPatientSer(targetVolumeGroupVO.getPatientSer());
            targetVolumeGroupVO.setEncounterId(encounter.getId());
        }
        boolean saveOrUpdate = targetVolumeServiceImp.saveTargetVolume(targetVolumeGroupVO);
        return Response.status(Response.Status.OK).entity(saveOrUpdate).build();
    }

    /**
     * Search target volumes
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/targetVolume/search")
    @GET
    public Response searchTargetVolume(
            @Auth UserContext userContext,
            @PathParam("patientSer") Long patientSer,
            @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
//          get current encounterId
            encounterId = getCurrentEncounter(patientSer);
        }
        TargetVolumeServiceImp targetVolumeServiceImp = new TargetVolumeServiceImp(userContext);
        TargetVolumeGroupVO targetVolumeGroupVO = targetVolumeServiceImp.queryTargetVolumeGroupByPatientSer(patientSer, encounterId);
        return Response.ok().entity(targetVolumeGroupVO).build();
    }

    /**
     * Search all target volume names
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/allTargetVolumeName/search")
    @GET
    public Response searchAllTargetVolumeName(
            @Auth UserContext userContext,
            @PathParam("patientSer") Long patientSer,
            @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
//          get current encounterId
            encounterId = getCurrentEncounter(patientSer);
        }
        TargetVolumeServiceImp targetVolumeServiceImp = new TargetVolumeServiceImp(userContext);
        TargetVolumeGroupVO targetVolumeGroupVO = targetVolumeServiceImp
                .queryTargetVolumeGroupOnlyTargetVolumeExceptItemByPatientSer(patientSer, encounterId);
        List<TargetVolumeVO> list = targetVolumeGroupVO.getTargetVolumeList();
        List<KeyValuePair> rlist = new ArrayList<>();
        list.forEach(targetVolumeVO -> {
            rlist.add(new KeyValuePair("name", targetVolumeVO.getName()));
        });
        return Response.ok(rlist).build();
    }

    /**
     * Get current encounter of a patient
     * @param patientSer patient ser
     * @return current encounter id
     */
    private Long getCurrentEncounter(Long patientSer) {
        Long encounterId = -1L;
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(new UserContext());
        Encounter encounter = encounterServiceImp.queryByPatientSer(patientSer);
        if (encounter != null) {
            encounterId = new Long(encounter.getId());
        }
        return encounterId;
    }


    /**
     * Save planned target volumes
     *
     * @param userContext        user context
     * @param planTargetVolumeVO planned target volume data
     * @return response
     */
    @Path("/patient/planTargetVolume/save")
    @POST
    public Response savePlanTargetVolume(@Auth UserContext userContext, PlanTargetVolumeVO planTargetVolumeVO) {
        PlanTargetVolumeServiceImp planTargetVolumeServiceImp = new PlanTargetVolumeServiceImp(userContext);
        if (planTargetVolumeVO.getPlanTargetVolumeList() == null
                || planTargetVolumeVO.getPlanTargetVolumeList().isEmpty()
                || StringUtils.isEmpty(planTargetVolumeVO.getPatientSer())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(planTargetVolumeVO).build();
        }
        boolean ok = planTargetVolumeServiceImp.savePlanTargetVolumeName(planTargetVolumeVO);
        return Response.ok(ok).build();
    }

    /**
     * Search planned target volumes
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/planTargetVolume/search")
    @GET
    public Response searchPlanTargetVolume(
            @Auth UserContext userContext,
            @PathParam("patientSer") Long patientSer,
            @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
            encounterId = this.getCurrentEncounter(patientSer);
        }
        PlanTargetVolumeServiceImp planTargetVolumeServiceImp = new PlanTargetVolumeServiceImp(userContext);
        PlanTargetVolumeVO targetVolumeVO = planTargetVolumeServiceImp.queryPlanTargetVolumeMappingByPatientSer(patientSer, encounterId);
        return Response.ok(targetVolumeVO).build();
    }

    /**
     * Save iso center data
     *
     * @param userContext        user context
     * @param patientSer         patient ser
     * @param isoPlanTrementList iso center data
     * @return response
     */
    @Path("/patient/{patientSer}/isocenter/save")
    @POST
    public Response saveISOCenter(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer, List<ISOPlanTretment> isoPlanTrementList) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        ISOCenterServiceImp isoCenterServiceImp = new ISOCenterServiceImp(userContext);
        ISOCenter isoCenter = new ISOCenter();
        isoCenter.setHisId(null);
        isoCenter.setPatientSer(patientSer);
        isoCenter.setPlanList(isoPlanTrementList);
        isoCenter.setEncounterId(String.valueOf(this.getCurrentEncounter(patientSer)));
        String saveOrUpdateId = isoCenterServiceImp.saveOrUpdateISOCenter(isoCenter);
        return Response.ok().entity(isNotEmpty(saveOrUpdateId)).build();
    }

    /**
     * Search iso center data
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/isocenter/search")
    @GET
    public Response searchISOCenter(
            @Auth UserContext userContext,
            @PathParam("patientSer") Long patientSer,
            @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
            encounterId = this.getCurrentEncounter(patientSer);
        }
        ISOCenterServiceImp isoCenterServiceImp = new ISOCenterServiceImp(userContext);
        List<ISOPlanTretment> ptList = isoCenterServiceImp.queryPlanTreatmentByPatientSer(patientSer, encounterId);
        return Response.ok().entity(ptList).build();
    }

    /**
     * Search confirm payments
     *
     * @param userContext  user context
     * @param patientSerList   patientSerList
     * @param activityCode activity code
     * @return response
     */
    @Path("/encounter/confirmpayment/search")
    @POST
    public Response searchConfirmPayment(@Auth UserContext userContext,List<KeyValuePair> patientSerList,
                                         @QueryParam("activityCode") String activityCode) {
        if (patientSerList == null || patientSerList.isEmpty()) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSerList);
        }
        ConfirmPaymentServiceImp confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
        Set<String> set = new HashSet<>();
        patientSerList.forEach(keyValuePair -> set.add(keyValuePair.getKey()));
        List<String> patientSearchList = new ArrayList<>();
        patientSearchList.addAll(set);
        List<ConfirmPayment> confirmPaymentList = confirmPaymentServiceImp.queryConfirmPaymentListByPatientSerList(patientSearchList);
        Map<String,ConfirmPayment> map = new HashMap<>();
        if (confirmPaymentList != null) {
            List<ConfirmPayment> list = new ArrayList<>();
            confirmPaymentList.forEach(confirmPayment -> map.put(String.valueOf(confirmPayment.getPatientSer()),confirmPayment));
            patientSearchList.forEach(patientSer -> {
                ConfirmPayment confirmPayment = map.get(patientSer);
                if(confirmPayment == null){
                    confirmPayment = confirmPaymentServiceImp.queryInitConfirmPayment(patientSer);
                    list.add(confirmPayment);
                    map.put(patientSer,confirmPayment);
                }else{
                    confirmPaymentServiceImp.queryMultiCarePathConfirmPayment(confirmPayment);
                }
            });
            confirmPaymentList.addAll(list);
        }
        List<ConfirmPayment> sortList = new ArrayList<>();
        patientSerList.forEach(keyValuePair->{
            sortList.add(map.get(keyValuePair.getKey()));
        });
        confirmPaymentList = sortList;
        if (StringUtils.isEmpty(activityCode)) {
            return Response.ok(confirmPaymentList).build();
        } else {
            List<KeyValuePair> keyValuePairs = new ArrayList<>();
            confirmPaymentList.forEach(confirmPayment -> {
                keyValuePairs.add(new KeyValuePair(String.valueOf(confirmPayment.getPatientSer()),String.valueOf(confirmPaymentServiceImp.containConfirmPayment(confirmPayment, activityCode))));
            });
            return Response.ok(keyValuePairs).build();
        }
    }

    /**
     * Save confirm payments
     *
     * @param userContext    user context
     * @param patientSer     patient ser
     * @param confirmPayment confirm payment data
     * @return response
     */
    @Path("/patient/{patientSer}/confirmpayment/save")
    @POST
    public Response saveConfirmPayment(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer, ConfirmPayment confirmPayment) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        ConfirmPaymentServiceImp confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
        confirmPayment.setPatientSer(patientSer);
        String primaryKey = confirmPaymentServiceImp.saveOrUpdateConfirmPayment(confirmPayment);
        return Response.ok().entity(Long.parseLong(primaryKey) > 0).build();
    }

    /**
     * Search treatment workload data
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/treatmentworkload/search")
    @GET
    public Response searchTreatmentWorkload(
            @Auth UserContext userContext,
            @PathParam("patientSer") Long patientSer,
            @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
            encounterId = this.getCurrentEncounter(patientSer);
        }
        TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        TreatmentWorkloadVO treatmentWorkload = treatmentWorkloadServiceImp
                .queryTreatmentWorkloadByPatientSer(patientSer, encounterId);
        return Response.ok(treatmentWorkload).build();
    }

    /**
     * Save treatment workload data
     *
     * @param userContext         user context
     * @param treatmentWorkloadVO treatment workload data
     * @return response
     */
    @Path("/patient/treatmentworkload/save")
    @POST
    public Response saveTreatmentWorkload(@Auth UserContext userContext, TreatmentWorkloadVO treatmentWorkloadVO) {
        if (treatmentWorkloadVO.getPlanList() == null || treatmentWorkloadVO.getPlanList().isEmpty()) {
            return Response.ok(true).build();
        }
        TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        treatmentWorkloadVO.setTreatmentDate(DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_FORMAT));
        return Response.ok(treatmentWorkloadServiceImp.createTreatmentWorkLoad(treatmentWorkloadVO)).build();
    }

    /**
     * Search history of treatment workload
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterId encounter id
     * @return response
     */
    @Path("/patient/{patientSer}/treatmentworkload/history/search")
    @GET
    public Response searchTreatmentWorkloadHistory(@Auth UserContext userContext, @PathParam("patientSer") Long patientSer, @QueryParam("encounterId") Long encounterId) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
            encounterId = this.getCurrentEncounter(patientSer);
        }
        TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        List<TreatmentWorkloadVO> list = treatmentWorkloadServiceImp.queryTreatmentWorkloadListByPatientSer(patientSer, encounterId);
        return Response.ok(list).build();
    }

    /**
     * Save allergy information.
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @param encounterVO encounter data
     * @return response
     */
    @Path("/encounter/allergyinfo/{patientSer}")
    @POST
    public Response modifyAllergyInfo(@Auth UserContext userContext,
                                      @PathParam("patientSer") Long patientSer,
                                      EncounterVO encounterVO) {
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        boolean ok = encounterServiceImp.modifyAllergyInfo(patientSer, encounterVO.getAllergyInfo());
        return Response.ok(ok).build();
    }

    /**
     * Search history of encounter title
     *
     * @param userContext user context
     * @param patientSer  patient ser
     * @return response
     */
    @Path("/encounter/history/list")
    @GET
    public Response historyList(@Auth UserContext userContext, @QueryParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        EncounterServiceImp service = new EncounterServiceImp(userContext);
        List<EncounterTitleItem> titleList = service.listHistory(patientSer);
        return Response.ok(titleList).build();
    }

    /**
     * Search detail of an encounter
     *
     * @param userContext user context
     * @param encounterId encounter id
     * @param patientSer  patient ser
     * @return response
     */
    @Path("/encounter/history/detail")
    @GET
    public Response queryEncounterById(@Auth UserContext userContext, @QueryParam("encounterId") Long encounterId,
                                       @QueryParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        if (encounterId == null) {
            log.warn("Parameter encounterId must not be null");
            return build400Response(patientSer);
        }
        EncounterServiceImp encounterServiceImp = new EncounterServiceImp(userContext);
        Encounter encounter = encounterServiceImp.queryEncounterByIdAndPatientSer(encounterId, patientSer);
        return Response.ok(encounter).build();
    }

    /**
     * Link to a new carepath.<br>
     *
     * @param patientSer         Patient Id
     * @param carepathTemplateId Care Path Template Id
     * @return new CarePath Instance Id
     */
    protected String linkCarepath(Long patientSer, String carepathTemplateId) {
        CarePathAntiCorruptionServiceImp service = new CarePathAntiCorruptionServiceImp();
        final String defaultDepartment = SystemConfigPool.queryDefaultDepartment();
        return service.linkCarePath(patientSer.toString(), defaultDepartment, carepathTemplateId);
    }

    /**
     * Update new carepath in encounter.
     *
     * @param context            user context
     * @param patientSer         patient ser
     * @param carePathInstanceId care path instance id
     * @param category           category of care path
     * @return true/false
     */
    private boolean updateEncounterWithNewCarePath(
            UserContext context,
            Long patientSer,
            String carePathInstanceId,
            EncounterCarePath.EncounterCarePathCategoryEnum category) {
        EncounterServiceImp encounterService = new EncounterServiceImp(context);
        EncounterCarePath encounterCarePath = new EncounterCarePath() {{
            setCpInstanceId(new Long(carePathInstanceId));
            setCategory(category);
        }};
        return encounterService.updateCarePathInstanceId(patientSer, encounterCarePath);
    }
}
