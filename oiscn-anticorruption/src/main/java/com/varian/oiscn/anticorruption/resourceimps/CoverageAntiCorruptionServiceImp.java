package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.anticorruption.assembler.CoverageAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCoverageInterface;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.pagination.Pagination;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 11/1/2017
 * @Modified By:
 */
public class CoverageAntiCorruptionServiceImp {

    private FHIRCoverageInterface fhirCoverageInterface;

    /**
     *  Default constructor
     */
    public CoverageAntiCorruptionServiceImp() {
        this.fhirCoverageInterface = new FHIRCoverageInterface();
    }

    /**
     * Create Coverage
     * @param coverageDto
     * @return new Id
     */
    public String createCoverage(CoverageDto coverageDto) {
        Coverage coverage = CoverageAssembler.getCoverage(coverageDto);
        return fhirCoverageInterface.create(coverage);
    }

    /**
     * Update coverage
     * @param dto coverage dto object
     * @return coverage id
     */
    public String updateCoverage(CoverageDto dto) {
        if (dto == null) {
            return StringUtils.EMPTY;
        }
        Coverage coverage = fhirCoverageInterface.queryByPatientId(dto.getPatientSer());
        if (coverage == null) {
            return createCoverage(dto);
        } else {
            CoverageAssembler.assemblerCoverage(coverage, dto);
            return fhirCoverageInterface.update(coverage);
        }
    }
    public Pagination<CoverageDto> queryCoverageDtoPaginationByPatientList(List<String> patientSerList,int countPerPage,int pageNumberTo){
        Pagination<Coverage> pagination = fhirCoverageInterface.queryCoveragePaginationByPatientSerList(patientSerList,countPerPage,pageNumberTo);
        Pagination<CoverageDto> rpagination = new Pagination<CoverageDto>(){{
           setLstObject(new ArrayList());
        }};
        if(pagination.getLstObject() != null ){
            pagination.getLstObject().forEach(coverage -> rpagination.getLstObject().add(CoverageAssembler.getCoverageDto(coverage)));
            rpagination.setTotalCount(rpagination.getTotalCount()+pagination.getTotalCount());
        }
         return rpagination;
    }


    /**
     * Query coverage dto by patient id
     * @param patientId patient id
     * @return coverage dto object
     */
    public CoverageDto queryByPatientId(String patientId) {
        CoverageDto coverageDto = null;
        Coverage coverage = fhirCoverageInterface.queryByPatientId(patientId);
        if (coverage != null) {
            coverageDto = CoverageAssembler.getCoverageDto(coverage);
        }
        return coverageDto;
    }
}
