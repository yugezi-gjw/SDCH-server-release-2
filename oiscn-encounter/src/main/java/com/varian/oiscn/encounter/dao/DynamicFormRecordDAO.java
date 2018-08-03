package com.varian.oiscn.encounter.dao;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dynamicform.DynamicFormRecord;
import com.varian.oiscn.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DynamicFormRecordDAO extends AbstractDAO<DynamicFormRecord>{

    public DynamicFormRecordDAO(UserContext userContext){
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "DynamicFormRecord";
    }

    @Override
    protected String getJsonbColumnName() {
        return "dynamicFormRecordInfo";
    }

    @Override
    public String create(Connection con, DynamicFormRecord dynamicFormRecord) throws SQLException{
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String createId;
        try{
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            StringBuilder createSql = new StringBuilder("INSERT INTO ");
            createSql.append(getTableName()).append("(createdUser,createdDate,hisId,encounterId,carePathInstanceId,templateId,");
            createSql.append(getJsonbColumnName()).append(", templateInfo ) VALUES(?,?,?,?,?,?,?,?)");
            ps = con.prepareStatement(createSql.toString(), RETURN_GENERATED_KEYS);
            int idx = 1;
            ps.setString(idx++, userContext.getName());
            ps.setTimestamp(idx++, curTime);
            ps.setString(idx++, dynamicFormRecord.getHisId());
            ps.setLong(idx++, dynamicFormRecord.getEncounterId());
            ps.setString(idx++, dynamicFormRecord.getCarePathInstanceId());
            ps.setString(idx++, dynamicFormRecord.getTemplateId());
            String json = new JsonSerializer<List>().getJson(dynamicFormRecord.getDynamicFormRecordInfo());
            ps.setString(idx++, json);
            ps.setString(idx++, dynamicFormRecord.getTemplateInfo());

            ps.executeUpdate();

            resultSet = ps.getGeneratedKeys();
            resultSet.next();
            createId = resultSet.getString(1);
        }finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return createId;
    }

    public List<DynamicFormRecord> queryDynamicFormRecordInfoByEncounterId(Connection con, Long patientSer, Long encounterId) throws SQLException{
        List<DynamicFormRecord> dynamicFormRecordList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            String sql = "SELECT id, encounterId, carePathInstanceId, templateId, createdDate FROM " + getTableName() + " WHERE encounterId = ? order by id";
            ps = con.prepareStatement(sql);
            ps.setLong(1,encounterId);
            Map<String, Integer> encounterIdCpInstanceIdTemplateIdAndListIndexMap = new HashMap<>();
            resultSet = ps.executeQuery();
            while(resultSet.next()){
                String encounterIdCpInstanceIdTemplateId = resultSet.getInt("encounterId") + "_" + resultSet.getString("carePathInstanceId") + "_" + resultSet.getString("templateId");
                if(!encounterIdCpInstanceIdTemplateIdAndListIndexMap.containsKey(encounterIdCpInstanceIdTemplateId)){
                    DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
                    dynamicFormRecord.setId(resultSet.getString("id"));
                    dynamicFormRecord.setPatientSer(patientSer);
                    dynamicFormRecord.setEncounterId(encounterId);
                    dynamicFormRecord.setCarePathInstanceId(resultSet.getString("carePathInstanceId"));
                    dynamicFormRecord.setTemplateId(resultSet.getString("templateId"));
                    dynamicFormRecord.setCreateDate(resultSet.getTimestamp("createdDate"));
                    dynamicFormRecordList.add(dynamicFormRecord);
                    encounterIdCpInstanceIdTemplateIdAndListIndexMap.put(encounterIdCpInstanceIdTemplateId, dynamicFormRecordList.size() - 1);
                } else {
                    int index = encounterIdCpInstanceIdTemplateIdAndListIndexMap.get(encounterIdCpInstanceIdTemplateId);
                    DynamicFormRecord dynamicFormRecord = dynamicFormRecordList.get(index);
                    dynamicFormRecord.setId(resultSet.getString("id"));
                    dynamicFormRecord.setCreateDate(resultSet.getTimestamp("createdDate"));
                }
            }
            return dynamicFormRecordList;
        }finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
    }

    public Map<String, String> queryDynamicFormRecordInfoById(Connection con, String id) throws SQLException{
        Map<String, String> recordInfoAndTemplateInfoMap = new HashMap<>();
        recordInfoAndTemplateInfoMap.put("recordInfo", "");
        recordInfoAndTemplateInfoMap.put("templateInfo", "");
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            String sql = "select dynamicFormRecordInfo, templateInfo from " + getTableName() + " where id = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, id);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                recordInfoAndTemplateInfoMap.put("recordInfo", resultSet.getString(1));
                recordInfoAndTemplateInfoMap.put("templateInfo", resultSet.getString(2));
            }
            return recordInfoAndTemplateInfoMap;
        }finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
    }

    public DynamicFormRecord queryExistingRecordByCarePathInstanceIdAndTemplateId(Connection con, String carePathInstanceId, String templateId) throws SQLException{
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try{
            String sql = "select * from " + getTableName() + " where carePathInstanceId = ? and templateId = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, carePathInstanceId);
            ps.setString(2, templateId);
            resultSet = ps.executeQuery();
            if(resultSet.next()){
                DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
                dynamicFormRecord.setId(String.valueOf(resultSet.getInt("id")));
                dynamicFormRecord.setHisId(resultSet.getString("hisId"));
                dynamicFormRecord.setEncounterId(resultSet.getLong("encounterId"));
                dynamicFormRecord.setCarePathInstanceId(resultSet.getString("carePathInstanceId"));
                dynamicFormRecord.setTemplateId(resultSet.getString("templateId"));
                dynamicFormRecord.setCreateDate(resultSet.getTimestamp("createdDate"));
                dynamicFormRecord.setCreatedUser(resultSet.getString("createdUser"));
                return dynamicFormRecord;
            } else {
                return null;
            }
        }finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
    }

    public int update(Connection con, DynamicFormRecord dynamicFormRecord) throws SQLException{
        PreparedStatement ps = null;
        int affectedRows = 0;
        try{
            String sql = "update " + getTableName() + " set createdDate = ?, dynamicFormRecordInfo = ? where id = ?";
            ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(dynamicFormRecord.getCreateDate().getTime()));
            String json = new JsonSerializer<List>().getJson(dynamicFormRecord.getDynamicFormRecordInfo());
            ps.setString(2, json);
            ps.setInt(3, Integer.parseInt(dynamicFormRecord.getId()));
            affectedRows = ps.executeUpdate();
            return affectedRows;
        }finally {
            DatabaseUtil.safeCloseAll(null, ps, null);
        }
    }

}
