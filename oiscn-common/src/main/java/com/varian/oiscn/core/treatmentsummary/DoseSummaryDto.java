package com.varian.oiscn.core.treatmentsummary;

import lombok.Data;

import java.util.Date;

/**
 * Created by asharma0 on 12-07-2017.
 */
@Data
public class DoseSummaryDto {
    private String siteId;
    private String siteName;
    private Double plannedDose;
    private Double deliveredDose;
    private Double remainingDose;
    private Double dosePerFraction;
    private Date lastTreatmentTime;
}
