package com.varian.oiscn.patient.registry;

import com.varian.oiscn.anticorruption.exception.FhirCreatePatientException;
import com.varian.oiscn.base.util.PinyinUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.Patient;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.patient.view.PatientRegistrationVO;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

@Path("/patient-registry")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PatientRegistryResource extends AbstractResource {

    public PatientRegistryResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }

    @POST
    @Path("/")
    public Response registry(@Auth UserContext userContext, PatientRegistrationVO vo) {
        Patient patient = vo.getPatient();
        Encounter encounter = vo.getEncounter();

        if (isBlank(vo.getScenarioFlag())
                || patient == null
                || encounter == null
                || !patient.verifyMandatoryDataAndLength()
                || !encounter.verifyMandatoryDataAndLength()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(vo).build();
        }

        IPatientRegistry registry = RegistryFactory.getRegistry(vo);
        if (registry == null) {
            log.error("{} is invalid scenario.", vo.getScenarioFlag());
            return Response.status(Response.Status.BAD_REQUEST).entity(vo).build();
        }

        RegistryVerifyStatusEnum verifyResult = registry.verifyRegistry();
        switch (verifyResult) {
            case DUPLICATE_HIS:
                final Map<String, String> hisErrorMap = new HashMap<>();
                // xx 已经存在
                hisErrorMap.put("errorItemId", "hisId");
                return Response.status(Response.Status.BAD_REQUEST).entity(hisErrorMap).build();
            case DUPLICATE_VID:
                final Map<String, String> vidErrorMap = new HashMap<>();
                // xx 已经存在
                vidErrorMap.put("errorItemId", "ariaId");
                return Response.status(Response.Status.BAD_REQUEST).entity(vidErrorMap).build();
            case INVALID_DIAGNOSIS_DATE:
                final Map<String, String> errorMap = new HashMap<>();
                // diagnosisDate 诊断时间晚于系统时间
                errorMap.put("errorItemId", "diagnosisDate");
                return Response.status(Response.Status.BAD_REQUEST).entity(errorMap).build();
        }

        patient.setChineseName(StringUtils.trimToEmpty(patient.getChineseName()));
        if (StringUtils.isEmpty(patient.getPinyin())) {
            patient.setPinyin(PinyinUtil.chineseName2PinyinAcronyms(patient.getChineseName()));
        }

        try {
            Long patientSer = registry.saveOrUpdate(configuration, userContext);
            if (patientSer == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(vo).build();
            }
            patient.setPatientSer(patientSer);
        } catch (Exception e) {
            if (e instanceof FhirCreatePatientException) {
                FhirCreatePatientException fe = (FhirCreatePatientException) e;
                String errorItemId = fe.getErrorItemId();
                final Map<String, String> errorMap = new HashMap<>();
                // 身份证号重复
                errorMap.put("errorItemId", errorItemId);
                return Response.status(Response.Status.BAD_REQUEST).entity(errorMap).build();
            }
        }
        return Response.status(Response.Status.OK).entity(vo).build();
    }

    @Path("/search")
    @GET
    public Response search(@Auth UserContext userContext,
                           @QueryParam("hisId") String hisId) {
        PatientRegistrationVO result = new PatientRegistrationVO();
        if (isBlank(hisId)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(result).build();
        }
        AbstractRegistryScenario registryScenario = RegistryFactory.getScenario(hisId, configuration, userContext);
        result = registryScenario.getPatientRegistrationVO();
        return Response.ok(result).build();
    }
}
