package com.varian.oiscn.core.patient;

import org.eclipse.jetty.util.StringUtil;

/**
 * Created by gbt1220 on 3/28/2017.
 * <p>
 * "N":Normal  "M":Merged   "D":Deleted
 */
public enum PatientStatusEnum {
    N,
    M,
    D;
    public static PatientStatusEnum fromString(String maritalStatus) {
        if (StringUtil.isBlank(maritalStatus)) {
            return N;
        }
        PatientStatusEnum ret;
        switch (maritalStatus.charAt(0)) {
            case 'n':
            case 'N':
                ret = N;
                break;
            case 'm':
            case 'M':
                ret = M;
                break;
            case 'd':
            case 'D':
                ret = D;
                break;
            default:
                ret = N;
        }
        return ret;
    }
}
