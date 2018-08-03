/**
 *
 */
package com.varian.oiscn.anticorruption.assembler;

import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.core.coverage.CoverageDto;
import org.hl7.fhir.dstu3.model.Coverage.GroupComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest({CoverageAssembler.class})
public class CoverageAssemblerTest {

    protected CoverageDto dto;

    @Before
    public void setUp() throws Exception {

    }

    /**
     * Test method for {@link com.varian.oiscn.anticorruption.assembler.CoverageAssembler#getCoverage(com.varian.oiscn.core.coverage.CoverageDto)}.
     */
    @Test
    public void testGetCoverage() {

        String insuranceTypeCode = "insuranceTypeCode";
        String insuranceTypeDesc = "insuranceTypeDesc";

        dto = new CoverageDto();
        dto.setPatientSer("PatientId");
        dto.setInsuranceTypeCode(insuranceTypeCode);
        dto.setInsuranceTypeDesc(insuranceTypeDesc);
        // PowerMockito.when(dto.getInsuranceTypeCode()).thenReturn(insuranceTypeCode);
        // PowerMockito.when(dto.getInsuranceTypeDesc()).thenReturn(insuranceTypeDesc);

        Coverage actual = CoverageAssembler.getCoverage(dto);
        GroupComponent group = actual.getGrouping();
        assertEquals(group.getPlan(), insuranceTypeCode);
        assertEquals(group.getPlanDisplay(), insuranceTypeDesc);
    }

    @Test
    public void testGetCoverageDto(){
        String insuranceTypeCode = "insuranceTypeCode";
        String insuranceTypeDesc = "insuranceTypeDesc";
        Coverage coverage = PowerMockito.mock(Coverage.class);
        Reference reference = PowerMockito.mock(Reference.class);
        PowerMockito.when(coverage.getPolicyHolder()).thenReturn(reference);
        PowerMockito.when(reference.getReference()).thenReturn("patientId/#1212");
        GroupComponent groupComponent = PowerMockito.mock(GroupComponent.class);
        PowerMockito.when(coverage.getGrouping()).thenReturn(groupComponent);
        PowerMockito.when(groupComponent.getPlan()).thenReturn(insuranceTypeCode);
        PowerMockito.when(groupComponent.getPlanDisplay()).thenReturn(insuranceTypeDesc);
        CoverageDto dto = CoverageAssembler.getCoverageDto(coverage);
        Assert.assertNotNull(dto);
        Assert.assertTrue(dto.getPatientSer().equals("1212"));
    }

    @Test
    public void testAssemblerCoverage(){
        String insuranceTypeCode = "insuranceTypeCode";
        String insuranceTypeDesc = "insuranceTypeDesc";
        dto = new CoverageDto(){{
            setInsuranceTypeCode(insuranceTypeCode);
            setInsuranceTypeDesc(insuranceTypeDesc);
        }};
        Coverage coverage = null;
        CoverageAssembler.assemblerCoverage(coverage,dto);
    }

}
