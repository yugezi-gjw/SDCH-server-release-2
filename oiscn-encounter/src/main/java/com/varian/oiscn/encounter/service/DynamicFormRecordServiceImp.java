package com.varian.oiscn.encounter.service;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.DynamicFormRecordDAO;
import com.varian.oiscn.encounter.dynamicform.DynamicFormRecord;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class DynamicFormRecordServiceImp {
    private DynamicFormRecordDAO dynamicFormRecordDAO;
    public DynamicFormRecordServiceImp(UserContext userContext){
        dynamicFormRecordDAO = new DynamicFormRecordDAO(userContext);
    }

    public String create(DynamicFormRecord dynamicFormRecord){
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            return dynamicFormRecordDAO.create(con, dynamicFormRecord);
        }catch (SQLException e) {
            log.error("create dynamicFormRecordDAO SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public List<DynamicFormRecord> queryDynamicFormRecordInfoByEncounterId(Long patientSer, Long encounterId){
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            return dynamicFormRecordDAO.queryDynamicFormRecordInfoByEncounterId(con, patientSer, encounterId);
        }catch (SQLException e) {
            log.error("DynamicFormRecordServiceImp queryDynamicFormRecordInfoByEncounterId SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public Map<String, String> queryDynamicFormRecordInfoById(String id){
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            return dynamicFormRecordDAO.queryDynamicFormRecordInfoById(con, id);
        }catch (SQLException e) {
            log.error("DynamicFormRecordServiceImp queryDynamicFormRecordInfoById SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }
}
