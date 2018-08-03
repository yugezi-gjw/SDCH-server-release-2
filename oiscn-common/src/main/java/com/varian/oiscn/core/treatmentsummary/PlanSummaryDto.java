package com.varian.oiscn.core.treatmentsummary;

import lombok.Data;

import java.util.List;
import java.util.Date;

@Data
public class PlanSummaryDto {
    private String planSetupId;
    private String planSetupName;
    private Integer deliveredFractions;
    private Integer plannedFractions;
    private Double deliveredDose;
    private Double plannedDose;
    private PlanStatusEnum status;
    private Date createdDt;
    private Date lastTreatmentTime;

    List<DoseSummaryDto> doseSummary;
    List<FieldSummaryDto> fields;
}
