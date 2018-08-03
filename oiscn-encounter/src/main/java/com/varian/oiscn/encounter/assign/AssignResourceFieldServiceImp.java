package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/5/7.
 */
@Slf4j
public class AssignResourceFieldServiceImp {

    private AssignResourceFieldDAO assignResourceFieldDAO;

    public AssignResourceFieldServiceImp(UserContext userContext){
        assignResourceFieldDAO = new AssignResourceFieldDAO(userContext);
    }

    /**
     *
     * @param category
     * @return
     */
    public List<AssignResourceField> queryAssignResourceFieldByCategory(String category) {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return assignResourceFieldDAO.queryAssignResourceFieldByCategory(connection,category);
        } catch (SQLException e) {
            log.debug("queryAssignResourceFieldByCategory SQLException SQLState=[{}], exception message {}, stack trace {} ", e.getSQLState(), e.getMessage(), e.getStackTrace());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public List<AssignResourceField> queryAssignResourceFieldValue(String category) {
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return assignResourceFieldDAO.queryAssignResourceFieldValue(connection,category);
        } catch (SQLException e) {
            log.debug("queryAssignResourceFieldValue SQLException SQLState=[{}], exception message {}, stack trace {} ", e.getSQLState(), e.getMessage(), e.getStackTrace());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return new ArrayList<>();
    }
}
