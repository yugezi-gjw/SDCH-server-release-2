package com.varian.oiscn.appointment.service;

import com.varian.oiscn.appointment.dao.QueuingManagementDAO;
import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueuingManagement;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.util.DatabaseUtil;
import com.varian.oiscn.base.util.DateUtil;
import com.varian.oiscn.core.user.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/20.
 */
@Slf4j
public class QueuingManagementServiceImpl {
    private QueuingManagementDAO queuingManagementDAO;

    public QueuingManagementServiceImpl() {
        queuingManagementDAO = new QueuingManagementDAO();
    }

    public QueuingManagementServiceImpl(UserContext userContext) {
        queuingManagementDAO = new QueuingManagementDAO(userContext);
    }



    /**
     * 置顶操作。
     * 需要参数：id 或者 appointmentId，activityCode;checkInStatus，checkInIdx
     * @param queuingManagementDTO
     * @return
     */
    public boolean checkInStick(QueuingManagementDTO queuingManagementDTO) {
        boolean result = false;
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            int effect = queuingManagementDAO.updateIdx(conn,dto2entity(queuingManagementDTO));
            if (effect > 0) {
                result = true;
            }
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return result;
    }

    /**
     * 呼叫下一个患者
     *
     * @param queuingManagementDTOList 保存的是需要进入到calling列表的患者信息
     * @return
     */
    public boolean callingNext(List<QueuingManagementDTO>  queuingManagementDTOList){
        Connection con = null;
        boolean result = false;
        try {
            con = ConnectionPool.getConnection();
            int effect = 0;
            for(QueuingManagementDTO queuingManagementDTO : queuingManagementDTOList){
                effect += queuingManagementDAO.updateStatusAndIdx(con,dto2entity(queuingManagementDTO));
            }
            if(effect >= queuingManagementDTOList.size()){
                result = true;
            }
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    /**
     * 到检
     * @param queuingManagementDTO
     * @return
     */
    public boolean checkIn(QueuingManagementDTO queuingManagementDTO){
        boolean result = false;
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            queuingManagementDTO.setCheckInStatus(CheckInStatusEnum.WAITING);
            String key = queuingManagementDAO.create(con,dto2entity(queuingManagementDTO));
            if(StringUtils.isNotEmpty(key)){
                result = true;
            }
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    /**
     * 查询当前日期到检的信息
     * @param queuingManagementDTO
     * @return
     */
    public List<QueuingManagementVO> queryCheckInList(QueuingManagementDTO queuingManagementDTO) {
        List<QueuingManagementVO> resultList =  new ArrayList<>();
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            List<QueuingManagement> list = queuingManagementDAO.queryList(con,queuingManagementDTO);
            list.forEach(queuingManagement -> {
                resultList.add(entity2Vo(queuingManagement));
            });

        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return resultList;
    }

    /**
     *
     * @param queuingManagementDTO
     * @return
     */
    public int unCheckIn(QueuingManagementDTO queuingManagementDTO){
        Connection con = null;
        try{
            con = ConnectionPool.getConnection();
            return queuingManagementDAO.updateStatusAndIdx(con,new QueuingManagement(){{
                setAppointmentId(queuingManagementDTO.getAppointmentId());
                setCheckInIdx(queuingManagementDTO.getCheckInIdx());
                setCheckInStatus(queuingManagementDTO.getCheckInStatus());
            }});
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return -1;
    }

    /**
     * 自助到检中查看某个预约是否已经到检
     * @param appointmentId
     * @return
     */
    public int ifAlreadyCheckedIn(String appointmentId){
        Connection con = null;
        int result = -1;
        try{
            con = ConnectionPool.getConnection();
            boolean isCheckedIn = queuingManagementDAO.ifAlreadyCheckedIn(con, appointmentId);
            if(isCheckedIn){
                result = 1;
            } else {
                result = 0;
            }
        }catch (SQLException e){
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return result;
    }

    /**
     * 将appointment中的值由uid，更新成实际的aria里面的appointmentId
     * @param uid
     * @param ariaAppointmentId
     * @return
     */
    public int modifyFromUid2AriaId(String uid,String ariaAppointmentId){
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            return queuingManagementDAO.updateUid2AriaAppointmentId(connection,uid,ariaAppointmentId);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return 0;
    }


    private QueuingManagement dto2entity(QueuingManagementDTO queuingManagementDTO) {
        QueuingManagement queuingManagement = null;
        try {
            queuingManagement = new QueuingManagement();
            String startTime = queuingManagementDTO.getStartTime();
            queuingManagementDTO.setStartTime(null);
            PropertyUtils.copyProperties(queuingManagement,queuingManagementDTO);
            if(StringUtils.isNotEmpty(startTime)) {
                queuingManagement.setStartTime(DateUtil.parse(startTime));
            }
            queuingManagement.setCheckInTime(new Date());
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException: {}", e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("InvocationTargetException: {}", e.getMessage());
        } catch (ParseException e){
            log.error("ParseException: {}", e.getMessage());
        }catch (NoSuchMethodException e){
            log.error("NoSuchMethodException: {}", e.getMessage());
        }
        return queuingManagement;
    }

    private QueuingManagementVO entity2Vo(QueuingManagement queuingManagement){
        QueuingManagementVO queueManagementVO = new QueuingManagementVO();
        try {
            PropertyUtils.copyProperties(queueManagementVO,queuingManagement);
        } catch (IllegalAccessException e) {
            log.error("IllegalAccessException: {}", e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("InvocationTargetException: {}", e.getMessage());
        } catch (NoSuchMethodException e){
            log.error("NoSuchMethodException: {}", e.getMessage());
        }
        return queueManagementVO;
    }

}
