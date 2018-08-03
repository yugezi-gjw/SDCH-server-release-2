package com.varian.oiscn.encounter.isocenter;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@Slf4j
public class ISOCenterServiceImp {
    private ISOCenterDAO isoCenterDAO;
    private EncounterDAO encounterDAO;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    public ISOCenterServiceImp(UserContext userContext) {
        isoCenterDAO = new ISOCenterDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
        treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
        patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
    }

    public List<ISOPlanTretment> queryPlanTreatmentByPatientSer(Long patientSer,Long encounterId) {
        Connection conn = null;
        List<ISOPlanTretment> rList = new ArrayList<>();
        try {
        	conn = ConnectionPool.getConnection();
            Optional<TreatmentSummaryDto> treatmentSummaryDto = treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(patientSer),String.valueOf(encounterId));
            if (!treatmentSummaryDto.isPresent()) {
                return rList;
            }
            ISOCenter isoCenter = isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,encounterId);
            List<PlanSummaryDto> psdList = treatmentSummaryDto.get().getPlans();
            if (isoCenter != null && isoCenter.getPlanList() != null) {
                List<ISOPlanTretment> list = isoCenter.getPlanList();
                rList.addAll(list);
                this.localAndRtPlanMerge(list, rList, psdList);
            } else {
                for (PlanSummaryDto dto : psdList) {
                    ISOPlanTretment ISOPlanTrement = new ISOPlanTretment();
                    ISOPlanTrement.setPlanId(dto.getPlanSetupId());
                    rList.add(ISOPlanTrement);
                }
            }
            Collections.sort(rList);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return rList;
    }

    private void localAndRtPlanMerge(List<ISOPlanTretment> list, List<ISOPlanTretment> rList, List<PlanSummaryDto> psdList) {
        for (PlanSummaryDto dto : psdList) {
            boolean has = false;
            for (ISOPlanTretment plan : list) {
                if (dto.getPlanSetupId().equals(plan.getPlanId())) {
                    has = true;
                    break;
                }
            }
            if (!has) {
                ISOPlanTretment ISOPlanTrement = new ISOPlanTretment();
                ISOPlanTrement.setPlanId(dto.getPlanSetupId());
                rList.add(ISOPlanTrement);
            }
        }
    }


    public String saveOrUpdateISOCenter(ISOCenter isoCenter) {
        Connection conn = null;
        String id = null;
        try {
        	conn = ConnectionPool.getConnection();
            ISOCenter tmpCenter = isoCenterDAO.selectISOCenterByPatientSer(conn, isoCenter.getPatientSer(),new Long(isoCenter.getEncounterId()));
            if (tmpCenter == null) {
                id = isoCenterDAO.create(conn, isoCenter);
            } else {
                id = tmpCenter.getId();
                isoCenter.setId(id);
                isoCenter.setEncounterId(tmpCenter.getEncounterId());
                isoCenterDAO.update(conn, isoCenter, id);
            }

        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return id;
    }
}
