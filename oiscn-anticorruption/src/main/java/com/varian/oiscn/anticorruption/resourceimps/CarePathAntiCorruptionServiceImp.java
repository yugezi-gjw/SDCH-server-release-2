package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.CarePath;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.assembler.AppointmentAssembler;
import com.varian.oiscn.anticorruption.assembler.CarePathAssembler;
import com.varian.oiscn.anticorruption.assembler.TaskAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCarePathInterface;
import com.varian.oiscn.cache.AppointmentCache;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.order.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by fmk9441 on 2017-04-18.
 */
@Slf4j
public class CarePathAntiCorruptionServiceImp {
    private FHIRCarePathInterface fhirCarePathInterface;

    /**
     * Default Constructor.<br>
     */
    public CarePathAntiCorruptionServiceImp() {
        fhirCarePathInterface = new FHIRCarePathInterface();
    }

    /**
     * Return CarePath Template By Template Name.<br>
     *
     * @param templateName Template Name
     * @return CarePath Template
     */
    public CarePathTemplate queryCarePathByTemplateName(String templateName) {
        CarePathTemplate carePathTemplate = null;
        CarePath carePath = fhirCarePathInterface.queryCarePathByTemplateName(templateName);
        if (null != carePath) {
            carePathTemplate = CarePathAssembler.getCPTemplate(carePath);
        }

        return carePathTemplate;
    }

    /**
     * Return CarePath Instance By Patient Id.<br>
     * @param patientID Patient Id
     * @return CarePath Instance
     */
    public CarePathInstance queryLastCarePathByPatientID(String patientID) {
        CarePathInstance carePathInstance = null;
        CarePath carePath = fhirCarePathInterface.queryLastCarePathByPatientID(patientID);
        if (null != carePath) {
            carePathInstance = CarePathAssembler.getCPInstance(carePath);
        }

        return carePathInstance;
    }

    /**
     * Return CarePath Instance By Patient Id.<br>
     * @param patientID Patient Id
     * @return CarePath Instance
     */
    public CarePathInstance queryCarePathByPatientIDAndActivityInstanceIdAndActivityType(String patientID,String instanceId,ActivityTypeEnum actityType) {
        CarePathInstance carePathInstance = null;
        List<CarePath>  carePathList = fhirCarePathInterface.queryAllCarePathByPatientID(patientID);
        if (carePathList != null && !carePathList.isEmpty()) {
            for(CarePath carePath : carePathList){
                carePathInstance = CarePathAssembler.getCPInstance(carePath);
               List<ActivityInstance> activityInstanceList =  carePathInstance.getOriginalActivityInstances();
               if(activityInstanceList != null && !activityInstanceList.isEmpty()){
                   Optional<ActivityInstance> optional = activityInstanceList.stream().filter(activityInstance -> StringUtils.isNotEmpty(activityInstance.getInstanceID()) && activityInstance.getInstanceID().equals(instanceId) && activityInstance.getActivityType().equals(actityType)).findFirst();
                   if(optional.isPresent()){
                       return carePathInstance;
                   }
               }
            }
        }
        return carePathInstance;
    }

    private List<CarePathInstance> queryCarePathListByPatientIDAndActivityCode(String patientID,String activityCode) {
        List<CarePathInstance> carePathInstanceList = new ArrayList<>();
        List<CarePath>  carePathList = fhirCarePathInterface.queryAllCarePathByPatientID(patientID);
        if (carePathList != null && !carePathList.isEmpty()) {
            for(CarePath carePath : carePathList){
                CarePathInstance carePathInstance = CarePathAssembler.getCPInstance(carePath);
                List<ActivityInstance> activityInstanceList =  carePathInstance.getOriginalActivityInstances();
                if(activityInstanceList != null && !activityInstanceList.isEmpty()){
                    Optional<ActivityInstance> optional = activityInstanceList.stream().filter(activityInstance -> activityInstance.getActivityCode().equals(activityCode) && (activityInstance.getPrevActivities() != null || activityInstance.getNextActivities() != null)).findFirst();
                    if(optional.isPresent()){
                        carePathInstanceList.add(carePathInstance);
                    }
                }
            }
        }
        return carePathInstanceList;
    }

    /**
     * 根据patientId和activityCode获取最新的CarePathInstance
     * @param patientId
     * @param activityCode
     * @return
     */
    public CarePathInstance queryLastCarePathByPatientIDAndActivityCode(String patientId,String activityCode){
        CarePathInstance carePathInstance = null;
        List<CarePathInstance> carePathInstanceList = queryCarePathListByPatientIDAndActivityCode(patientId,activityCode);
        carePathInstance = carePathInstanceList.stream().max((o1, o2) -> (int)(Long.parseLong(o1.getId()) - Long.parseLong(o2.getId()))).get();
        return carePathInstance;
    }


    /**
     * Return CarePath Instance By Patient Id.<br>
     * @param patientID Patient Id
     * @return CarePath Instance
     */
    public List<CarePathInstance> queryAllCarePathByPatientID(String patientID) {
        List<CarePathInstance> carePathInstanceList = new ArrayList<>();
        List<CarePath> carePathList = fhirCarePathInterface.queryAllCarePathByPatientID(patientID);
        carePathList.forEach(carePath -> carePathInstanceList.add(CarePathAssembler.getCPInstance(carePath)));
        return carePathInstanceList;
    }


    /**
     * Return CarePath Instance List By Patient Id List.
     * return master carepath<br>
     * @param lstPatientID Patient Id List
     * @return CarePath Instance List
     */
    public Map<String, List<CarePathInstance>> queryCarePathListByPatientIDList(List<String> lstPatientID) {
        Map<String, List<CarePathInstance>> carePathInstanceHashMap = new HashMap<>();
        List<CarePath> lstCarePath = fhirCarePathInterface.queryAllCarePathByPatientIDList(lstPatientID);
        if (!lstCarePath.isEmpty()) {
            lstCarePath.forEach(carePath -> {
                CarePathInstance carePathInstance = CarePathAssembler.getCPInstance(carePath);
                List<CarePathInstance> list = carePathInstanceHashMap.get(carePathInstance.getPatientID());
                if(list == null){
                    list = new ArrayList<>();
                    carePathInstanceHashMap.put(carePathInstance.getPatientID(),list);
                }
                list.add(carePathInstance);
            });
        }
        return carePathInstanceHashMap;
    }

    /**
     * Schedule Next Task by CarePath Id, Activity Id, Order DTO.<br>
     * @param carePathID CarePath Id
     * @param activityID Activity Id
     * @param orderDto Order DTO
     * @return new Task Id
     */
    public String scheduleNextTask(String carePathID, String activityID, OrderDto orderDto) {
        Task task = TaskAssembler.getTask(orderDto);
        return fhirCarePathInterface.scheduleNextTask(carePathID, activityID, task);
    }

    /**
     * Schedule Next Appointment by CarePath Id, Activity Id, Appointment DTO.<br>
     * @param carePathID CarePath Id
     * @param activityID Activity Id
     * @param appointmentDto Appointment DTO
     * @return new Appointment Id
     */
    public String scheduleNextAppointment(String carePathID, String activityID, AppointmentDto appointmentDto) {
        Appointment appointment = AppointmentAssembler.getAppointment(appointmentDto);
        String appointmentId = fhirCarePathInterface.scheduleNextAppointment(carePathID, activityID, appointment);
        if (isNotEmpty(appointmentId)) {
            appointmentDto.setAppointmentId(appointmentId);
            AppointmentCache.put(appointmentDto);
        }
        return appointmentId;
    }

    /**
     * Assign a carepath to a patient
     * @param departmentID the department ser
     * @param carePathID the carepath template ser
     * @param patientID the patient ser
     * @return true/false
     */
    @Deprecated
    public boolean assignCarePath(String departmentID, String carePathID, String patientID) {
        try {
            fhirCarePathInterface.assignCarePath(departmentID, carePathID, patientID);
            return true;
        } catch (Exception e) {
            log.error("assignCarePath Exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Link to a new CarePath and return new CarePath instance id.<br>
     *
     * @param patientId          Patient Id
     * @param departmentId       Department Id
     * @param carePathTemplateId CarePath Template Id
     * @return
     */
    public String linkCarePath(String patientId, String departmentId, String carePathTemplateId) {
        String carePathInstanceId = StringUtils.EMPTY;
        try {
            // get real carepath template if from TEMPLATE ID in Aria
            CarePath cpTemplate = fhirCarePathInterface.queryCarePathByTemplateName(carePathTemplateId);
            String carePathTemplateRealId = cpTemplate.getIdElement().getIdPart();
            fhirCarePathInterface.assignCarePath(departmentId, carePathTemplateRealId, patientId);
            // FIXME: should remove when assign returns a new Instance Id.
            List<CarePath> carePathList = fhirCarePathInterface.queryAllCarePathByPatientID(patientId);
            long maxId = 0;
            CarePath lastestCarePath = null;
            for (CarePath carePath : carePathList) {
                long id = carePath.getIdElement().getIdPartAsLong();
                if (id > maxId) {
                    maxId = id;
                    lastestCarePath = carePath;
                }
            }
            if (lastestCarePath != null) {
                carePathInstanceId = lastestCarePath.getIdElement().getIdPart();
            }
        } catch (Exception e) {
            log.error("linkCarePath Exception: {}", e.getMessage());
        }
        return carePathInstanceId;
    }

    public CarePathInstance queryCarePathInstanceByInstanceId(String instanceId){
        CarePath carePath = fhirCarePathInterface.queryById(instanceId,CarePath.class);
        CarePathInstance cpi = CarePathAssembler.getCPInstance(carePath);
        return cpi;
    }
}