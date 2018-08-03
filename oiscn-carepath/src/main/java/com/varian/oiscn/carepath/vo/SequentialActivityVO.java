package com.varian.oiscn.carepath.vo;

import com.varian.oiscn.core.activity.ActivityTypeEnum;
import com.varian.oiscn.core.activity.SequentialActivityStatusEnum;
import lombok.Data;

import java.util.List;

/**
 * Created by gbt1220 on 5/17/2017.
 */
@Data
public class SequentialActivityVO {
    private String activityId;
    private String instanceId;
    private String displayName;
    private ActivityTypeEnum type;
    private String activityCode;
    private SequentialActivityStatusEnum status;
    private String workspaceType;
    private String defaultAppointmentView;
    private List<String> dynamicFormTemplateIds;
    private String eclipseModuleId;
//  CarePath InstanceId
    private String carePathInstanceId;
}
