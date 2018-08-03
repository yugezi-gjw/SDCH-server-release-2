package com.varian.oiscn.anticorruption.datahelper;

import ca.uhn.fhir.parser.IParser;
import com.varian.fhir.common.Stu3ContextHelper;
import com.varian.fhir.resources.TreatmentSummary;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.dstu3.model.Bundle;

import java.io.File;

/**
 * Created by fmk9441 on 2017-07-24.
 */
public final class MockTreatmentSummaryUtil {
    private MockTreatmentSummaryUtil() {

    }

    public static TreatmentSummary givenATreatmentSummary() throws Exception {
        File file = new File("src/test/resources/TreatmentSummary.json");
        String content = FileUtils.readFileToString(file);
        IParser parser = Stu3ContextHelper.getStu3Context().newJsonParser();
        TreatmentSummary treatmentSummary = (TreatmentSummary) ((Bundle) parser.parseResource(content)).getEntryFirstRep().getResource();
        return treatmentSummary;
    }
}
