package com.varian.oiscn.core.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@AllArgsConstructor
@JsonSerialize
@Data
public class Identifier {
    private String domain;
    private String value;
    private IdentifierTypeEnum type;
    private IdentifierStatusEnum status;
    private IdentifierUseEnum use;

}
