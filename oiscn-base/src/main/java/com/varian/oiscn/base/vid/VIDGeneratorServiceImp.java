package com.varian.oiscn.base.vid;

import com.varian.oiscn.base.common.Constants;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Created by gbt1220 on 10/16/2017.
 */
@Slf4j
public class VIDGeneratorServiceImp {

    private VIDGeneratorDAO dao;

    public VIDGeneratorServiceImp() {
        dao = new VIDGeneratorDAO();
    }

    /**
     * 生成V号
     * 如果db中没有，从配置中取初始V号number；否则使用db中的number，取完后更新V号number
     *
     * @return V号
     */
    public synchronized String generateVID() {
        Connection connection = null;
        String vid = StringUtils.EMPTY;
        try {
            connection = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(connection, false);

            vid = dao.getVID(connection);
            if (StringUtils.isEmpty(vid)) {
                vid = SystemConfigPool.queryStartVIDNumber();
                int nextVid = Integer.parseInt(vid) + 1;
                dao.addVID(connection, nextVid);
            } else {
                int nextVid = Integer.parseInt(vid) + 1;
                dao.updateVID(connection, nextVid);
            }

            connection.commit();
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(connection);
        } finally {
            DatabaseUtil.safeSetAutoCommit(connection, true);
            DatabaseUtil.safeCloseConnection(connection);
        }
        return SystemConfigPool.queryVIDPrefix()
                + new DecimalFormat(Constants.VID_FORMAT).format(Integer.parseInt(vid));
    }
}
