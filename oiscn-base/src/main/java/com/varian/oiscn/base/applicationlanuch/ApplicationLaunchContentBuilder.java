package com.varian.oiscn.base.applicationlanuch;

import com.varian.oiscn.anticorruption.base.PatientIdMapper;
import com.varian.oiscn.anticorruption.resourceimps.PatientCacheService;
import com.varian.oiscn.core.patient.PatientDto;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gbt1220 on 7/20/2017.
 */
@Slf4j
public class ApplicationLaunchContentBuilder {

    private ApplicationLaunchContentBuilder() {
    }

    /**
     * Return Launch Content.<br>
     *
     * @param param ApplicationLaunchParam
     * @return Launch Content
     */
    public static String getLaunchContent(ApplicationLaunchParam param) {
        StringBuilder result = new StringBuilder("<launchContent>");
        result.append("<moduleid>").append(param.getModuleId()).append("</moduleid>");
        result.append("<token>").append(param.getOspToken()).append("</token>");
        result.append("<context>");
        if (param.getPatientSer() != null) {
            String patientSer = String.valueOf(param.getPatientSer());
            String patientID1;
            if (PatientIdMapper.getPatientId1Mapper().equals(PatientIdMapper.IDENTIFIER_MAPPER_TO_HIS_ID)) {
                patientID1 = patientSer;
            } else {
                PatientCacheService patientCacheService = new PatientCacheService();
                PatientDto patientDto = patientCacheService.queryPatientByPatientId(patientSer);
                if (patientDto != null) {
                    patientID1 = patientDto.getAriaId();
                } else {
                    patientID1 = "";
                    log.error("Cannot find ariaId.");
                }
            }
            result.append("<subject type=\"patient\" id=\"").append(patientID1).append("\">").append("</subject>");

            if (isNotBlank(param.getTaskId())) {
                result.append("<subject type=\"task\" id=\"").append(param.getTaskId()).append("\">True").append("</subject>");
                result.append("<subject type=\"LocalWorklist\" id=\"LocalWorklist\">").append("&lt;Tasks&gt;&lt;Task Id=\"").
                        append(param.getTaskId()).append("\" TaskName=\"").append(param.getTaskName()).append("\"").
                        append(" PatientId=\"").append(patientID1).append("\"").
                        append(" PatientName=\"").append(param.getPatientName()).append("\"").
                        append(" ModuleId=\"").append(param.getModuleId()).append("\"").
                        append(" ResourceName=\"").append(param.getResourceName()).append("\"").
                        append(" IsActiveTaskInSession=\"true\"").
                        append(" /&gt;&lt;/Tasks&gt;").
                        append("</subject>");
            }
        }
        result.append("<subject type=\"OSP\" uid=\"").append(param.getOspCUID()).append("\">").append("</subject>");
        result.append("</context>");
        result.append("</launchContent>");
        return result.toString();
    }
}
