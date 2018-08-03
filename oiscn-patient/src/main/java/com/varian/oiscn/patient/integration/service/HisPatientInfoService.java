package com.varian.oiscn.patient.integration.service;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.integration.config.HisPatientInfoConfigService;
import com.varian.oiscn.core.patient.RegistrationVO;
import com.varian.oiscn.patient.integration.exception.HisServiceException;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/20/2017
 * @Modified By:
 */
@Slf4j
public class HisPatientInfoService {

    private static JsonSerializer jsonSerializer = new JsonSerializer();

    private HisPatientInfoService() {

    }

    /**
     * Check the server status
     *
     * @return
     */
    public static boolean isOK() {
        return HisPatientInfoConfigService.getConfiguration() != null;
    }

    public static RegistrationVO callHisWebservice(String params) {
        HisPatientHttpClient client = null;
        RegistrationVO hisPatientVO = null;
        String json = null;
        try {
            client = new HisPatientHttpClient(params);
            json = client.sendMessage();
            if (isNotEmpty(json)) {
                hisPatientVO = (RegistrationVO) jsonSerializer.getObject(json, RegistrationVO.class);
            }
        } catch (HisServiceException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (Exception ie) {
                log.error(ie.getMessage());
            }
        }
        return hisPatientVO;
    }

}
