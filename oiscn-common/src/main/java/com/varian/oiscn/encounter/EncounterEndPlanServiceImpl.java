package com.varian.oiscn.encounter;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.EncounterEndPlan;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/23.
 */
@Slf4j
public class EncounterEndPlanServiceImpl {
    private EncounterEndPlanDAO encounterEndPlanDAO;

    public EncounterEndPlanServiceImpl(){
        encounterEndPlanDAO = new EncounterEndPlanDAO();
    }

    public List<EncounterEndPlan> queryEncounterEndPlanListByPatientSer(String patientSer){
        Connection conn = null;
        List<EncounterEndPlan> list;
        try {
            conn = ConnectionPool.getConnection();
            list = encounterEndPlanDAO.selectEncounterEndPlanListByPatientSer(conn,new Long(patientSer));
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            list = new ArrayList<>();
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return list;
    }
}
