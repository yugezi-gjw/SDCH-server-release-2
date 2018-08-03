package com.varian.oiscn.base.codesystem;

import com.google.common.base.Strings;
import com.varian.oiscn.base.common.Constants;
import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.base.diagnosis.BodyPart;
import com.varian.oiscn.base.util.PinyinUtil;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 6/19/2017.
 */
public class CodeSystemDAO  {
    public void create(Connection connection, List<CodeValueDTO> codeValueDTOs) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO Diagnosis(createdUser,createdDate,lastUpdatedUser,lastUpdatedDate,diagnosisInfo,code,description,pinyin,value,system) VALUES(?,?,?,?,?,?,?,?,?,?)");
            Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
            String codeSystemJSON;
            for (CodeValueDTO codeValueDTO : codeValueDTOs) {
                codeSystemJSON = new JsonSerializer<CodeValueDTO>().getJson(codeValueDTO);
                ps.setString(1,Constants.SYSTEM);
                ps.setTimestamp(2,timestamp );
                ps.setString(3,Constants.SYSTEM);
                ps.setTimestamp(4,timestamp );
                ps.setString(5,codeSystemJSON);
                ps.setString(6,codeValueDTO.getCode());
                ps.setString(7,codeValueDTO.getDesc());
                if(!Strings.isNullOrEmpty(codeValueDTO.getDesc())){
                    ps.setString(8, PinyinUtil.chineseName2PinyinAcronyms(codeValueDTO.getDesc()));
                } else {
                    ps.setString(8, null);
                }
                ps.setString(9,codeValueDTO.getValue());
                ps.setString(10,codeValueDTO.getSystem());
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
    }

    public boolean isDiagnosisExisted(Connection connection, String scheme) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String querySql = "select count(*) from Diagnosis where code = ?";
            ps = connection.prepareStatement(querySql);
            ps.setString(1,scheme);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return false;
    }

    public List<CodeValueDTO> queryDiagnosis(Connection connection, String scheme, String keyword, String topN) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CodeValueDTO> result = new ArrayList<>();
        try {
            JsonSerializer<CodeValueDTO> jsonSerializer = new JsonSerializer();
            String sqlTopN = "";
            if(!StringUtils.isEmpty(topN)){
                sqlTopN = " top " + topN;
            }
            String querySql = "select" + sqlTopN + " d.diagnosisinfo from Diagnosis as d where value like '%.%' and code= ? and (value like '%'+?+'%' or description like '%'+?+'%' or pinyin like '%'+?+'%')";
            ps = connection.prepareStatement(querySql);
            ps.setString(1,scheme);
            ps.setString(2,keyword);
            ps.setString(3,keyword);
            ps.setString(4,keyword);
            rs = ps.executeQuery();
            CodeValueDTO codeValueDTO;
            while (rs.next()) {
                codeValueDTO = jsonSerializer.getObject(rs.getString(1), CodeValueDTO.class);
                result.add(codeValueDTO);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    /**
     * body part had exists or not
     * @param connection
     * @return
     * @throws SQLException
     */
    public boolean isBodyPartExisted(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String querySql = "select count(*) from BodyPart";
            ps = connection.prepareStatement(querySql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return false;
    }
    /**
     * insert bodyParts info to table
     * @param connection
     * @param bodyParts
     * @throws SQLException
     */
    public void createBodyParts(Connection connection,List<BodyPart> bodyParts) throws SQLException {
        String sql  = "INSERT INTO BodyPart (code,description,pinyin) values (?,?,?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        int batchSize = 2000;
        int count = 0;
        for(BodyPart bodyPart:bodyParts){
            ps.setString(1,bodyPart.getCode());
            ps.setString(2,bodyPart.getDescription());
            ps.setString(3,bodyPart.getPinyin());
            ps.addBatch();
            count++;
            if(count == batchSize){
                ps.executeBatch();
                count = 0;
            }
        }
        if(count > 0){
            ps.executeBatch();
        }
    }

    /**
     * query body part by desc or pinyin
     * @param connection
     * @param condition
     * @return
     * @throws SQLException
     */
    public List<BodyPart> queryBodyParts(Connection connection, String condition, String topN) throws  SQLException{
        List<BodyPart> list = new ArrayList<>();
        String sqlTopN = "";
        if(!StringUtils.isEmpty(topN)){
            sqlTopN = " top " + topN;
        }
        String sql = "SELECT" + sqlTopN + " code,description,pinyin FROM BodyPart WHERE 1=1 ";
        if(StringUtils.isNotEmpty(condition)){
            sql+=" AND (description LIKE '%'+?+'%' or pinyin LIKE '%'+?+'%') ";
        }
        PreparedStatement ps = connection.prepareStatement(sql);
        if(StringUtils.isNotEmpty(condition)){
            ps.setString(1,condition);
            ps.setString(2,condition.toUpperCase());;
        }
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(new BodyPart(rs.getString("code"),rs.getString("description"),rs.getString("pinyin")));
        }
        return list;
    }
}
