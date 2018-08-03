package com.varian.oiscn.patient.registry;

import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.UserContext;

public interface IPatientRegistry {
    RegistryVerifyStatusEnum verifyRegistry();
    Long saveOrUpdate(Configuration configuration, UserContext userContext);
}