package com.varian.oiscn.rt.view;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TreatmentSummaryCardVO {
    private String bodyPart;
    private String lastTreatmentDate;
    private List<PlanCardVO> planList;

    public void addPlan(PlanCardVO planCardVO) {
        if (planList == null) {
            planList = new ArrayList<>();
        }
        planList.add(planCardVO);
    }
}
