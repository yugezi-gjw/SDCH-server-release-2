package com.varian.oiscn.rt;

import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.resource.AbstractResource;
import com.varian.oiscn.rt.view.PlanCardVO;
import com.varian.oiscn.rt.view.TreatmentSummaryCardVO;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Created by asharma0 on 12-07-2017.
 */
@Path("/rt")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TreatmentSummaryResource extends AbstractResource {
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;

    public TreatmentSummaryResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
        this.treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
    }

    @GET()
    @Path("/treatmentsummary")
    public Response get(@Auth UserContext userContext, @QueryParam("patientSer") Long patientSer,@QueryParam("encounterId") Long encounterId) {
        if (patientSer != null) {
            Optional<TreatmentSummaryDto> treatmentSummaryDto;
            if(encounterId != null){
                treatmentSummaryDto = treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(patientSer),String.valueOf(encounterId));
            }else {
                treatmentSummaryDto = treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(patientSer));
            }
            if (treatmentSummaryDto.isPresent()) {
                return Response.ok(treatmentSummaryDto.get()).build();
            }
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    @GET
    @Path("/treatmentsummarycard")
    public Response getSummaryCard(@Auth UserContext userContext, @QueryParam("patientSer") Long patientSer,@QueryParam("encounterId") Long encounterId) {

        if (patientSer == null) {
            // not valid HisId.
            return Response.noContent().build();
        }
        TreatmentSummaryCardVO treatmentSummaryCardVO = new TreatmentSummaryCardVO();
        Optional<TreatmentSummaryDto> treatmentSummaryDto;
        if(encounterId != null){
            treatmentSummaryDto = treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(patientSer),String.valueOf(encounterId));
        }else{
           treatmentSummaryDto = treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(String.valueOf(patientSer));
        }
        if (treatmentSummaryDto.isPresent()) {
            if(treatmentSummaryDto.get().getLastTreatmentDate() != null){
                treatmentSummaryCardVO.setLastTreatmentDate(DateUtil.formatDate(treatmentSummaryDto.get().getLastTreatmentDate(), DateUtil.DATE_FORMAT));
            }
            List<PlanSummaryDto> planSummaryDtoList = treatmentSummaryDto.get().getPlans();
            if(planSummaryDtoList != null){
                planSummaryDtoList.forEach(planSummaryDto -> {
                    treatmentSummaryCardVO.addPlan(new PlanCardVO(planSummaryDto.getPlanSetupId(), planSummaryDto.getPlanSetupName(), planSummaryDto.getPlannedFractions(), planSummaryDto.getDeliveredFractions()));
                });
            }
        }
        return Response.status(Response.Status.OK).entity(treatmentSummaryCardVO).build();
    }
}