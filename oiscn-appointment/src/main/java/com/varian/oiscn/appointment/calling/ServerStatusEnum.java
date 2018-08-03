package com.varian.oiscn.appointment.calling;

import com.varian.oiscn.util.I18nReader;
import lombok.Getter;

/**
 * Server Status Enumeration.<br>
 */
@Getter
public enum ServerStatusEnum {
    NO_CONFIGURATION("90", I18nReader.getLocaleValueByKey("Calling.Server.NoConfiguration")), // NO_CONFIGURATION
    BAD_CONFIGURATION("91", I18nReader.getLocaleValueByKey("Calling.Server.BadConfiguration")), // BAD_CONFIGURATION
    SERVER_NOT_READY("92", I18nReader.getLocaleValueByKey("Calling.Server.ServerNotReady")), // SERVER_NOT_READY
    SERVICE_NOT_AVAILABLE("93", I18nReader.getLocaleValueByKey("Calling.Server.ServiceNotAvailable")), // SERVICE_NOT_AVAILABLE
    SERVICE_TIME_OUT("94", I18nReader.getLocaleValueByKey("Calling.Server.ServiceTimeout")), // SERVICE_TIME_OUT
    SERVICE_ERROR("95", I18nReader.getLocaleValueByKey("Calling.Server.UnknownError")), // SERVICE_ERROR
    BAD_REQUEST("96", I18nReader.getLocaleValueByKey("Calling.Server.BadRequest")), // BAD_REQUEST
    NORMAL("00", "");

    protected String code;
    protected String errMsg;

    ServerStatusEnum(String code, String errMsg) {
        this.code = code;
        this.errMsg = errMsg;
    }
}
