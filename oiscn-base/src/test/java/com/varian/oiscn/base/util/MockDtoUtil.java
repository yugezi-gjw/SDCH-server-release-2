package com.varian.oiscn.base.util;

import com.varian.oiscn.core.activity.ActivityCodeEnum;
import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.carepath.*;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.order.OrderDto;
import com.varian.oiscn.core.order.OrderStatusEnum;
import com.varian.oiscn.core.participant.ParticipantDto;
import com.varian.oiscn.core.participant.ParticipantTypeEnum;
import com.varian.oiscn.core.patient.Diagnosis;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.OspLogin;
import com.varian.oiscn.core.user.UserContext;

import java.util.*;

/**
 * Created by gbt1220 on 5/26/2017.
 * Modified by bhp9696 on 7/6/2017.
 */
public class MockDtoUtil {
    private static final String DEFAULT_GROUPID = "10013";
    private static final String SYSTEM = "system";

    private MockDtoUtil() {
    }

    public static UserContext givenUserContext() {
        return new UserContext(givenALogin(), givenAnOspLogin());
    }

    public static Login givenALogin() {
        Login login = new Login();
        login.setGroup("group");
        login.setName("name");
        login.setResourceSer(1L);
        login.setToken("token");
        login.setUsername("username");
        return login;
    }

    public static OspLogin givenOspLogin() {
        OspLogin ospLogin = new OspLogin();
        ospLogin.setToken("token");
        ospLogin.setUserCUID("cuid");
        ospLogin.setName("name");
        ospLogin.setDisplayName("displayName");
        ospLogin.setUsername("username");
        return ospLogin;
    }

    public static OspLogin givenAnOspLogin() {
        OspLogin ospLogin = new OspLogin();
        ospLogin.setName("name");
        ospLogin.setUsername("username");
        ospLogin.setDisplayName("displayName");
        ospLogin.setUserCUID("cuid");
        ospLogin.setToken("token");
        ospLogin.setLastModifiedDt(new Date());
        return ospLogin;
    }

    public static PatientDto givenAPatient() {
        PatientDto patientDto = new PatientDto();
        patientDto.setAriaId("11122");
        patientDto.setHisId("hisId");
        patientDto.setNationalId("nationId");
        patientDto.setChineseName("chineseName");
        patientDto.setEnglishName("englishName");
        patientDto.setGender("gender");
        patientDto.setBirthday(new Date());
        patientDto.setContactPerson("contactPerson");
        patientDto.setContactPhone("contactPhone");
        patientDto.setPatientSer("12121");
        patientDto.setPhysicianGroupId("physicianGroupId");
        patientDto.setPhysicianId("physicianId");
        patientDto.setPhysicianName("physicianName");
        patientDto.setPhysicianPhone("physicianPhone");
        patientDto.setTelephone("telephone");
        patientDto.setAddress("address");
        patientDto.setCreatedDT(new Date());
        patientDto.setCpTemplateId("templateId");
        patientDto.setCpTemplateName("templateName");
        return patientDto;
    }

    public static RegistrationVO givenARegistrationVO() {
        RegistrationVO registrationVO = new RegistrationVO();
        registrationVO.setAriaId("ariaId");
        registrationVO.setHisId("hisId");
        registrationVO.setNationalId("nationId");
        registrationVO.setChineseName("chineseName");
        registrationVO.setEnglishName("englishName");
        registrationVO.setGender("gender");
        registrationVO.setBirthday(new Date());
        registrationVO.setContactPerson("contactPerson");
        registrationVO.setContactPhone("contactPhone");
        registrationVO.setPatientSer("patientSer");
        registrationVO.setPhysicianGroupId("physicianGroupId");
        registrationVO.setPhysicianGroupName("physicianGroupName");
        registrationVO.setPhysicianId("physicianId");
        registrationVO.setPhysicianName("physicianName");
        registrationVO.setPhysicianPhone("12345678");
        registrationVO.setTelephone("telephone");
        registrationVO.setAddress("address");
        registrationVO.setCpTemplateId("templateId");
        registrationVO.setCpTemplateName("templateName");

        registrationVO.setUrgent(true);
        registrationVO.setWarningText("warningText");
        registrationVO.setHealthSummary("healthSummary");
        registrationVO.setDiagnosisCode("code");
        registrationVO.setDiagnosisDesc("desc");
        registrationVO.setRecurrent("false");
        registrationVO.setBodypart("bodypart");
        registrationVO.setStaging("staging");
        registrationVO.setTcode("tcode");
        registrationVO.setNcode("ncode");
        registrationVO.setMcode("mcode");
        registrationVO.setDiagnosisDate(new Date());
        registrationVO.setEcogScore("1");
        registrationVO.setEcogDesc("ecogDesc");
        registrationVO.setPositiveSign("Positive");
        registrationVO.setInsuranceType("State");
        registrationVO.setPatientSource("Inpatient");
        registrationVO.setAge("60");
        return registrationVO;
    }

    public static List<PatientDto> givenAPatientList() {
        return Arrays.asList(givenAPatient(), givenAPatient());
    }

    public static OrderDto givenAnOrderDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setOrderId("1");
        orderDto.setOrderStatus(OrderStatusEnum.READY.name());
        orderDto.setCreatedDT(new Date());
        orderDto.setLastModifiedDT(new Date());
        orderDto.setOrderGroup("1");
        orderDto.setOrderType("code");
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, "1"));
        orderDto.setParticipants(participantDtos);
        return orderDto;
    }

    public static List<OrderDto> givenOrderList() {
        return Arrays.asList(givenAnOrderDto());
    }

    public static List<AppointmentDto> givenAppointmentListDto() {
        List<AppointmentDto> appointmentDtoList = new ArrayList<>();
        List<ParticipantDto> participantDtos = new ArrayList<>();
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PATIENT, "1"));
        participantDtos.add(new ParticipantDto(ParticipantTypeEnum.PRACTITIONER, "1"));
        appointmentDtoList.add(new AppointmentDto("1", "1", new Date(), new Date(), "DoCTSim", "", "", new Date(), new Date(), participantDtos));
        return appointmentDtoList;
    }

    public static Encounter givenAnEncounter() {
        Encounter encounter = new Encounter();
        encounter.setAge("10");
//        encounter.setBedNo("bed10");
        encounter.setId("1");
//        encounter.setInPatientArea("inPatientArea");
//        encounter.setOrganizationID("1");
        encounter.setPrimaryPhysicianID("1");
//        encounter.setPatientID("1");
        encounter.setPatientSer("1");
        encounter.setStatus(StatusEnum.IN_PROGRESS);
//        encounter.setPatientSourceEnum(PatientSourceEnum.E);
        encounter.setPrimaryPhysicianGroupID("1");
        encounter.setPrimaryPhysicianID("1");
        encounter.addDiagnosis(givenADiagnosis());
        return encounter;
    }

    public static Diagnosis givenADiagnosis() {
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPatientID("PatientID");
        diagnosis.setCode("Code");
        diagnosis.setDesc("Desc");
        diagnosis.setSystem(SYSTEM);
        diagnosis.setRecurrence(true);
        Diagnosis.Staging diagnosisStaging = new Diagnosis.Staging();
        diagnosisStaging.setSchemeName("SchemeName");
        diagnosisStaging.setBasisCode("BasisCode");
        diagnosisStaging.setTcode("T");
        diagnosisStaging.setNcode("N");
        diagnosisStaging.setMcode("M");
        diagnosisStaging.setDate(new Date());
        diagnosis.setStaging(diagnosisStaging);
        diagnosis.setDiagnosisDate(new Date());
        diagnosis.setBodypartCode("BodypartCode2");
        diagnosis.setBodypartDesc("BodypartDesc2");
        diagnosis.setDiagnosisNote("DiagnosisNote");
        return diagnosis;
    }

    public static CodeSystem givenCodeSystem() {
        CodeSystem codeSystem = new CodeSystem();
        codeSystem.setCode("code");
        codeSystem.setSystem(SYSTEM);
        codeSystem.addCodeValue(new CodeValue("code", "desc"));
        return codeSystem;
    }

    public static List<CodeValueDTO> givenCodeValueList() {
        List<CodeValueDTO> result = new ArrayList<>();
        CodeValueDTO dto = new CodeValueDTO();
        dto.setCode("code");
        dto.setSystem(SYSTEM);
        dto.setValue("A10");
        dto.setDesc("desc");
        result.add(dto);

        CodeValueDTO dto2 = new CodeValueDTO();
        dto2.setCode("code");
        dto2.setSystem(SYSTEM);
        dto2.setValue("A10.1");
        dto2.setDesc("desc");
        result.add(dto2);
        return result;
    }

    public static CarePathTemplate givenCarePathTemplate() {
        CarePathTemplate template = new CarePathTemplate();
        template.setId("1");
        template.setTemplateName("QinDemo");
        template.setStatus(CarePathStatusEnum.ACTIVE);
        template.setDepartmentID("1");

        PlannedActivity activity = new PlannedActivity();
        activity.setId("1");
        activity.setDepartmentID("1");
        activity.setDefaultGroupID(DEFAULT_GROUPID);
        activity.setActivityType(ActivityTypeEnum.TASK);
        activity.setActivityCode(ActivityCodeEnum.IMPORT_CT_IMAGE.name());
        activity.setPrevActivities(null);
        activity.setNextActivities(Arrays.asList("2", "3"));

        PlannedActivity activity2 = new PlannedActivity();
        activity2.setId("2");
        activity2.setDepartmentID("1");
        activity2.setDefaultGroupID(DEFAULT_GROUPID);
        activity2.setActivityType(ActivityTypeEnum.TASK);
        activity2.setActivityCode(ActivityCodeEnum.TARGET_CONTOURING.name());
        activity2.setPrevActivities(Arrays.asList("1"));
        activity2.setNextActivities(Arrays.asList("4"));
        activity2.setLagAfterPrevActivity(100);

        PlannedActivity activity3 = new PlannedActivity();
        activity3.setId("3");
        activity3.setDepartmentID("1");
        activity3.setDefaultGroupID(DEFAULT_GROUPID);
        activity3.setActivityType(ActivityTypeEnum.TASK);
        activity3.setActivityCode(ActivityCodeEnum.CONTOURING_APPROVAL.name());
        activity3.setPrevActivities(Arrays.asList("1"));
        activity3.setNextActivities(Arrays.asList("4"));
        activity3.setLagAfterPrevActivity(10);

        PlannedActivity activity4 = new PlannedActivity();
        activity4.setId("4");
        activity4.setDepartmentID("1");
        activity4.setDefaultGroupID(DEFAULT_GROUPID);
        activity4.setActivityType(ActivityTypeEnum.APPOINTMENT);
        activity4.setActivityCode("PlacePlanningOrder");
        activity4.setPrevActivities(Arrays.asList("2", "3"));
        activity4.setNextActivities(null);

        template.addPlannedActivity(activity);
        template.addPlannedActivity(activity2);
        template.addPlannedActivity(activity3);
        template.addPlannedActivity(activity4);
        return template;
    }

    public static CarePathInstance givenACarePathInstance() {
        CarePathInstance instance = new CarePathInstance();
        instance.setId("1");
        instance.setPatientID("patientID");
        instance.setEncounterID("1");

        ActivityInstance one = new ActivityInstance();
        one.setId("1");
        one.setActivityCode("activityCode1");
        one.setActivityType(ActivityTypeEnum.TASK);
        one.setDefaultGroupID("1");
        one.setDepartmentID("1");
        one.setInstanceID("1");
        one.setIsActiveInWorkflow(true);
        one.setPrevActivities(null);
        one.setNextActivities(Arrays.asList("2"));
        one.setStatus(CarePathStatusEnum.ACTIVE);
        instance.addActivityInstance(one);

        ActivityInstance two = new ActivityInstance();
        two.setId("2");
        two.setActivityCode("activityCode2");
        two.setActivityType(ActivityTypeEnum.TASK);
        two.setDefaultGroupID("1");
        two.setDepartmentID("1");
        two.setInstanceID("");
        two.setIsActiveInWorkflow(false);
        two.setPrevActivities(Arrays.asList("1"));
        two.setNextActivities(Arrays.asList("3"));
        instance.addActivityInstance(two);

        ActivityInstance three = new ActivityInstance();
        three.setId("3");
        three.setActivityCode("activityCode3");
        three.setActivityType(ActivityTypeEnum.TASK);
        three.setDefaultGroupID("1");
        three.setDepartmentID("1");
        three.setInstanceID("");
        three.setIsActiveInWorkflow(false);
        three.setPrevActivities(Arrays.asList("2"));
        three.setNextActivities(null);
        instance.addActivityInstance(three);

        return instance;
    }

    public static Map<GroupDto, List<PractitionerDto>> givenAPractitionerGroupMap() {
        Map<GroupDto, List<PractitionerDto>> groupDtoListMap = new HashMap<>();
        ArrayList<PractitionerDto> practitionerDtoArrayListBoss = new ArrayList<>();
        practitionerDtoArrayListBoss.add(new PractitionerDto("1", "BossHead1",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListHead = new ArrayList<>();
        practitionerDtoArrayListHead.add(new PractitionerDto("1", "BossHead1",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHead.add(new PractitionerDto("2", "Head2",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHead.add(new PractitionerDto("3", "Head3",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListHeadA = new ArrayList<>();
        practitionerDtoArrayListHeadA.add(new PractitionerDto("1", "BossHead1",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHeadA.add(new PractitionerDto("4", "HeadA2",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHeadA.add(new PractitionerDto("5", "HeadA3",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListHeadB = new ArrayList<>();
        practitionerDtoArrayListHeadB.add(new PractitionerDto("2", "Head2",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHeadB.add(new PractitionerDto("6", "HeadB1",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListHeadB.add(new PractitionerDto("7", "HeadB2",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListChest = new ArrayList<>();
        practitionerDtoArrayListChest.add(new PractitionerDto("8", "Chest1",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListChestA = new ArrayList<>();
        practitionerDtoArrayListChestA.add(new PractitionerDto("8", "Chest1",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListChestA.add(new PractitionerDto("9", "ChestA1",ParticipantTypeEnum.PRACTITIONER));
        practitionerDtoArrayListChestA.add(new PractitionerDto("10", "ChestA2",ParticipantTypeEnum.PRACTITIONER));
        ArrayList<PractitionerDto> practitionerDtoArrayListChestB = new ArrayList<>();
        practitionerDtoArrayListChestB.add(new PractitionerDto("11", "ChestB1",ParticipantTypeEnum.PRACTITIONER));
        groupDtoListMap.put(new GroupDto("1", "Oncologist"), practitionerDtoArrayListBoss);
        groupDtoListMap.put(new GroupDto("2", "Oncologist_Head"), practitionerDtoArrayListHead);
        groupDtoListMap.put(new GroupDto("3", "Oncologist_Head_HeadA"), practitionerDtoArrayListHeadA);
        groupDtoListMap.put(new GroupDto("4", "Oncologist_Head_HeadB"), practitionerDtoArrayListHeadB);
        groupDtoListMap.put(new GroupDto("5", "Oncologist_Chest"), practitionerDtoArrayListChest);
        groupDtoListMap.put(new GroupDto("6", "Oncologist_Chest_ChestA"), practitionerDtoArrayListChestA);
        groupDtoListMap.put(new GroupDto("7", "Oncologist_Chest_ChestB"), practitionerDtoArrayListChestB);
        groupDtoListMap.put(new GroupDto("8", "Oncologist_EmptyPractitionerList"), new ArrayList<>());
        groupDtoListMap.put(new GroupDto("9", "Oncologist_NullPractitionerList"), null);
        return groupDtoListMap;
    }
}
