package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.targetvolume.PlanTargetVolume;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeInfo;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeVO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhp9696 on 2018/3/1.
 */
@Slf4j
public class PlanTargetVolumeServiceImp {

    private EncounterDAO encounterDAO;
    private PlanTargetVolumeDAO planTargetVolumeDAO;

    public PlanTargetVolumeServiceImp(UserContext userContext){
        encounterDAO = new EncounterDAO(userContext);
        planTargetVolumeDAO = new PlanTargetVolumeDAO(userContext);
    }
    /**
     * 保存患者的计划Id和靶区
     * @param ptv
     * @return
     */
    public boolean savePlanTargetVolumeName(PlanTargetVolumeVO ptv){
        Connection conn = null;
        boolean r = false;
        try{
            conn = ConnectionPool.getConnection();
            if(StringUtils.isEmpty(ptv.getEncounterId())){
                ptv.setEncounterId(encounterDAO.queryByPatientSer(conn,Long.parseLong(ptv.getPatientSer())).getId());
            }
            DatabaseUtil.safeSetAutoCommit(conn,false);
            List<PlanTargetVolume> planTargetVolumeList = new ArrayList<>();
            List<PlanTargetVolumeInfo> targetVolumeItemList = ptv.getPlanTargetVolumeList();
            targetVolumeItemList.forEach(item->{
                if(item.getNameList().isEmpty()){
                    planTargetVolumeList.add(new PlanTargetVolume(ptv.getHisId(),ptv.getEncounterId(),Long.parseLong(ptv.getPatientSer()),item.getPlanId(), null));
                } else {
                    item.getNameList().forEach(name->{
                        planTargetVolumeList.add(new PlanTargetVolume(ptv.getHisId(),ptv.getEncounterId(),Long.parseLong(ptv.getPatientSer()),item.getPlanId(),name));
                    });
                }
            });
            this.planTargetVolumeDAO.delete(conn,new Long(ptv.getPatientSer()),new Long(ptv.getEncounterId()));
            r = this.planTargetVolumeDAO.create(conn, planTargetVolumeList);
            if(r) {
                conn.commit();
            }
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(conn);
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return r;
    }

    /**
     * 根据patientSer查询计划对应的
     * @param patientSer
     * @return
     */
    public PlanTargetVolumeVO queryPlanTargetVolumeMappingByPatientSer(Long patientSer,Long encounterId){
        PlanTargetVolumeVO targetVolumeVO = new PlanTargetVolumeVO(null,null,String.valueOf(patientSer),new ArrayList<>());
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            List<PlanTargetVolume> planTargetVolumeList = planTargetVolumeDAO.queryPlanTargetVolumeListByPatientSer(conn,patientSer,encounterId);
            Map<String,PlanTargetVolumeInfo> tmpMap = new LinkedHashMap<>();
            planTargetVolumeList.forEach(planTargetVolume -> {
                PlanTargetVolumeInfo item = tmpMap.get(planTargetVolume.getPlanId());
                if(item == null){
                    List<String> names = new ArrayList<>();
                    names.add(planTargetVolume.getTargetVolumeName());
                    item = new PlanTargetVolumeInfo(planTargetVolume.getPlanId(),names);
                    tmpMap.put(planTargetVolume.getPlanId(),item);
                }else{
                    item.getNameList().add(planTargetVolume.getTargetVolumeName());
                }
                if(StringUtils.isEmpty(targetVolumeVO.getEncounterId())){
                    targetVolumeVO.setEncounterId(planTargetVolume.getEncounterId());
                }
            });
            targetVolumeVO.getPlanTargetVolumeList().addAll(tmpMap.values());
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return targetVolumeVO;
    }

}
