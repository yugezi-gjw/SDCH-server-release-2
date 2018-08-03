package com.varian.oiscn.core.device;

/**
 * Created by bhp9696 on 2018/3/22.
 */
public enum DeviceJobTypeEnum {
//  CT 设备
    CT,
//    制模设备
    IMMOB,
//    治疗设备
    TREATMENT,
    // NOT-IN-ARIA 非ARIA设备，比如TPS Resource
    NOT_IN_ARIA,
//    未确定用途
    NONE;


    public DeviceJobTypeEnum fromCode(String code){
        String tmpcode = code.toUpperCase();
        DeviceJobTypeEnum deviceTypeEnum;
        switch (tmpcode){
            case "CT":
                deviceTypeEnum = CT;
                 break;
            case  "IMMOB":
                deviceTypeEnum = IMMOB;
                break;
            case    "TREATMENT":
                deviceTypeEnum = TREATMENT;
                break;
            case "NOT_IN_ARIA":
                deviceTypeEnum = NOT_IN_ARIA;
            default:
                deviceTypeEnum = NONE;

        }
        return deviceTypeEnum;
    }

    public String toCode(DeviceJobTypeEnum deviceTypeEnum){
        String code;
        switch (deviceTypeEnum){
            case CT:
                code = "CT";
                break;
            case IMMOB:
                code = "IMMOB";
                break;
            case TREATMENT:
                code  ="TREATMENT";
                break;
                default:
                    code = "NONE";
        }
        return code;
    }
}
