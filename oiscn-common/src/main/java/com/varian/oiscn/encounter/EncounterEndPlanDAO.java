package com.varian.oiscn.encounter;

import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.core.encounter.StatusEnum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by bhp9696 on 2018/3/23.
 */
public class EncounterEndPlanDAO {

    public boolean batchCreate(Connection conn,List<EncounterEndPlan> encounterEndPlanList) throws SQLException {
        boolean ok = false;
        String sql = "insert into EncounterEndPlan (encounterId,planSetupId,planCreatedDt) values (?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for(EncounterEndPlan encounterEndPlan : encounterEndPlanList) {
            ps.setLong(1, encounterEndPlan.getEncounterId());
            ps.setString(2, encounterEndPlan.getPlanSetupId());
            ps.setTimestamp(3, new Timestamp(encounterEndPlan.getPlanCreatedDt().getTime()));
            ps.addBatch();
        }
        if(!encounterEndPlanList.isEmpty()){
            ok = ps.executeBatch().length > 0;
        }
        return ok;
    }
    public List<EncounterEndPlan> selectEncounterEndPlanListByPatientSer(Connection conn,Long patientSer) throws SQLException {
        List<EncounterEndPlan> list = new ArrayList<>();
        String sql = "SELECT ep.encounterId,ep.planSetupId,ep.planCreatedDt from EncounterEndPlan ep,Encounter e where e.id=ep.encounterId and e.patientSer=? and e.status = ? order by ep.encounterId";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1,patientSer);
        ps.setString(2, StatusEnum.FINISHED.name());
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new EncounterEndPlan(){{
                setEncounterId(rs.getLong("encounterId"));
                setPlanSetupId(rs.getString("planSetupId"));
                setPlanCreatedDt(new Date(rs.getTimestamp("planCreatedDt").getTime()));
            }});
        }
        return list;
    }
}
