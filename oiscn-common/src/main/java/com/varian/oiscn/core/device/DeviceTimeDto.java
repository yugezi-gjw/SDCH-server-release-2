package com.varian.oiscn.core.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by BHP9696 on 2017/8/16.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeviceTimeDto implements Serializable {
    private String startTime;
    private String endTime;
}
