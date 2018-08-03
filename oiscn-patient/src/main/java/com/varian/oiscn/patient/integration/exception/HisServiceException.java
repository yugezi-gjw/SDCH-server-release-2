package com.varian.oiscn.patient.integration.exception;

import com.varian.oiscn.patient.integration.config.HisServerStatusEnum;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 12/20/2017
 * @Modified By:
 */
public class HisServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    protected HisServerStatusEnum status = null;

    public HisServiceException(HisServerStatusEnum status) {
        this.status = status;
    }

    public HisServerStatusEnum getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return status.getErrMsg();
    }
}
