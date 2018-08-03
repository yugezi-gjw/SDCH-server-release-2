package com.varian.oiscn.core.assign;
import lombok.Data;

import java.util.Date;
/**
 * Created by bhp9696 on 2018/3/23.
 */
@Data
public class AssignResource {

    private String id;
    /**  资源的ID */
    private String resourceId;
    /** Activity Code */
    private String activityCode;

    private Long patientSer;

    private Long encounterId;

    private Integer amount;

}
