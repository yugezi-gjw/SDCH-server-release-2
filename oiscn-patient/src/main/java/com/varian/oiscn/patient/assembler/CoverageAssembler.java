package com.varian.oiscn.patient.assembler;

import com.varian.oiscn.base.coverage.PayorInfoPool;
import com.varian.oiscn.core.coverage.CoverageDto;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by gbt1220 on 12/20/2017.
 */
@Slf4j
public class CoverageAssembler {

    private CoverageAssembler() {}

    /**
     * Create coverage dto object
     * @param patientSer patient id
     * @param insuranceCode insurance code
     * @return coverage dto object
     */
    public static CoverageDto getCoverageDto(String patientSer, String insuranceCode) {
        CoverageDto coverageDto = null;
        if (isNotBlank(patientSer) && isNotBlank(insuranceCode)) {
            String insuranceType = PayorInfoPool.getValue(insuranceCode);
            if (isEmpty(insuranceType)) {
                log.error("Not found the insurance, please check the insurance type: {}", insuranceCode);
                return null;
            }
            coverageDto = new CoverageDto();
            coverageDto.setPatientSer(patientSer);
            coverageDto.setInsuranceTypeCode(insuranceCode);
            coverageDto.setInsuranceTypeDesc(insuranceType);
        }
        return coverageDto;
    }
}
