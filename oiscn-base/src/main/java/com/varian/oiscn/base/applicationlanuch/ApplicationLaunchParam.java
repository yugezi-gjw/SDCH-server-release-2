package com.varian.oiscn.base.applicationlanuch;

import lombok.Data;

/**
 * Created by gbt1220 on 7/20/2017.
 */
@Data
public class ApplicationLaunchParam {
    private String moduleId;
    private String ospToken;
    private String taskId;
    private String taskName;
    private Long patientSer;
    private String patientName;
    private String resourceName;
    private String ospCUID;
}
