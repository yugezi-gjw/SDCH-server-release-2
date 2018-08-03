package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.base.diagnosis.BodyPart;
import com.varian.oiscn.base.diagnosis.BodyPartVO;
import com.varian.oiscn.base.util.PinyinUtil;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 6/19/2017.
 */
@Slf4j
public class CodeSystemServiceImp {

    private CodeSystemDAO codeSystemDAO;

    public CodeSystemServiceImp() {
        codeSystemDAO = new CodeSystemDAO();
    }

    public void create(CodeSystem codeSystem) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            List<CodeValueDTO> codeValueDTOList = new ArrayList<>();
            int index = 0;
            for (CodeValue codeValue : codeSystem.getCodeValues()) {
                codeValueDTOList.add(CodeValueAssembler.assemblerCodeValueDTO(codeSystem, codeValue));
                if (++index == 1000) {
                    codeSystemDAO.create(con, codeValueDTOList);
                    index = 0;
                    codeValueDTOList = new ArrayList<>();
                }
            }
            codeSystemDAO.create(con, codeValueDTOList);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            // only invoked while initialization, so throw the Error to shutdown the server. 
            throw new RuntimeException(e.getMessage(), e.getCause());
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public void createBodyParts(List<BodyPartVO> bodyParts){
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            List<BodyPart> bodyPartList = new ArrayList<>();
            bodyParts.forEach(bodyPartVO ->bodyPartList.add(new BodyPart(bodyPartVO.getCode(),bodyPartVO.getDesc(), PinyinUtil.chineseName2PinyinAcronyms(bodyPartVO.getDesc()))));
            codeSystemDAO.createBodyParts(con,bodyPartList);
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public List<BodyPartVO> queryBodyParts(String condition, String topN){
        Connection con = null;
        List<BodyPartVO> bodyparts = new ArrayList<>();
        try{
            con = ConnectionPool.getConnection();
            List<BodyPart> diagnosisPairsList = codeSystemDAO.queryBodyParts(con,condition, topN);

            diagnosisPairsList.forEach(diagnosisPair -> {
                bodyparts.add(new BodyPartVO(diagnosisPair.getCode(),diagnosisPair.getDescription(),diagnosisPair.getPinyin()));
            });
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return bodyparts;
    }
    public boolean isDiagnosisExisted(String scheme) {
        Connection connection = null;
        try {
        	connection = ConnectionPool.getConnection();
            return codeSystemDAO.isDiagnosisExisted(connection, scheme);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            // SQL Exception should be fixed while server initialization
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
    }

    public boolean isBodyPartExisted() {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return codeSystemDAO.isBodyPartExisted(connection);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            return true;
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
    }
    public List<CodeValueDTO> queryDiagnosis(String scheme, String keyword, String topN) {
        Connection connection = null;
        try {
        	connection = ConnectionPool.getConnection();
            return codeSystemDAO.queryDiagnosis(connection, scheme, keyword, topN);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            return new ArrayList<>();
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
    }
}
