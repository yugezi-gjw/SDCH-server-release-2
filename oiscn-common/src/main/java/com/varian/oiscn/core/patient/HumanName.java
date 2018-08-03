package com.varian.oiscn.core.patient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@AllArgsConstructor
@JsonSerialize
@Data
public class HumanName {
    private String familyName;
    private String givenName;
    private String middleName;
    private String fullName;
    private String prefixName;
    private String suffixName;
    private String titleName;
    private NameTypeEnum nameType;
}
