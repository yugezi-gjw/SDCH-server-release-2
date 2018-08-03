package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.assign.AssignResourceField;
import com.varian.oiscn.core.user.UserContext;
import lombok.Cleanup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/5/7.
 */
public class AssignResourceFieldDAO extends AbstractDAO<AssignResourceField> {

    private static String SELECT_SQL = "SELECT category,name,value,sortNo FROM AssignResourceField WHERE category = ? ORDER BY sortNo";

    public AssignResourceFieldDAO(UserContext userContext) {
        super(userContext);
    }

    @Override
    protected String getTableName() {
        return "AssignResourceField";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }

    /**
     *
     * @param con
     * @param category
     * @return
     * @throws SQLException
     */
    public List<AssignResourceField> queryAssignResourceFieldByCategory(Connection con,String category) throws SQLException {
        List<AssignResourceField> list = new ArrayList<>();
        @Cleanup
        PreparedStatement ps = con.prepareStatement(SELECT_SQL);
        ps.setString(1,category);
        @Cleanup
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            list.add(rs2dto(rs));
        }
        return list;
    }

    /**
     *
     * @param con
     * @return
     * @throws SQLException
     */
    public  List<AssignResourceField> queryAssignResourceFieldValue(Connection con,String category) throws SQLException {
        return queryAssignResourceFieldByCategory(con,category);
    }

    private AssignResourceField rs2dto(ResultSet rs) throws SQLException {
        AssignResourceField assignResourceField = new AssignResourceField(){{
            setCategory(rs.getString("category"));
            setName(rs.getString("name"));
            setValue(rs.getString("value"));
            setSortNumber(rs.getInt("sortNo"));
        }};
        return assignResourceField;
    }

}
