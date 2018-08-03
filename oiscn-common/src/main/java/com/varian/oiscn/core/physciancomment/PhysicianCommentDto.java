package com.varian.oiscn.core.physciancomment;

import lombok.Data;

import java.util.Date;

/**
 * Created by gbt1220 on 12/21/2017.
 */
@Data
public class PhysicianCommentDto {
    private String patientSer;
    private String practitionerId;
    private String comments;
    private Date lastUpdateTime;
}
