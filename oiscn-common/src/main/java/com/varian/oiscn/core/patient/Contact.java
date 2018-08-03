package com.varian.oiscn.core.patient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@JsonSerialize
@NoArgsConstructor
@Data
public class Contact {
    private RelationshipEnum relationship;
    private String name;
    private String workPhone;
    private String homePhone;
    private String mobilePhone;
    private String address;
}
