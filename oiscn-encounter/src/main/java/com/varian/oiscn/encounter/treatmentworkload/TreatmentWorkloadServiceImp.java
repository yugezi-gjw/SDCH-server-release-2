package com.varian.oiscn.encounter.treatmentworkload;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by BHP9696 on 2017/8/21.
 */
@Slf4j
public class TreatmentWorkloadServiceImp {
    private UserContext userContext;
    private TreatmentWorkloadDAO treatmentWorkloadDAO;
    private EncounterDAO encounterDAO;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    public TreatmentWorkloadServiceImp(UserContext userContext) {
        this.userContext = userContext;
        treatmentWorkloadDAO = new TreatmentWorkloadDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
        treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
        patientAntiCorruptionServiceImp = new PatientAntiCorruptionServiceImp();
    }


    /**
     * 技师进入到记录单时调用的接口，初始化治疗记录
     *
     * @param patientSer
     * @return
     */
    public TreatmentWorkloadVO queryTreatmentWorkloadByPatientSer(Long patientSer,Long encounterId) {
        Connection connection = null;
        TreatmentWorkloadVO treatmentWorkload = new TreatmentWorkloadVO();
        try {
            connection = ConnectionPool.getConnection();
            treatmentWorkload.setPlanList(new ArrayList<>());
            treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection,patientSer,encounterId).forEach(workloadPlan ->
                    treatmentWorkload.getPlanList().add(new WorkloadPlanVO() {{
                        setNum(workloadPlan.getDeliveredFractions());
                        setPlanId(workloadPlan.getPlanId());
                        setSelected(workloadPlan.getSelected() == 1);
                    }})
            );
            Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(patientSer),String.valueOf(encounterId));
            treatmentWorkload.setEncounterId(String.valueOf(encounterId));

            treatmentWorkload.setSign(new ArrayList<>());
            treatmentWorkload.setWorker(new ArrayList<>());

            if (treatmentSummaryDtoOptional != null && treatmentSummaryDtoOptional.isPresent()
                    && treatmentSummaryDtoOptional.get().getPlans() != null
                    && !treatmentSummaryDtoOptional.get().getPlans().isEmpty()) {
                List<PlanSummaryDto> need2AddList = new ArrayList<>();
                for (PlanSummaryDto planSummaryDto : treatmentSummaryDtoOptional.get().getPlans()) {
                    boolean has = false;
                    for (WorkloadPlanVO workloadPlan : treatmentWorkload.getPlanList()) {
                        if (planSummaryDto.getPlanSetupId().equals(workloadPlan.getPlanId())) {
                            has = true;
                            break;
                        }
                    }
                    if (!has) {
                        need2AddList.add(planSummaryDto);
                    }
                }

                for (PlanSummaryDto planSummaryDto : need2AddList) {
                    treatmentWorkload.getPlanList().add(new WorkloadPlanVO() {{
                        setPlanId(planSummaryDto.getPlanSetupId());
                        setNum(0);
                    }});
                }
                for (PlanSummaryDto planSummaryDto : treatmentSummaryDtoOptional.get().getPlans()) {
                    treatmentWorkload.getPlanList().forEach(workloadPlanVO -> {
                        if(workloadPlanVO.getPlanId().equals(planSummaryDto.getPlanSetupId())){
                            workloadPlanVO.setDeliveredFractions(planSummaryDto.getDeliveredFractions());
                            workloadPlanVO.setPlannedFractions(planSummaryDto.getPlannedFractions());
                            workloadPlanVO.setDeliveredDose(planSummaryDto.getDeliveredDose());
                            workloadPlanVO.setPlannedDose(planSummaryDto.getPlannedDose());
                        }
                    });
                }
            }
            Collections.sort(treatmentWorkload.getPlanList());
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return treatmentWorkload;
    }

    public List<TreatmentWorkloadVO> queryTreatmentWorkloadListByPatientSer(Long patientSer,Long encounterId) {
        Connection conn = null;
        List<TreatmentWorkloadVO> list = new ArrayList<>();
        try {
            conn = ConnectionPool.getConnection();
            treatmentWorkloadDAO.queryTreatmentWorkloadListByPatientSer(conn, patientSer,encounterId).forEach(treatmentWorkload -> {
                list.add(entity2TreatmentWorkloadVO(treatmentWorkload));
            });
            Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(String.valueOf(patientSer),String.valueOf(encounterId));
            if(!list.isEmpty()) {
//            处理中途增加的计划
                if (treatmentSummaryDtoOptional.isPresent() && treatmentSummaryDtoOptional.get().getPlans() != null
                        && !treatmentSummaryDtoOptional.get().getPlans().isEmpty()) {
                    List<PlanSummaryDto> planSummaryDtoList = treatmentSummaryDtoOptional.get().getPlans();
                    list.forEach(treatmentWorkloadVO -> {
//                        循环每个treatmentWorkloadVO
                        if (treatmentWorkloadVO.getPlanList().size() < planSummaryDtoList.size()) {
//                      需要增加计划
                            List<WorkloadPlanVO> needAddList = new ArrayList<>();
                            planSummaryDtoList.forEach(planSummaryDto -> {
                                boolean have = false;
                                for (WorkloadPlanVO planVO : treatmentWorkloadVO.getPlanList()) {
                                    if (planVO.getPlanId().equals(planSummaryDto.getPlanSetupId())) {
                                        have = true;
                                        break;
                                    }
                                }
                                if (!have) {
                                    needAddList.add(new WorkloadPlanVO() {{
                                        setNum(0);
                                        setPlanId(planSummaryDto.getPlanSetupId());
                                    }});
                                }
                            });
                            treatmentWorkloadVO.getPlanList().addAll(needAddList);
                        }
                        Collections.sort(treatmentWorkloadVO.getPlanList());
                    });
                }
            }else{
                if (treatmentSummaryDtoOptional.isPresent() && treatmentSummaryDtoOptional.get().getPlans() != null
                        && !treatmentSummaryDtoOptional.get().getPlans().isEmpty()) {
                    List<PlanSummaryDto> planSummaryDtoList = treatmentSummaryDtoOptional.get().getPlans();
                    TreatmentWorkloadVO treatmentWorkloadVO  = new TreatmentWorkloadVO();
                    treatmentWorkloadVO.setHisId(null);
                    treatmentWorkloadVO.setEncounterId(String.valueOf(encounterId));
                    treatmentWorkloadVO.setPlanList(new ArrayList<>());
                    planSummaryDtoList.forEach(planSummaryDto ->treatmentWorkloadVO.getPlanList().add(new WorkloadPlanVO(){{
                            setPlanId(planSummaryDto.getPlanSetupId());
                        }})
                   );
                    Collections.sort(treatmentWorkloadVO.getPlanList());
                    list.add(treatmentWorkloadVO);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return list;
    }

    public boolean createTreatmentWorkLoad(TreatmentWorkloadVO treatmentWorkloadVO) {
        Connection conn = null;
        boolean ok = false;
        try {
            conn = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(conn, false);
            TreatmentWorkload treatmentWorkload = treatmentWorkloadVO2Entity(treatmentWorkloadVO);
            if (StringUtils.isEmpty(treatmentWorkload.getEncounterId())) {
                treatmentWorkload.setEncounterId(encounterDAO.queryByPatientSer(conn, treatmentWorkload.getPatientSer()).getId());
            }
            String id = treatmentWorkloadDAO.create(conn, treatmentWorkloadVO2Entity(treatmentWorkloadVO));
            if (StringUtils.isNotEmpty(id)) {
                ok = true;
            }
            conn.commit();
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(conn);
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return ok;
    }

    private TreatmentWorkload treatmentWorkloadVO2Entity(TreatmentWorkloadVO treatmentWorkloadVO) {
        TreatmentWorkload treatmentWorkload = new TreatmentWorkload();
        try {
            treatmentWorkload.setTreatmentDate(DateUtil.parse(treatmentWorkloadVO.getTreatmentDate()));
        } catch (ParseException e) {
            log.error("treatmentWorkloadVO2Entity ParseException: {}", e.getMessage());
        }
        treatmentWorkload.setEncounterId(treatmentWorkloadVO.getEncounterId());
        treatmentWorkload.setHisId(treatmentWorkloadVO.getHisId());
        treatmentWorkload.setPatientSer(treatmentWorkloadVO.getPatientSer());
        treatmentWorkload.setWorkloadPlans(new ArrayList<>());
        treatmentWorkload.setWorkloadSignatures(new ArrayList<>());
        treatmentWorkload.setWorkloadWorkers(new ArrayList<>());
        if (treatmentWorkloadVO.getPlanList() != null) {
            treatmentWorkloadVO.getPlanList().forEach(workloadPlanVO -> {
                treatmentWorkload.getWorkloadPlans().add(new WorkloadPlan() {{
                    setPlanId(workloadPlanVO.getPlanId());
                    setComment(workloadPlanVO.getComments());
                    setDeliveredFractions(workloadPlanVO.getNum());
                    setSelected(workloadPlanVO.isSelected()?(byte)1:(byte)0);
                }});
            });
        }
        if (treatmentWorkloadVO.getSign() != null) {
            Calendar calendar = Calendar.getInstance();
            treatmentWorkloadVO.getSign().forEach(workloadSignatureVO -> {
                treatmentWorkload.getWorkloadSignatures().add(new WorkloadSignature() {{
                    try {
                        setSignDate(new Date(DateUtil.parse(workloadSignatureVO.getTime()).getTime()+calendar.getTimeZone().getRawOffset()));
                    } catch (ParseException e) {
                        log.error("treatmentWorkloadVO2Entity setSignDate ParseException: {}", e.getMessage());
                    }
                    setResourceName(workloadSignatureVO.getName());
                    setSignType(workloadSignatureVO.getType());
                }});
            });
        }
        if (treatmentWorkloadVO.getWorker() != null) {
            final int[] order = {0};
            treatmentWorkloadVO.getWorker().forEach(workloadWorkerVO -> {
                treatmentWorkload.getWorkloadWorkers().add(new WorkloadWorker() {{
                    setWorkerName(workloadWorkerVO);
                    setOrderNum(++order[0]);
                }});
            });
        }

        return treatmentWorkload;

    }

    private TreatmentWorkloadVO entity2TreatmentWorkloadVO(TreatmentWorkload treatmentWorkload) {
        TreatmentWorkloadVO treatmentWorkloadVO = new TreatmentWorkloadVO();
        treatmentWorkloadVO.setTreatmentDate(treatmentWorkload.getTreatmentDate().getTime() + "");
        treatmentWorkloadVO.setEncounterId(treatmentWorkload.getEncounterId());
        treatmentWorkloadVO.setHisId(treatmentWorkload.getHisId());
        treatmentWorkloadVO.setPatientSer(treatmentWorkload.getPatientSer());
        treatmentWorkloadVO.setPlanList(new ArrayList<>());
        treatmentWorkloadVO.setSign(new ArrayList<>());
        treatmentWorkloadVO.setWorker(new ArrayList<>());
        if (treatmentWorkload.getWorkloadPlans() != null) {
            treatmentWorkload.getWorkloadPlans().forEach(workloadPlan -> {
                treatmentWorkloadVO.getPlanList().add(new WorkloadPlanVO() {{
                    setPlanId(workloadPlan.getPlanId());
                    setComments(workloadPlan.getComment());
                    setNum(workloadPlan.getDeliveredFractions());
                }});
            });
        }
        if (treatmentWorkload.getWorkloadSignatures() != null) {
            treatmentWorkload.getWorkloadSignatures().forEach(workloadSignature -> {
                treatmentWorkloadVO.getSign().add(new WorkloadSignatureVO() {{
                    setTime(workloadSignature.getSignDate().getTime() + "");
                    setName(workloadSignature.getResourceName());
                    setType(workloadSignature.getSignType());
                }});
            });
        }
        if (treatmentWorkload.getWorkloadWorkers() != null) {
            treatmentWorkload.getWorkloadWorkers().forEach(workloadWorker -> {
                treatmentWorkloadVO.getWorker().add(workloadWorker.getWorkerName());
            });
        }
        return treatmentWorkloadVO;

    }
}
