package com.varian.oiscn.patient.integration;

import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.patient.integration.service.HisPatientInfoService;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/21/2017
 * @Modified By:
 */
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

        if (isEmpty(hisId)) {
            return null;
        }

        return HisPatientInfoService.callHisWebservice(REQUEST_PARAMS_NAME + hisId);
    }
}
