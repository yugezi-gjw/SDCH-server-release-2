package com.varian.oiscn.carepath.assembler;

import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.carepath.vo.ActivityEntryVO;
import com.varian.oiscn.core.carepath.CarePathTemplate;
import com.varian.oiscn.core.carepath.PlannedActivity;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 4/21/2017.
 */
@AllArgsConstructor
public class CarePathTemplateAssembler {
    private CarePathTemplate template;

    public List<ActivityEntryVO> getActivityEntries(String userId, String userGroup, List<String> groupIDList) {
        List<ActivityEntryVO> result = new ArrayList<>();
        for (PlannedActivity activity : template.getActivities()) {
            if (groupIDList != null && groupIDList.contains(activity.getDefaultGroupID())) {
                result.add(new ActivityEntryVO(activity.getActivityCode(),
                        activity.getActivityType().name(),
                        ActivityCodesReader.getActivityCode(activity.getActivityCode()).getEntryContent()));
            }
        }
        return result;
    }

}
