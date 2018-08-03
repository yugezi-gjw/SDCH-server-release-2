package com.varian.oiscn.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@Slf4j
public class DatabaseUtil {

    private DatabaseUtil(){

    }

    /**
     * safe to close connection
     *
     * @param con the connection
     */
    public static void safeCloseConnection(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }
    }

    /**
     * safe to close statement
     *
     * @param stat
     */
    public static void safeCloseStatement(Statement stat) {
        try {
            if (stat != null) {
                stat.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }
    }

    /**
     * safe to close result set
     *
     * @param rs the rs
     */
    public static void safeCloseResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }
    }

    /**
     * Close the JDBC resource together.
     *
     * @param con Connection
     * @param st  Statement
     * @param rs  ResultSet
     */
    public static void safeCloseAll(Connection con, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }

        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }

        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            log.warn("SQLException SQLState=[{}]", e.getSQLState());
        }
    }

    /**
     * Safe set auto commit.
     *
     * @param con        the con
     * @param autoCommit the auto commit
     */
    public static void safeSetAutoCommit(Connection con, boolean autoCommit) {
        try {
            if (con != null) {
                con.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            log.warn("SQLException SetAutoCommit=[{}], SQLState=[{}]", e.getMessage(), e.getSQLState());
        }
    }

    /**
     * Safe rollback.
     *
     * @param con the con
     */
    public static void safeRollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            log.warn("SQLException Rollback=[{}], SQLState=[{}]", e.getMessage(), e.getSQLState());
        }
    }
}
