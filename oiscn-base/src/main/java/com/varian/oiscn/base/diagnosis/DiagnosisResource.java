package com.varian.oiscn.base.diagnosis;

import com.varian.oiscn.anticorruption.resourceimps.DiagnosisAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.ValueSetAntiCorruptionServiceImp;
import com.varian.oiscn.base.codesystem.CodeSystemServiceImp;
import com.varian.oiscn.base.codesystem.CodeValueAssembler;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by gbt1220 on 6/19/2017.
 */
@Slf4j
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiagnosisResource extends AbstractResource {

    private static final String T = "T";
    private static final String N = "N";
    private static final String M = "M";

    private CodeSystemServiceImp codeSystemServiceImp;

    public DiagnosisResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        codeSystemServiceImp = new CodeSystemServiceImp();
    }

    @Path("/diagnosis/search")
    @GET
    public Response search(@Auth UserContext userContext,
                           @QueryParam("keyword") String keyword) {
        final List<DiagnosisPair> result = new ArrayList<>();
        if (StringUtils.isBlank(keyword)) {
            log.error("The keyword is empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        String topN = SystemConfigPool.queryDiagnosisSearchTopN();
        String diagnosisScheme = configuration.getDiagnosisCodeScheme();
        List<CodeValueDTO> diagnosisList = codeSystemServiceImp.queryDiagnosis(diagnosisScheme, keyword, topN);
        diagnosisList.forEach(codeValue -> {
            result.add(new DiagnosisPair(codeValue.getValue(), CodeValueAssembler.htmlDescape(codeValue.getDesc())));
        });
        return Response.status(Response.Status.OK).entity(result).build();
    }

    @Path("/staging/search")
    @GET
    public Response searchStaging(@Auth UserContext userContext,
                                  @QueryParam("dxCode") String dxCode) {
        StagingVO stagingVO = new StagingVO();
        Response response = Response.status(Response.Status.OK).entity(stagingVO).build();
        if (StringUtils.isBlank(dxCode)) {
            log.warn("dxCode is blank in request !");
            return response;
        }
        
        String stagingScheme = configuration.getStagingCodeScheme();
        String dxScheme = configuration.getDiagnosisCodeScheme();
        ValueSetAntiCorruptionServiceImp valueSetAntiCorruptionServiceImp = new ValueSetAntiCorruptionServiceImp();
        CodeSystem stagingSchemeSystem = valueSetAntiCorruptionServiceImp.queryStagingSchemeByDxCodeAndDxScheme(dxCode, dxScheme);
        if (stagingSchemeSystem == null || stagingSchemeSystem.getCodeValues() == null) {
            log.error("Can't find the staging scheme, please check the config in ARIA.");
            return response;
        }
        Optional<CodeValue> stagingSchemeOptional = stagingSchemeSystem.getCodeValues().stream().filter(codeValue -> StringUtils.equals(stagingScheme, codeValue.getDesc())).findAny();
        if (!stagingSchemeOptional.isPresent()) {
            log.error("Can't find the staging scheme, please check staging scheme config in local.yam.");
            return response;
        }
        CodeSystem stagingCodeSystem = valueSetAntiCorruptionServiceImp.queryStagingValueByDxCodeAndDxSchemeAndStagingScheme(dxCode, dxScheme, stagingSchemeOptional.get().getCode());
        if (stagingCodeSystem != null) {
            stagingCodeSystem.getCodeValues().stream().forEach(codeValue -> {
                //the code value like this : T/T0 or N/N1 or M/M1
                String[] splitCodeValue = StringUtils.split(codeValue.getCode(), "/");
                if (StringUtils.equals(splitCodeValue[0], T)) {
                    stagingVO.addTCode(new KeyValuePair(splitCodeValue[1], splitCodeValue[1]));
                } else if (StringUtils.equals(splitCodeValue[0], N)) {
                    stagingVO.addNCode(new KeyValuePair(splitCodeValue[1], splitCodeValue[1]));
                } else if (StringUtils.equals(splitCodeValue[0], M)) {
                    stagingVO.addMCode(new KeyValuePair(splitCodeValue[1], splitCodeValue[1]));
                }
            });
        }
        return Response.status(Response.Status.OK).entity(stagingVO).build();
    }

    @Path("/staging/calculate")
    @GET
    public Response calculate(@Auth UserContext userContext,
                              @QueryParam("dxCode") String dxCode,
                              @QueryParam("tCode") String tCode,
                              @QueryParam("nCode") String nCode,
                              @QueryParam("mCode") String mCode) {
        if (isEmpty(dxCode) || isEmpty(tCode) || isEmpty(nCode) || isEmpty(mCode)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        DiagnosisAntiCorruptionServiceImp diagnosisAntiCorruptionServiceImp = new DiagnosisAntiCorruptionServiceImp();
        String dxScheme = configuration.getDiagnosisCodeScheme();
        String stagingScheme = configuration.getStagingCodeScheme();
        String staging = diagnosisAntiCorruptionServiceImp.calculateStageSummary(dxCode, dxScheme, stagingScheme, tCode, nCode, mCode);
        return Response.status(Response.Status.OK).entity(staging).build();
    }

    @Path("/bodypart/search")
    @GET
    public Response searchBodyPart(@Auth UserContext userContext,@QueryParam("keyword") String condition) {
        CodeSystemServiceImp codeSystemServiceImp = new CodeSystemServiceImp();
        String topN = SystemConfigPool.queryDiagnosisSearchTopN();
        List<BodyPartVO> bodyParts = codeSystemServiceImp.queryBodyParts(condition, topN);
        bodyParts.forEach(diagnosisPairVO ->diagnosisPairVO.setDesc(CodeValueAssembler.htmlDescape(diagnosisPairVO.getDesc())));
        return Response.status(Response.Status.OK).entity(bodyParts).build();
    }
}
