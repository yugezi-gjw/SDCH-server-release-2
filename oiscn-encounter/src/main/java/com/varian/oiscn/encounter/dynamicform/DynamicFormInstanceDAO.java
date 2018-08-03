package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.StatusEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;

import java.sql.*;
import java.util.*;

/**
 * Created by gbt1220 on 6/30/2017.
 */
public class DynamicFormInstanceDAO extends AbstractDAO<DynamicFormInstance> {

    public DynamicFormInstanceDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "DynamicFormInstance";
    }

    @Override
    protected String getJsonbColumnName() {
        return "dynamicFormInstanceInfo";
    }


    @Override
    public String create(Connection con, DynamicFormInstance dynamicFormInstance) throws SQLException {
        PreparedStatement ps;
        ResultSet resultSet;
        String createId = null;
        Timestamp curTime = new Timestamp(new java.util.Date().getTime());
        String json = new JsonSerializer<DynamicFormInstance>().getJson(dynamicFormInstance);
        StringBuilder createSql = new StringBuilder("INSERT INTO ");
        createSql.append(getTableName()).append("(hisId,encounterId,createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,");
        createSql.append(getJsonbColumnName()).append(" ,patientSer) VALUES(?,?,?,?,?,?,?,?)");
        ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, dynamicFormInstance.getHisId());
        ps.setLong(2, Long.parseLong(dynamicFormInstance.getEncounterId()));
        ps.setString(3, userContext.getName());
        ps.setTimestamp(4, curTime);
        ps.setString(5, userContext.getName());
        ps.setTimestamp(6, curTime);
        ps.setString(7, json);
        ps.setLong(8,Long.parseLong(dynamicFormInstance.getPatientSer()));
        ps.executeUpdate();
        resultSet = ps.getGeneratedKeys();
        if (resultSet.next()) {
            createId = resultSet.getString(1);
        }
        batchCreateItems(con, dynamicFormInstance.getPatientSer(), Long.parseLong(dynamicFormInstance.getEncounterId()), dynamicFormInstance.getDynamicFormItems());
        return createId;
    }

    private int batchCreateItems(Connection con, String patientSer, long encounterId, List<KeyValuePair> dynamicFormItemsList) throws SQLException {
        String sql = "insert into DynamicFormItem (hisId,encounterId,patientSer,itemKey,itemValue) VALUES (?,?,?,?,?)";
        int batchSize = 1000;
        int count = 0;
        int insertRows = 0;
        PreparedStatement ps = con.prepareStatement(sql);
        for (int i = 0; i < dynamicFormItemsList.size(); i++) {
            KeyValuePair keyValuePair = dynamicFormItemsList.get(i);
            int idx = 1;
            ps.setString(idx++, null);
            ps.setLong(idx++, encounterId);
            ps.setLong(idx++, new Long(patientSer));
            ps.setString(idx++, keyValuePair.getKey());
            ps.setString(idx++, keyValuePair.getValue());
            ps.addBatch();
            count++;
            if (count % batchSize == 0) {
                insertRows = ps.executeBatch().length;
                count = 0;

            }
        }
        if (count > 0) {
            insertRows += ps.executeBatch().length;
        }
        return insertRows;
    }

    @Override
    public boolean update(Connection connection, DynamicFormInstance dynamicFormInstance, String id) throws SQLException {
        PreparedStatement preparedStatement;
        String updateJson = new JsonSerializer<DynamicFormInstance>().getJson(dynamicFormInstance);
        StringBuilder updateSql = new StringBuilder("UPDATE ");
        updateSql.append(getTableName());
        updateSql.append(" SET lastUpdatedUser=?,lastUpdatedDate=?,");
        updateSql.append(getJsonbColumnName()).append("=? WHERE id=?");
        preparedStatement = connection.prepareStatement(updateSql.toString());
        preparedStatement.setString(1, userContext.getLogin().getUsername());
        preparedStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        preparedStatement.setString(3, updateJson);
        preparedStatement.setLong(4, Long.parseLong(id));
        preparedStatement.executeUpdate();

        deleteFormItemsByPatientSerAndEncounterId(connection, dynamicFormInstance.getPatientSer(), Long.parseLong(dynamicFormInstance.getEncounterId()));
        batchCreateItems(connection, dynamicFormInstance.getPatientSer(), Long.parseLong(dynamicFormInstance.getEncounterId()), dynamicFormInstance.getDynamicFormItems());
        return true;
    }

    private int deleteFormItemsByPatientSerAndEncounterId(Connection con, String patientSer, long encounterId) throws SQLException {
        String sql = "delete from DynamicFormItem where patientSer = ? and encounterId = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, new Long(patientSer));
        ps.setLong(2, encounterId);
        return ps.executeUpdate();
    }

    public DynamicFormInstance queryByPatientSer(Connection connection, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        DynamicFormInstance instance = null;
        String instanceId;
        try {
            JsonSerializer<DynamicFormInstance> jsonSerializer = new JsonSerializer();
            String querySql = "select fi.id, fi."+this.getJsonbColumnName()+" from DynamicForminstance fi,Encounter e where fi.patientSer = ? and fi.encounterId = e.id and e.status = ?";
            ps = connection.prepareStatement(querySql);
            ps.setLong(1, patientSer);
            ps.setString(2, StatusEnum.IN_PROGRESS.name());
            rs = ps.executeQuery();
            if (rs.next()) {
                instanceId = rs.getString(1);
                instance = jsonSerializer.getObject(rs.getString(2), DynamicFormInstance.class);
                instance.setId(instanceId);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return instance;
    }

    public String selectFieldValueByPatientSerAndName(Connection connection, String patientSer, String fieldName)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String value;
        try {
           Map<String,String> map = this.selectFieldValueByPatientSerListAndName(connection, Arrays.asList(patientSer),fieldName);
           value = map.get(patientSer);
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return value;
    }

    /**
     * 获取指定动态表单字段名称的hisId List 的值
     * @param connection
     * @param patientSerList
     * @param fieldName
     * @return
     * @throws SQLException
     */
    public Map<String,String> selectFieldValueByPatientSerListAndName(Connection connection, List<String> patientSerList, String fieldName)
            throws SQLException {
        Map<String,String> map = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            List<Object> paramList = new ArrayList();
            StringBuilder sql = new StringBuilder("select i.hisId,i.patientSer,i.itemValue from  DynamicFormItem i,Encounter e where i.encounterId = e.id and i.itemKey = ? and e.status = ? ");
            if(patientSerList != null){
                sql.append(" and (");
                for(int i=0;i<patientSerList.size();i++){
                    if(i == 0 ){
                        sql.append("i.patientSer = ? ");
                    }else{
                        sql.append(" or i.patientSer = ? ");
                    }
                }
                sql.append(")");
            }
            sql.append(" order by i.id desc");
            paramList.add(fieldName);
            paramList.add(StatusEnum.IN_PROGRESS.name());
            patientSerList.forEach(patientSer-> paramList.add(new Long(patientSer)));
            ps = connection.prepareStatement(sql.toString());
            for(int i=0;i<paramList.size();i++){
                ps.setObject(i+1,paramList.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("patientSer"),rs.getString("itemValue"));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return map;
    }

    public Map<String, Map<String, String>> selectFieldNameValuePairsByPatientSerList(Connection connection, List<String> patientSerList,
                                                                                      List<String> fieldNames) throws SQLException {
        Map<String, Map<String, String>> map = new HashMap<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            List<Object> paramList = new ArrayList();
            StringBuilder sql = new StringBuilder("select i.hisId, i.patientSer, i.itemKey, i.itemValue from DynamicFormItem i, Encounter e where i.encounterId = e.id and e.status = ? ");
            if(patientSerList != null) {
                sql.append(" and ( ");
                for(int i = 0; i<patientSerList.size(); i++) {
                    if(i == 0 ){
                        sql.append("i.patientSer = ? ");
                    }else{
                        sql.append(" or i.patientSer = ? ");
                    }
                }
                sql.append(")");
            }
            if(fieldNames != null) {
                sql.append(" and ( ");
                for(int i = 0; i<fieldNames.size(); i++) {
                    if(i == 0 ){
                        sql.append("i.itemKey = ? ");
                    }else{
                        sql.append(" or i.itemKey = ? ");
                    }
                }
                sql.append(")");
            }
            sql.append(" order by i.id desc");
            paramList.add(StatusEnum.IN_PROGRESS.name());
            patientSerList.forEach(patientSer-> paramList.add(new Long(patientSer)));
            fieldNames.forEach(fieldName -> paramList.add(fieldName));
            ps = connection.prepareStatement(sql.toString());
            for(int i=0;i<paramList.size();i++){
                ps.setObject(i+1,paramList.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                String patientSer = rs.getString("patientSer");
                Map<String, String> fieldNamePair = map.get(patientSer);
                if(fieldNamePair != null) {
                    fieldNamePair.put(rs.getString("itemKey"), rs.getString("itemValue"));
                }
                else {
                    fieldNamePair = new HashMap<>();
                    fieldNamePair.put(rs.getString("itemKey"), rs.getString("itemValue"));
                    map.put(patientSer, fieldNamePair);
                }
            }

        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return map;
    }

}
