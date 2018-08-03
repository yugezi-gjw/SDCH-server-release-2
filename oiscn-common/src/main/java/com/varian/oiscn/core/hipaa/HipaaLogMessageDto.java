package com.varian.oiscn.core.hipaa;

import com.varian.oiscn.util.hipaa.HipaaEvent;
import com.varian.oiscn.util.hipaa.HipaaObjectType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by gbt1220 on 2/27/2018.
 */
@Data
@AllArgsConstructor
public class HipaaLogMessageDto {

    private String patientSer;

    private HipaaEvent event;

    private HipaaObjectType objectType;

    private String comment = "";
}
