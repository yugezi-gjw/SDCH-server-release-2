package com.varian.oiscn.base.tasklocking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by BHP9696 on 2017/10/16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskLockingDto implements Serializable {
    private String taskId;
    private String activityType;
    private String lockUserName;
    private Long resourceSer;
    private String resourceName;
    private Date lockTime;
}
