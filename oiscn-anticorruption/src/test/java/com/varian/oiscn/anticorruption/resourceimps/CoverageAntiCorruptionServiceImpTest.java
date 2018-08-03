package com.varian.oiscn.anticorruption.resourceimps;

import com.varian.fhir.resources.Coverage;
import com.varian.oiscn.anticorruption.assembler.CoverageAssembler;
import com.varian.oiscn.anticorruption.fhirinterface.FHIRCoverageInterface;
import com.varian.oiscn.core.coverage.CoverageDto;
import com.varian.oiscn.core.pagination.Pagination;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

/**
 * Created by bhp9696 on 2017/11/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CoverageAntiCorruptionServiceImp.class, FHIRCoverageInterface.class, CoverageAssembler.class})
public class CoverageAntiCorruptionServiceImpTest {
    private CoverageAntiCorruptionServiceImp coverageAntiCorruptionServiceImp;
    private FHIRCoverageInterface fhirCoverageInterface;

    @Before
    public void setup(){
        PowerMockito.mockStatic(CoverageAssembler.class);
        fhirCoverageInterface = PowerMockito.mock(FHIRCoverageInterface.class);
        try {
            PowerMockito.whenNew(FHIRCoverageInterface.class).withAnyArguments().thenReturn(fhirCoverageInterface);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        coverageAntiCorruptionServiceImp = new CoverageAntiCorruptionServiceImp();
    }

    @Test
    public void givenCoverageDtoWhenCreateCoverageThenReturnId(){
        CoverageDto coverageDto = new CoverageDto();
        Coverage coverage = PowerMockito.mock(Coverage.class);
        PowerMockito.when(CoverageAssembler.getCoverage(coverageDto)).thenReturn(coverage);
        PowerMockito.when(fhirCoverageInterface.create(coverage)).thenReturn("12");
        String r = coverageAntiCorruptionServiceImp.createCoverage(coverageDto);
        Assert.assertNotNull(r);
        Assert.assertEquals("12",r);
    }

    @Test
    public void givenPatientSerListWhenQueryCoverageDtoPaginationByPatientListThenReturnPagination(){
        Coverage coverage = new Coverage();
        CoverageDto coverageDto = PowerMockito.mock(CoverageDto.class);
        Pagination<Coverage> pagination = new Pagination<Coverage>(){{
            setLstObject(new ArrayList());
            setTotalCount(1);
        }};
        pagination.getLstObject().add(coverage);
        pagination.setTotalCount(1);
        PowerMockito.when(CoverageAssembler.getCoverageDto(coverage)).thenReturn(coverageDto);
        PowerMockito.when(fhirCoverageInterface.queryCoveragePaginationByPatientSerList(Matchers.anyList(),Matchers.anyInt(),Matchers.anyInt()))
                .thenReturn(pagination);
        Pagination<CoverageDto> p = coverageAntiCorruptionServiceImp.queryCoverageDtoPaginationByPatientList(Matchers.anyList(),Matchers.anyInt(),Matchers.anyInt());
        Assert.assertNotNull(p);
        Assert.assertTrue(p.getTotalCount() == 1);
    }

    @Test
    public void testQueryByPatientIdThenReturnCoverageDto(){
        CoverageDto coverageDto = PowerMockito.mock(CoverageDto.class);
        Coverage coverage = new Coverage();
        PowerMockito.when(fhirCoverageInterface.queryByPatientId(Matchers.anyString())).thenReturn(coverage);
        PowerMockito.when(CoverageAssembler.getCoverageDto(coverage)).thenReturn(coverageDto);
        CoverageDto dto = coverageAntiCorruptionServiceImp.queryByPatientId("12121");
        Assert.assertTrue(dto.equals(coverageDto));
    }

    @Test
    public void testUpdateCoverage(){
        CoverageDto  coverageDto = new CoverageDto(){{
            setPatientSer("121212");
            setInsuranceTypeCode("code");
            setInsuranceTypeDesc("codeDesc");
        }};
        Coverage coverage = new Coverage();
        PowerMockito.when(fhirCoverageInterface.queryByPatientId(coverageDto.getPatientSer())).thenReturn(coverage);
        PowerMockito.when(fhirCoverageInterface.update(coverage)).thenReturn("1212");
        String key = coverageAntiCorruptionServiceImp.updateCoverage(coverageDto);
        Assert.assertTrue("1212".equals(key));
    }

}
