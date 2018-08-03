package com.varian.oiscn.patient.resource;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

/**
 * Created by gbt1220 on 1/13/2017.
 */
public class DefaultAriaIDGenerator implements IAriaIDGenerator {
    @Override
    public String generate() {
        return new Date().getTime() + RandomStringUtils.randomNumeric(4);
    }
}
