package com.varian.oiscn.anticorruption.fhirinterface;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.util.DateUtils;
import com.varian.fhir.resources.ActivityDefinition;
import com.varian.fhir.resources.Appointment;
import com.varian.fhir.resources.CarePath;
import com.varian.fhir.resources.Task;
import com.varian.oiscn.anticorruption.converter.DataHelper;
import com.varian.oiscn.anticorruption.fhircontext.FHIRContextFactory;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 * Created by fmk9441 on 2017-04-18.
 */
@Slf4j
public class FHIRCarePathInterface extends FHIRInterface <CarePath> {
    /**
     * Return Fhir CarePath from Template Name.<br>
     *
     * @param templateName Template Name
     * @return Fhir CarePath
     */
    public CarePath queryCarePathByTemplateName(String templateName) {
        CarePath carePath = null;
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();

        try {
            long time1 = System.currentTimeMillis();
            Bundle bundle = client.search()
                    .forResource(CarePath.class)
                    .where(new StringClientParam(CarePath.SP_TEMPLATE).matchesExactly().value(templateName))
                    .preferResponseTypes(Arrays.asList(CarePath.class, ActivityDefinition.class))
                    .returnBundle(Bundle.class)
                    .execute();
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - CarePathResource - QueryBy[sp_template] : {}", (time2 - time1) / 1000.0);
            if (bundle != null && bundle.hasEntry() && bundle.getEntryFirstRep().hasResource()) {
                carePath = (CarePath) bundle.getEntryFirstRep().getResource();
            }
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }

        return carePath;
    }

    /**
     * Return Fhir CarePath from Patient Id.<br>
     *     get current activity CarePath
     * @param patientId Patient Id
     * @return Fhir CarePath
     */
    public CarePath queryLastCarePathByPatientID(String patientId) {
        CarePath lastestCarePath = null;
        List<CarePath> carePathList = queryAllCarePathByPatientID(patientId);
        if(carePathList != null && !carePathList.isEmpty()){
            if(carePathList.size() > 1) {
                for (CarePath carePath : carePathList) {
                    PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(DataHelper.getReferenceValue(carePath.getPatient().getId()));
                    if (patientEncounterCarePath != null && patientEncounterCarePath.getPlannedCarePath() != null) {
                        String carePathInstanceId = patientEncounterCarePath.getPlannedCarePath().getMasterCarePathInstanceId() + "";
                        if (carePath.getIdElement().getIdPart().equals(carePathInstanceId)) {
                            lastestCarePath = carePath;
                            break;
                        }
                    }
                }
            }else{
                lastestCarePath = carePathList.get(0);
            }
        }
        return lastestCarePath;
    }

    private List<CarePath> queryAllCarePathListByPatientIDList(List<String> lstPatientID) {
        List<CarePath> allList = new ArrayList<>();
        Pagination<CarePath> pagination;
        try {
            int countPerPage = Integer.MAX_VALUE;
            long time1 = System.currentTimeMillis();
            log.debug("FHIR - CarePathResource - QueryBy[sp_patient_list] time1 : {}", DateUtils.formatDate(new Date(time1), "yyyy-MM-dd HH:mm:ss"));
            pagination = queryCarePathPaginationList(lstPatientID, countPerPage);
            if (pagination != null && pagination.getLstObject().size() > 0) {
                allList.addAll(pagination.getLstObject());
            }
            long time2 = System.currentTimeMillis();
            log.debug("FHIR - CarePathResource - QueryBy[sp_patient_list] time2 : {}" + DateUtils.formatDate(new Date(time2), "yyyy-MM-dd HH:mm:ss"));
            log.debug("FHIR - CarePathResource - QueryBy[sp_patient_list] : {}", (time2 - time1) / 1000.0);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
        return allList;
    }


    /**
     * Return Fhir CarePath List from Patient Id List.<br>
     * get current activity carePath
     * @param lstPatientID Patient Id List
     * @return Fhir CarePath List
     */
    public List<CarePath> queryCarePathListByPatientIDList(List<String> lstPatientID) {
        List<CarePath> lstCarePath = new ArrayList<>();
        List<CarePath> allList = queryAllCarePathListByPatientIDList(lstPatientID);
        allList.forEach(carePath -> {
            List<CarePath.PlannedActivity> plannedActivityList = carePath.getPlannedActivities();
            PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(DataHelper.getReferenceValue(carePath.getPatient().getId()));
            if (patientEncounterCarePath != null && patientEncounterCarePath.getPlannedCarePath() != null) {
                String carePathInstanceId = patientEncounterCarePath.getPlannedCarePath().getMasterCarePathInstanceId() + "";
                if(carePath.getIdElement().getIdPart().equals(carePathInstanceId)) {
                    lstCarePath.add(carePath);
                }
            } else {
                for (CarePath.PlannedActivity activity : plannedActivityList) {
                    if (activity.getStatus() != null && !(activity.getStatus().equals(CarePath.CarePathStatus.CANCELLED) || activity.getStatus().equals(CarePath.CarePathStatus.COMPLETED))) {
                        lstCarePath.add(carePath);
                        break;
                    }
                }
            }
        });
        return lstCarePath;
    }

    private Pagination<CarePath> queryCarePathPaginationList(List<String> lstPatientID,int countPerPage) {
        Pagination<CarePath> pagination = new Pagination<>();
        if (!lstPatientID.isEmpty()) {
            IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
            IQuery<IBaseBundle> iQuery = client.search().forResource(CarePath.class);
            iQuery = iQuery.where(new StringClientParam(CarePath.SP_PATIENT_LIST).matchesExactly().values(lstPatientID));
            Bundle bundle = queryPagingBundle(iQuery, Arrays.asList(CarePath.class, ActivityDefinition.class),countPerPage);
            if (bundle != null) {
                List<Bundle.BundleEntryComponent> bundleEntryComponentList = PaginationHelper.queryPagingBundle(client, bundle, 1, Integer.MAX_VALUE, Arrays.asList(CarePath.class, ActivityDefinition.class));
                List<CarePath> carePathList = getListFromBundleEntryComponent(bundleEntryComponentList);
                pagination.setLstObject(carePathList);
                pagination.setTotalCount(bundle.getTotal());
            }
        }
        return pagination;
    }


    /**
     * 根据patintId查询所有的CarePath
     * @param patientID
     * @return
     */
    public List<CarePath> queryAllCarePathByPatientID(String patientID) {
        return queryAllCarePathListByPatientIDList(Arrays.asList(patientID));
    }
    /**
     * 根据patintIdList查询所有的CarePath
     * @param patientIdList
     * @return
     */
    public List<CarePath> queryAllCarePathByPatientIDList(List<String> patientIdList) {
        return queryAllCarePathListByPatientIDList(patientIdList);
    }
    /**
     * Schedule Next Fhir Task by Fhir CarePath Id, and activity Id, and Fhir Task.<br>
     * @param carePathID CarePath Id
     * @param activityInstanceID Activity Id
     * @param task Fhir Task
     * @return new Task Id
     */
    public String scheduleNextTask(String carePathID, String activityInstanceID, Task task) {
        IParser parser = FHIRContextFactory.getInstance().getXmlParser();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        long time1 = System.currentTimeMillis();
        Parameters inParams = new Parameters();
        inParams.addParameter().setName("activityId").setValue(new IdType(activityInstanceID));
        inParams.addParameter().setName("task").setValue(new StringType(parser.encodeResourceToString(task)));
        Parameters outParams = client
                .operation()
                .onInstance(new IdDt("CarePath", carePathID))
                .named("$scheduleTask")
                .withParameters(inParams)
                .execute();
        long time2 = System.currentTimeMillis();
        log.debug("FHIR - CarePathResource - ScheduleNextTask[{}] : {}", activityInstanceID, (time2 - time1) / 1000.0);
        return getId(outParams);
    }

    /**
     * Schedule the Next Fhir Appointment and return new Appointment Id.<br>
     * @param carePathID Fhir CarePath Id
     * @param activityInstanceID Activity Id
     * @param appointment Fhir Appointment
     * @return new Fhir Appointment Id
     */
    public String scheduleNextAppointment(String carePathID, String activityInstanceID, Appointment appointment) {
        IParser parser = FHIRContextFactory.getInstance().getXmlParser();
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        long time1 = System.currentTimeMillis();
        Parameters inParams = new Parameters();
        inParams.addParameter().setName("activityId").setValue(new IdType(activityInstanceID));
        inParams.addParameter().setName("appointment").setValue(new StringType(parser.encodeResourceToString(appointment)));
        Parameters outParams = client
                .operation()
                .onInstance(new IdDt("CarePath", carePathID))
                .named("$scheduleAppointment")
                .withParameters(inParams)
                .execute();
        long time2 = System.currentTimeMillis();
        log.debug("FHIR - CarePathResource - ScheduleNextAppointment[{}] : {}", activityInstanceID, (time2 - time1) / 1000.0);
        return getId(outParams);
    }

    private String getId(Parameters outParams) {
        if (outParams != null && outParams.getParameter() != null) {
            for (Parameters.ParametersParameterComponent param : outParams.getParameter()) {
                if (StringUtils.equalsIgnoreCase("Id", param.getName())) {
                    return param.getValue().toString();
                }
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Assign a carepath to a patient
     * @param departmentID the department ser
     * @param carePathTemplateID the carepath template ser
     * @param patientID the patient ser
     */
    public void assignCarePath(String departmentID, String carePathTemplateID, String patientID) {
        Parameters inputParams = new Parameters();
        inputParams.addParameter().setName("carePathId").setValue(new StringType(carePathTemplateID));
        inputParams.addParameter().setName("departmentSer").setValue(new StringType(departmentID));
        IGenericClient client = FHIRContextFactory.getInstance().newRestfulGenericClient();
        client.operation()
                .onInstance(new IdType(ResourceType.Patient.name(), patientID))
                .named("$assignCarePath")
                .withParameters(inputParams)
                .execute();
    }
}