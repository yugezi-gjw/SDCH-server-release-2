package com.varian.oiscn.core.treatmentsummary;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TreatmentSummaryDto {
    private Date lastTreatmentDate;
    private String doseUnit;
    private List<PlanSummaryDto> plans;
}
