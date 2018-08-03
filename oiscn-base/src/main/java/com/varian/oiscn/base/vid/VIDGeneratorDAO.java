package com.varian.oiscn.base.vid;

import com.varian.oiscn.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by gbt1220 on 10/16/2017.
 */
public class VIDGeneratorDAO {
    public String getVID(Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "select vid from VID";
        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return null;
    }

    public boolean addVID(Connection connection, int vid) throws SQLException {
        String sql = "INSERT INTO VID(vid) VALUES (?)";
        return saveOrUpdate(connection, vid, sql);
    }

    public boolean updateVID(Connection connection, int vid) throws SQLException {
        String sql = "UPDATE VID SET vid=?";
        return saveOrUpdate(connection, vid, sql);
    }

    private boolean saveOrUpdate(Connection connection, int vid, String sql) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, vid);
            ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
        return true;
    }
}
