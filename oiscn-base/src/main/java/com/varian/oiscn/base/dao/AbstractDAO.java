package com.varian.oiscn.base.dao;

import com.varian.oiscn.base.common.JsonSerializer;
import com.varian.oiscn.util.DatabaseUtil;
import com.varian.oiscn.core.user.UserContext;
import lombok.AllArgsConstructor;

import java.sql.*;

/**
 * Created by gbt1220 on 7/7/2017.
 */
@AllArgsConstructor
public abstract class AbstractDAO<T> {

    protected UserContext userContext;

    protected abstract String getTableName();

    protected abstract String getJsonbColumnName();

    public String create(Connection con, T obj) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String createId;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            String json = new JsonSerializer<T>().getJson(obj);
            StringBuilder createSql = new StringBuilder("INSERT INTO ");
            createSql.append(getTableName()).append("(CreatedUser,CreatedDate,LastUpdatedUser,LastUpdatedDate,");
            createSql.append(getJsonbColumnName()).append(" ) VALUES(?,?,?,?,?::jsonb)");
            ps = con.prepareStatement(createSql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, userContext.getName());
            ps.setTimestamp(2, curTime);
            ps.setString(3, userContext.getName());
            ps.setTimestamp(4, curTime);
            ps.setString(5, json);
            ps.executeUpdate();

            resultSet = ps.getGeneratedKeys();
            resultSet.next();
            createId = resultSet.getString(1);
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return createId;
    }

    public boolean update(Connection connection, T obj, String id) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            String updateJson = new JsonSerializer<T>().getJson(obj);
            StringBuilder updateSql = new StringBuilder("UPDATE ");
            updateSql.append(getTableName());
            updateSql.append(" SET LastUpdatedUser=?,LastUpdatedDate=?,");
            updateSql.append(getJsonbColumnName()).append("=?::jsonb WHERE id=?");
            preparedStatement = connection.prepareStatement(updateSql.toString());
            preparedStatement.setString(1, userContext.getLogin().getUsername());
            preparedStatement.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            preparedStatement.setString(3, updateJson);
            preparedStatement.setLong(4, Long.parseLong(id));
            preparedStatement.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(preparedStatement);
        }
        return true;
    }
}
