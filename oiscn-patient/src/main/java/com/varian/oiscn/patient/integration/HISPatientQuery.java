package com.varian.oiscn.patient.integration;

import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.patient.integration.service.HisPatientInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/21/2017
 * @Modified By:
 */
@Slf4j
public class HISPatientQuery implements IPatientQuery {

    private static final String REQUEST_PARAMS_NAME = "patientid=";

    /**
     * Query patient info by hisId
     *
     * @param hisId
     * @return
     */
    @Override
    public RegistrationVO queryByHisId(String hisId) {
        return StringUtils.isEmpty(hisId) ? null : HisPatientInfoService.callHisWebservice("patientid=" + hisId);
    }

    public RegistrationVO queryByZyId(String zyId) {
        log.debug("queryByZyId-[{}]", zyId);
        RegistrationVO vo = null;
        if (StringUtils.isEmpty(zyId)) {
            return vo;
        } else {
            if (HisPatientInfoService.isOK()) {
                log.debug("HisPatientInfoService.isOK");
                vo = HisPatientInfoService.queryHisPatient(zyId);
            }

            return vo;
        }
    }
}
