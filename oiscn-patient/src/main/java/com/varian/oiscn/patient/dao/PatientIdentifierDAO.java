package com.varian.oiscn.patient.dao;

import com.varian.oiscn.base.dao.AbstractDAO;
import com.varian.oiscn.core.identifier.Identifier;
import com.varian.oiscn.core.identifier.IdentifierStatusEnum;
import com.varian.oiscn.core.identifier.IdentifierTypeEnum;
import com.varian.oiscn.core.identifier.IdentifierUseEnum;
import com.varian.oiscn.core.user.UserContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhp9696 on 2018/1/22.
 */
public class PatientIdentifierDAO extends AbstractDAO<Identifier> {
    private static String INSERT_SQL = "INSERT INTO PatientIdentifier(domain,value,type,status,used) VALUES(?,?,?,?,?)";
    private static String UPDATE_VALUE_SQL = "UPDATE PatientIdentifier SET value = ? WHERE domain = ?";
    private static String SELECT_BY_DOMAIN = "SELECT value,type,status,used FROM  PatientIdentifier WHERE domain = ?";

    private UserContext userContext;

    public PatientIdentifierDAO(UserContext userContext){
        super(userContext);
        this.userContext = userContext;
    }


    /**
     *
     * @param connection
     * @param dataList
     * @return
     */
   public int batchInsert(Connection connection, List<Identifier> dataList) throws SQLException {
       if(dataList == null){
           dataList = new ArrayList<>();
       }
       PreparedStatement ps = connection.prepareStatement(INSERT_SQL);

       for(Identifier identifier : dataList){
            int idx = 1;
            ps.setString(idx++,identifier.getDomain());
            ps.setString(idx++,identifier.getValue());
            ps.setString(idx++,identifier.getType().name());
            ps.setString(idx++,identifier.getStatus().name());
            ps.setString(idx++,identifier.getUse().name());
            ps.addBatch();
       }
       if(!dataList.isEmpty()){
           return ps.executeBatch().length;
       }
       return 0;
   }

    /**
     * update value for identifier
     * @param connection
     * @param identifier
     * @return
     * @throws SQLException
     */
   public int updateValue(Connection connection,Identifier identifier) throws SQLException {
       PreparedStatement ps = connection.prepareStatement(UPDATE_VALUE_SQL);
       ps.setString(1,identifier.getValue());
       ps.setString(2,identifier.getDomain());
       return ps.executeUpdate();
   }

    /**
     *
     * @param connection
     * @param domain
     * @return
     * @throws SQLException
     */
   public List<Identifier> selectIdentifierListByDomain(Connection connection,String domain) throws SQLException {
       List<Identifier> dataList = new ArrayList<>();
       PreparedStatement ps = connection.prepareStatement(SELECT_BY_DOMAIN);
       ps.setString(1,domain);
       ResultSet rs = ps.executeQuery();
       while(rs.next()){
           dataList.add(new Identifier(domain,rs.getString("value"), IdentifierTypeEnum.valueOf(rs.getString("type")),IdentifierStatusEnum.valueOf(rs.getString("status")), IdentifierUseEnum.valueOf(rs.getString("used"))));
       }
       return dataList;
   }
    @Override
    protected String getTableName() {
        return "PatientIdentifier";
    }

    @Override
    protected String getJsonbColumnName() {
        return null;
    }
}
