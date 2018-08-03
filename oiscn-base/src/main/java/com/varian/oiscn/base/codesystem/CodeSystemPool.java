package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.anticorruption.resourceimps.GroupAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.base.coverage.PayorInfoPool;
import com.varian.oiscn.base.diagnosis.BodyPartVO;
import com.varian.oiscn.base.group.GroupInfoPool;
import com.varian.oiscn.base.helper.GroupPractitionerHelper;
import com.varian.oiscn.base.statusicon.StatusIconPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.group.GroupDto;
import com.varian.oiscn.core.practitioner.PractitionerDto;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/19/2017.
 */
@Slf4j
public class CodeSystemPool {

    protected static List<CodeValue> carePathList;

    private CodeSystemPool() {

    }

    public static void initDiagnosis(String scheme, String fhirLanguage) {
        CodeSystemServiceImp codeSystemServiceImp = new CodeSystemServiceImp();
        if (codeSystemServiceImp.isDiagnosisExisted(scheme)) {
        	log.info("OK - Diagnosis exists, no need initialization from FHIR.");
            return;
        }

        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem diagnosis = valueSetAntiCorruptionServiceImp.queryDiagnosisListByScheme(scheme, fhirLanguage);
        if (diagnosis != null) {
            diagnosis.setCode(scheme);
            diagnosis.setSystem(scheme);
            codeSystemServiceImp.create(diagnosis);
            log.info("OK - Init Diagnosis from FHIR.");
        } else {
        	log.error("No Diagnosis From FHIR ! {}", SystemConfigConstant.MSG_CHECK_FHIR);
        }
    }

    public static void initStatusIcon() {
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem statusIcon = valueSetAntiCorruptionServiceImp.queryAllPatientStatusIcons();
        if (statusIcon != null) {
            statusIcon.getCodeValues().stream().forEach(
                    codeValue -> StatusIconPool.put(codeValue.getCode(), codeValue.getDesc())
            );
            log.info("OK - Init Status Icon from FHIR.");
        } else {
        	log.error("No Status Icon From FHIR ! {}", SystemConfigConstant.MSG_CHECK_FHIR);
        }
    }

    public static void initPatientLabel() {
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem patientLabels = valueSetAntiCorruptionServiceImp.queryAllPatientLabels();
        if (patientLabels != null) {
            patientLabels.getCodeValues().stream().forEach(codeValue -> PatientLabelPool.put(codeValue.getCode(), codeValue.getDesc()));
            log.info("OK - Init Patient Label from FHIR.");
        } else {
        	log.error("No Patient Label From FHIR ! {}", SystemConfigConstant.MSG_CHECK_FHIR);
        }
    }

    public static void initGroupPractitionerListMap(){
        //TODO 目前先配置成departmentSer，将来需要改成name，然后调用查询接口取
        List<String> defaultDepartmentIds = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT);
        if (!defaultDepartmentIds.isEmpty()) {
            GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
            Map<GroupDto, List<PractitionerDto>> groupDtoWithResourceIdListMap = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(SystemConfigPool.queryGroupOncologistPrefix(), defaultDepartmentIds.get(0));
            if (groupDtoWithResourceIdListMap != null) {
            	GroupPractitionerHelper.setOncologyGroupTreeNode(GroupPractitionerHelper.convertMapToTree(groupDtoWithResourceIdListMap));
                log.info("OK - Init Group Practitioner from FHIR.");
            } else {
                log.error("No Group Practitioner Settings in FHIR! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            }

            groupDtoWithResourceIdListMap = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(SystemConfigPool.queryGroupPhysicistPrefix(), defaultDepartmentIds.get(0));
            if (groupDtoWithResourceIdListMap != null) {
                GroupPractitionerHelper.setPhysicistGroupTreeNode(GroupPractitionerHelper.convertMapToTree(groupDtoWithResourceIdListMap));
                log.info("OK - Init Group Physicist from FHIR.");
            } else {
                log.error("No Group Physicist Settings in FHIR! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            }

            groupDtoWithResourceIdListMap = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(SystemConfigPool.queryGroupTechnicianPrefix(), defaultDepartmentIds.get(0));
            if (groupDtoWithResourceIdListMap != null) {
                GroupPractitionerHelper.setTechGroupTreeNode(GroupPractitionerHelper.convertMapToTree(groupDtoWithResourceIdListMap));
                log.info("OK - Init Group Technician from FHIR.");
            } else {
                log.error("No Group Technician Settings in FHIR! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            }

            groupDtoWithResourceIdListMap = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(SystemConfigPool.queryGroupNursePrefix(), defaultDepartmentIds.get(0));
            if (groupDtoWithResourceIdListMap != null) {
                GroupPractitionerHelper.setNurseGroupTreeNode(GroupPractitionerHelper.convertMapToTree(groupDtoWithResourceIdListMap));
                log.info("OK - Init Group Settings from FHIR.");
            } else {
                log.error("No Group Nurse Settings in FHIR! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            }

        } else {
            log.error("No Default Department Settings in System Config! {}", SystemConfigConstant.MSG_CHECK_SYSTEM_CONFIG);
        }
    }

    public static void initPhysicistGroupInfoMap() {
        List<String> defaultDepartmentIds = SystemConfigPool.queryConfigValueByName(SystemConfigConstant.DEFAULT_DEPARTMENT);
        if (!defaultDepartmentIds.isEmpty()) {
            GroupAntiCorruptionServiceImp groupAntiCorruptionServiceImp = new GroupAntiCorruptionServiceImp();
            Map<GroupDto, List<PractitionerDto>> groupDtoListMap = groupAntiCorruptionServiceImp.queryGroupDtoWithResourceIdListMap(SystemConfigPool.queryGroupPhysicistPrefix(), defaultDepartmentIds.get(0));

            if(!groupDtoListMap.isEmpty()) {
                groupDtoListMap.forEach((groupDto, lstPartitionerDto) -> {
                    if(groupDto.getGroupName().startsWith(SystemConfigPool.queryGroupPhysicistPrefix())){
                        GroupInfoPool.put(groupDto.getGroupId(), groupDto.getGroupName());
                    }
                });
            } else {
                log.error("No Group Physicist Settings in FHIR! {}", SystemConfigConstant.MSG_CHECK_FHIR);
            }

        } else {
        	log.error("No Default Department Settings in System Config! {}", SystemConfigConstant.MSG_CHECK_SYSTEM_CONFIG);
        }
    }

    public static void initPayorInfos() {
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem payorInfos = valueSetAntiCorruptionServiceImp.queryAllPayorInfo();
        if(payorInfos != null && payorInfos.getCodeValues() != null) {
            payorInfos.getCodeValues().stream().forEach(codeValue -> PayorInfoPool.put(codeValue.getCode(), codeValue.getDesc()));
        }
    }

    /**
     * init body part
     * @param fhirLanguage
     */
    public static void initBodyPart(String fhirLanguage) {
        CodeSystemServiceImp codeSystemServiceImp = new CodeSystemServiceImp();
        if (codeSystemServiceImp.isBodyPartExisted()) {
        	log.info("OK - BodyPart exists, no need initialization from FHIR.");
            return;
        }
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem codeSystem = valueSetAntiCorruptionServiceImp.queryAllPrimarySites(fhirLanguage);
        List<BodyPartVO> bodyParts = new ArrayList<>();
        if (codeSystem != null && codeSystem.getCodeValues() != null) {
            codeSystem.getCodeValues().forEach(codeValue -> bodyParts.add(new BodyPartVO(codeValue.getCode(), codeValue.getDesc(),null)));
        }
        
        if(!bodyParts.isEmpty()){
            codeSystemServiceImp.createBodyParts(bodyParts);
            log.info("OK - Init BodyPart from FHIR.");
        } else {
        	log.error("No BodyPart From FHIR ! [{}]", SystemConfigConstant.MSG_CHECK_FHIR);
        }
    }

    public static void initCarePath() {
        final ValueSetAntiCorruptionServiceImp service = new ValueSetAntiCorruptionServiceImp();
        final CodeSystem cs = service.queryAllCarePathTemplates();
        if (cs != null) {
            log.debug("CodeSystem: code=[{}], system=[{}]", cs.getCode(), cs.getSystem());
            carePathList = cs.getCodeValues();
        } else {
        	log.error("No CarePath From FHIR ! {}", SystemConfigConstant.MSG_CHECK_FHIR);
        }
    }

    public static List<CodeValue> getCarePathList() {
        return carePathList;
    }
}
