package com.varian.oiscn.patient.view;

import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import lombok.Data;

import java.util.Map;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 4/2/2018
 * @Modified By:
 */
@Data
public class PatientRegistrationVO {

    public static final String N1_HIS_ONLY = "N1_HIS_ONLY";

    public static final String N2_HIS_ARIA = "N2_HIS_ARIA";

    public static final String N3_HIS_ARIA_QIN_ACTIVE = "N3_HIS_ARIA_QIN_ACTIVE";//in treatment

    public static final String N4_HIS_ARIA_QIN_INACTIVE = "N4_HIS_ARIA_QIN_INACTIVE";//finished treatment

    public static final String N5_NOHIS_ARIA_QIN_ACTIVE = "N5_NOHIS_ARIA_QIN_ACTIVE";//in treatment with no his

    public static final String N6_NOHIS_ARIA_QIN_INACTIVE = "N6_NOHIS_ARIA_QIN_INACTIVE";//finished treatment with no his

    public static final String N7_NOHIS_ARIA_ONLY = "N7_NOHIS_ARIA_ONLY";

    public static final String N8_NOHIS_QIN_ONLY = "N8_NOHIS_QIN_ONLY";//no possible scenario

    public static final String N9_NOALL = "N9_NOALL";

    private Patient patient;
    private Encounter encounter;
    private String scenarioFlag;
    private Map<String, String> dynamicFormItems;
}
