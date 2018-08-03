package com.varian.oiscn.core.patient;

import org.eclipse.jetty.util.StringUtil;

/**
 * Created by gbt1220 on 3/28/2017.
 * <p>
 * * This value set has an inline code system http://hl7.org/fhir/marital-status, which defines the following codes:
 * U The person is not presently married. The marital history is not known or stated.
 * A Annulled Marriage contract has been declared null and to not have existed
 * D Marriage contract has been declared dissolved and inactive
 * I Subject to an Interlocutory Decree.
 * L Legally Separated
 * M A current marriage contract is active
 * P More than 1 current spouse
 * S No marriage contract has ever been entered
 * T Person declares that a domestic partner relationship exists.
 * W The spouse has died
 */
public enum MaritalStatusEnum {
    U,
    A,
    D,
    I,
    L,
    M,
    P,
    S,
    T,
    W;

    public static MaritalStatusEnum fromString(String maritalStatus) {
        if (StringUtil.isBlank(maritalStatus)) {
            return U;
        }
        MaritalStatusEnum ret;
        switch (maritalStatus.charAt(0)) {
            case 'u':
            case 'U':
                ret = U;
            case 'a':
            case 'A':
                ret = A;
            case 'd':
            case 'D':
                ret = D;
            case 'i':
            case 'I':
                ret = I;
            case 'l':
            case 'L':
                ret = L;
            case 'm':
            case 'M':
                ret = M;
            case 'p':
            case 'P':
                ret = P;
            case 's':
            case 'S':
                ret = S;
            case 't':
            case 'T':
                ret = T;
            case 'w':
            case 'W':
                ret = W;
            default:
                ret = U;
        }
        return ret;
    }
}
