package com.varian.oiscn.base.diagnosis;

/**
 * Created by gbt1220 on 6/19/2017.
 */

import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.base.codesystem.CodeSystemServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.MockDtoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.core.user.UserContext;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DiagnosisResource.class, SystemConfigPool.class})
public class DiagnosisResourceTest {

    private Configuration configuration;

    private Environment environment;

    private CodeSystemServiceImp codeSystemServiceImp;

    private ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp;

    private DiagnosisResource diagnosisResource;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        codeSystemServiceImp = PowerMockito.mock(CodeSystemServiceImp.class);
        PowerMockito.whenNew(CodeSystemServiceImp.class).withNoArguments().thenReturn(codeSystemServiceImp);
        diagnosisResource = new DiagnosisResource(configuration, environment);
        valueSetAntiCorruptionServiceImp = PowerMockito.mock(ValueSetAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(ValueSetAntiCorruptionServiceImp.class).withNoArguments().thenReturn(valueSetAntiCorruptionServiceImp);
    }

    @Test
    public void givenNullKeywordWhenSearchThenReturnBadRequest() {
        Response response = diagnosisResource.search(new UserContext(), "");
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenKeywordWhenSearchThenReturnDiagnosis() {
        String diagnosisScheme = "scheme";
        String keyword = "keyword";
        List<CodeValueDTO> codeValueDTOList = MockDtoUtil.givenCodeValueList();
        PowerMockito.when(configuration.getDiagnosisCodeScheme()).thenReturn(diagnosisScheme);
        String topN = "TOP_N";
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryDiagnosisSearchTopN()).thenReturn(topN);
        PowerMockito.when(codeSystemServiceImp.queryDiagnosis(diagnosisScheme, keyword, topN)).thenReturn(codeValueDTOList);
        Response response = diagnosisResource.search(new UserContext(), keyword);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenWhenSearchBodypartThenReturnOk() throws Exception {
        CodeSystemServiceImp codeSystemServiceImp = PowerMockito.mock(CodeSystemServiceImp.class);
        PowerMockito.whenNew(CodeSystemServiceImp.class).withAnyArguments().thenReturn(codeSystemServiceImp);
        List<BodyPartVO> list = new ArrayList<>();
        list.add(new BodyPartVO("1","\\u767d\\u8840\\u75c5","bxb"));
        PowerMockito.mockStatic(SystemConfigPool.class);
        PowerMockito.when(SystemConfigPool.queryDiagnosisSearchTopN()).thenReturn("");
        PowerMockito.when(codeSystemServiceImp.queryBodyParts(Matchers.anyString(), Matchers.anyString())).thenReturn(list);
        Response response = diagnosisResource.searchBodyPart(new UserContext(),"BXB");
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void givenDXCodeWhenSearchStagingAndFhirQueryStagingSchemeIsNullThenReturnEmpty() {
        String dxScheme = "nonExistedScheme";
        String dxCode = "code";
        PowerMockito.when(configuration.getDiagnosisCodeScheme()).thenReturn(dxScheme);
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryStagingSchemeByDxCodeAndDxScheme(dxCode, dxScheme)).thenReturn(null);
        Response response = diagnosisResource.searchStaging(new UserContext(), dxCode);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is(new StagingVO()));
    }

    @Test
    public void givenDXCodeWhenSearchStagingAndStagingSchemeNotFoundThenReturnEmpty() {
        String dxScheme = "scheme";
        String dxCode = "code";
        String stagingScheme = "stagingScheme";
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        PowerMockito.when(configuration.getDiagnosisCodeScheme()).thenReturn(dxScheme);
        PowerMockito.when(configuration.getStagingCodeScheme()).thenReturn(stagingScheme);
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryStagingSchemeByDxCodeAndDxScheme(dxCode, dxScheme)).thenReturn(codeSystem);
        Response response = diagnosisResource.searchStaging(new UserContext(), dxCode);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is(new StagingVO()));
    }

    @Test
    public void givenDXCodeWhenSearchStagingAndStagingSchemeFoundThenReturnStaging() {
        String dxScheme = "scheme";
        String dxCode = "code";
        String stagingScheme = "stagingScheme";
        CodeSystem stagingSchemeCodeSystem = givenStagingSchemeCodeSystem();
        PowerMockito.when(configuration.getDiagnosisCodeScheme()).thenReturn(dxScheme);
        PowerMockito.when(configuration.getStagingCodeScheme()).thenReturn(stagingScheme);
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryStagingSchemeByDxCodeAndDxScheme(dxCode, dxScheme)).thenReturn(stagingSchemeCodeSystem);
        CodeSystem stagingCodeSystem = givenStagingCodeSystem();
        PowerMockito.when(valueSetAntiCorruptionServiceImp.queryStagingValueByDxCodeAndDxSchemeAndStagingScheme(dxCode, dxScheme, "schemeCode")).thenReturn(stagingCodeSystem);
        Response response = diagnosisResource.searchStaging(new UserContext(), dxCode);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void givenDXCodeTCodeMCodeNCodeWhenCalculateStagingAndDXCodeIsEmptyThenReturnBadRequest() {
        Response response = diagnosisResource.calculate(new UserContext(), "", "tcode", "ncode", "mcode");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenDXCodeTCodeMCodeNCodeWhenCalculateStagingAndTCodeIsEmptyThenReturnBadRequest() {
        Response response = diagnosisResource.calculate(new UserContext(), "dxCode", "", "ncode", "mcode");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenDXCodeTCodeMCodeNCodeWhenCalculateStagingAndNCodeIsEmptyThenReturnBadRequest() {
        Response response = diagnosisResource.calculate(new UserContext(), "dxCode", "tcode", "", "mcode");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenDXCodeTCodeMCodeNCodeWhenCalculateStagingAndMCodeIsEmptyThenReturnBadRequest() {
        Response response = diagnosisResource.calculate(new UserContext(), "dxCode", "tcode", "ncode", "");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void givenDXCodeTCodeMCodeNCodeWhenCalculateStagingThenReturnStaging() throws Exception {
        DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp = PowerMockito.mock(DiagnosisAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DiagnosisAntiCorruptionServiceImp.class).withNoArguments().thenReturn(diagnosisAntiCorruptionServiceImp);
        String dxScheme = "scheme";
        String stagingScheme = "stagingScheme";
        String dxcode = "dxcode";
        String tcode = "tcode";
        String ncode = "ncode";
        String mcode = "mcode";
        String staging = "staging";
        PowerMockito.when(configuration.getDiagnosisCodeScheme()).thenReturn(dxScheme);
        PowerMockito.when(configuration.getStagingCodeScheme()).thenReturn(stagingScheme);
        PowerMockito.when(diagnosisAntiCorruptionServiceImp.calculateStageSummary(dxcode, dxScheme, stagingScheme, tcode, ncode, mcode)).thenReturn(staging);
        Response response = diagnosisResource.calculate(new UserContext(), dxcode, tcode, ncode, mcode);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        assertThat(response.getEntity(), is(staging));
    }

    private CodeSystem givenStagingSchemeCodeSystem() {
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        codeSystem.addCodeValue(new CodeValue("schemeCode", "stagingScheme"));
        return codeSystem;
    }

    private CodeSystem givenStagingCodeSystem() {
        CodeSystem codeSystem = MockDtoUtil.givenCodeSystem();
        codeSystem.addCodeValue(new CodeValue("T/T0", "Tcode"));
        codeSystem.addCodeValue(new CodeValue("N/N0", "Ncode"));
        codeSystem.addCodeValue(new CodeValue("M/M0", "Mcode"));
        return codeSystem;
    }
}
