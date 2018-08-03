package com.varian.oiscn.core.patient;

import org.eclipse.jetty.util.StringUtil;

/**
 * Created by gbt1220 on 3/28/2017.
 * "N":Normal patient   "VIPEnum":VIPEnum patient
 */
public enum VIPEnum {
    N,
    VIP;
    public static VIPEnum fromString(String vip) {
        if (StringUtil.isBlank(vip)) {
            return N;
        }
        VIPEnum ret;
        switch (vip.charAt(0)) {
        case 'v':
        case 'V':
            ret = VIP;
            break;
        default:
            ret = N;
        }
        return ret;
    }
}
