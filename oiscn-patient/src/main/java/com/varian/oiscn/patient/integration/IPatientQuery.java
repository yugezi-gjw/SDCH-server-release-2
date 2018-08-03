package com.varian.oiscn.patient.integration;

import com.varian.oiscn.core.patient.RegistrationVO;

/**
 * Created by gbt1220 on 11/21/2017.
 */
public interface IPatientQuery {
    RegistrationVO queryByHisId(String hisId);

    RegistrationVO queryByZyId(String zyId);
}
