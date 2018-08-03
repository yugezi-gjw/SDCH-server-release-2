package com.varian.oiscn.patient.registry;

public enum RegistryVerifyStatusEnum {

    PASS(000, "PASS"),
    DUPLICATE_HIS(001, "Duplicate HIS ID."),
    DUPLICATE_VID(002, "Duplicate VID."),
    INVALID_DIAGNOSIS_DATE(003, "Invalid diagnosis date.");

    private final int code;
    private final String reason;

    RegistryVerifyStatusEnum(final int statusCode, final String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
    }
}
