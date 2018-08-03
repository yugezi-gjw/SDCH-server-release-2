package com.varian.oiscn.anticorruption.assembler;


import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.anticorruption.converter.DataHelper;
import com.varian.oiscn.core.coverage.CoverageDto;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.ResourceType;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
@Slf4j
public class CoverageAssembler {

    private CoverageAssembler() {

    }

    /**
     *  Save insurance type into ARIA
     * @param coverageDto
     * @return fhir Coverage
     */
    public static Coverage getCoverage(CoverageDto coverageDto) {
        Coverage coverage = new Coverage();
        if(coverageDto != null) {
            coverage.setPolicyHolder(DataHelper.getReference(coverageDto.getPatientSer(), null, ResourceType.Patient.name(), false));
            coverage.getGrouping().setPlan(coverageDto.getInsuranceTypeCode()).setPlanDisplay(coverageDto.getInsuranceTypeDesc());
            coverage.setPrimaryFlag(new IntegerType(0));
        }
        return coverage;
    }
    /**
     *  Save insurance type into ARIA
     * @param coverage
     * @return fhir Coverage
     */
    public static CoverageDto getCoverageDto(Coverage coverage) {
        CoverageDto coverageDto = new CoverageDto();
        if(coverage != null) {
            coverageDto.setPatientSer(DataHelper.getReferenceValue(coverage.getPolicyHolder().getReference()));
            coverageDto.setInsuranceTypeCode(coverage.getGrouping().getPlan());
            coverageDto.setInsuranceTypeDesc(coverage.getGrouping().getPlanDisplay());
        }
        return coverageDto;
    }

    /**
     * Assembler dto data to coverage
     * @param coverage coverage object
     * @param coverageDto dto object
     */
    public static void assemblerCoverage(Coverage coverage, CoverageDto coverageDto) {
        if (coverage == null) {
            coverage = new Coverage();
        }
        if(coverageDto != null) {
            //TODO: now coverage still fails to update, need to confirm with Shubham.
//            coverage.getPolicyHolder().setReference(coverageDto.getPatientId());
//            coverage.getPolicyHolder().setId(coverageDto.getPatientId());
//            coverage.getSubscriber().setReference(coverageDto.getPatientId());
//            coverage.getSubscriber().setId(coverageDto.getPatientId());
//            coverage.getBeneficiary().setReference(coverageDto.getPatientId());
//            coverage.getBeneficiary().setId(coverageDto.getPatientId());
            coverage.getGrouping().setPlan(coverageDto.getInsuranceTypeCode()).setPlanDisplay(coverageDto.getInsuranceTypeDesc());
        }
    }
}
