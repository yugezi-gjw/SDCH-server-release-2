package com.varian.oiscn.encounter;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bhp9696 on 2017/11/9.
 */
@Slf4j
public class EncounterCarePathServiceImpl {

    private EncounterCarePathDAO encounterCarePathDAO;

    public EncounterCarePathServiceImpl(UserContext userContext){
        encounterCarePathDAO = new EncounterCarePathDAO(userContext);
    }

    /**
     *
     * @param encounterCarePath
     * @return
     */
    public boolean addEncounterCarePath(EncounterCarePath encounterCarePath){
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return encounterCarePathDAO.addCarePathInstanceId(connection,encounterCarePath) > 0;
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return false;

    }


    /**
     *
     * @param patientSer
     * @return
     */
    public PatientEncounterCarePath queryEncounterCarePathByPatientSer(String patientSer){
        List<PatientEncounterCarePath> list = this.queryEncounterCarePathList(Arrays.asList(patientSer));
        return list.isEmpty()?null:list.get(0);
    }

    /**
     *
     * @param patientSerList
     * @return
     */
    public List<PatientEncounterCarePath> queryEncounterCarePathList(List<String> patientSerList){
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return encounterCarePathDAO.selectCarePathInstanceIdList(connection,patientSerList);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return new ArrayList<>();
    }

    /**
     * Query current HisId's CarePath Instance Count.<br>
     *
     * @param patientSer HIS ID
     * @return count of carepath of current encounter
     */
    public int countCarePathByPatientSer(Long patientSer) {
        int count = 0;
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            count = encounterCarePathDAO.countCarePathByPatientSer(con, patientSer);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            count = 0;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return count;
    }
}
