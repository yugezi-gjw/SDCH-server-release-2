package com.varian.oiscn.appointment.service;

import com.varian.oiscn.appointment.dao.QueuingManagementDAO;
import com.varian.oiscn.appointment.dto.CheckInStatusEnum;
import com.varian.oiscn.appointment.dto.QueuingManagement;
import com.varian.oiscn.appointment.dto.QueuingManagementDTO;
import com.varian.oiscn.appointment.util.MockDatabaseConnection;
import com.varian.oiscn.appointment.vo.QueuingManagementVO;
import com.varian.oiscn.connection.ConnectionPool;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by BHP9696 on 2017/10/23.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({QueuingManagementServiceImpl.class, ConnectionPool.class, BasicDataSourceFactory.class,PropertyUtils.class})
public class QueuingManagementServiceTest {

    private QueuingManagementDAO queuingManagementDAO;

    private QueuingManagementServiceImpl queuingManagementService;

    private Connection conn;

    @Before
    public void setup() {
        try {
            queuingManagementDAO = PowerMockito.mock(QueuingManagementDAO.class);
            PowerMockito.whenNew(QueuingManagementDAO.class).withAnyArguments().thenReturn(queuingManagementDAO);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            conn = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(conn);
            queuingManagementService = new QueuingManagementServiceImpl();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenQueuingManagementDTOWhenCheckInThenReturnTrue() throws SQLException {
        PowerMockito.when(queuingManagementDAO.create(Matchers.any(), Matchers.any())).thenReturn("6");
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        queuingManagementDTO.setStartTime("2017-10-25 14:30:00");
        boolean result = queuingManagementService.checkIn(queuingManagementDTO);
        Assert.assertTrue(result);
        PowerMockito.when(queuingManagementDAO.create(Matchers.any(), Matchers.any())).thenThrow(new SQLException("sqlerror"));
        result = queuingManagementService.checkIn(queuingManagementDTO);
        Assert.assertFalse(result);
    }

    @Test
    public void givenQueuingManagementDTOWhenCheckInStickThenReturnTrue() throws SQLException {
        PowerMockito.when(queuingManagementDAO.updateIdx(Matchers.any(), Matchers.any())).thenReturn(1);
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        boolean result = queuingManagementService.checkInStick(queuingManagementDTO);
        Assert.assertTrue(result);
        PowerMockito.when(queuingManagementDAO.updateIdx(Matchers.any(), Matchers.any())).thenThrow(new SQLException("sqlerror"));
        result = queuingManagementService.checkInStick(queuingManagementDTO);
        Assert.assertFalse(result);
    }
    @Test
    public void givenQueuingManagementDTOWhenCheckInStickThenThrowPropertyCopyException() throws SQLException {
        PowerMockito.when(queuingManagementDAO.updateIdx(Matchers.any(), Matchers.any())).thenReturn(1);
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        try {
            PowerMockito.mockStatic(PropertyUtils.class);
            PowerMockito.spy(PropertyUtils.class);
            PowerMockito.doThrow(new IllegalAccessException()).when(PropertyUtils.class, "copyProperties",Matchers.any(),Matchers.any());
            queuingManagementService.checkInStick(queuingManagementDTO);
            PowerMockito.doThrow(new InvocationTargetException(new Exception())).when(PropertyUtils.class, "copyProperties",Matchers.any(),Matchers.any());
            queuingManagementService.checkInStick(queuingManagementDTO);
            PowerMockito.doThrow(new NoSuchMethodException()).when(PropertyUtils.class, "copyProperties",Matchers.any(),Matchers.any());
            queuingManagementService.checkInStick(queuingManagementDTO);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
    @Test
    public void givenAppointmentIdWhenDeleteByAppointmentIdThenReturnInt() throws SQLException {
        QueuingManagementDTO queuingManagement = new QueuingManagementDTO() {{
            setAppointmentId("12121");
            setCheckInIdx(-1);
            setCheckInStatus(CheckInStatusEnum.DELETED);
        }};
        PowerMockito.when(queuingManagementDAO.updateStatusAndIdx(Matchers.any(), Matchers.any())).thenReturn(1);
        int effect = queuingManagementService.unCheckIn(queuingManagement);
        Assert.assertTrue(effect == 1);
        PowerMockito.when(queuingManagementDAO.updateStatusAndIdx(Matchers.any(), Matchers.any())).thenThrow(new SQLException("sqlerror"));
        effect = queuingManagementService.unCheckIn(queuingManagement);
        Assert.assertTrue(effect < 0);
    }

    @Test
    public void givenAppointmentIdThenReturnIfThisPatientHasBeenCheckedIn() throws SQLException {
        boolean result = true;
        PowerMockito.when(queuingManagementDAO.ifAlreadyCheckedIn(Matchers.any(), Matchers.anyString())).thenReturn(result);
        Assert.assertEquals(queuingManagementService.ifAlreadyCheckedIn("appointmentId"), 1);
        PowerMockito.when(queuingManagementDAO.ifAlreadyCheckedIn(Matchers.any(), Matchers.anyString())).thenReturn(false);
        Assert.assertEquals(queuingManagementService.ifAlreadyCheckedIn("appointmentId"), 0);
        PowerMockito.when(queuingManagementDAO.ifAlreadyCheckedIn(Matchers.any(), Matchers.anyString())).thenThrow(new SQLException("sql error"));
        int effect = queuingManagementService.ifAlreadyCheckedIn("appointmentId");
        Assert.assertTrue(effect < 0);
    }

    @Test
    public void givenUidAndAriaIdAppointmentWhenModifyFromUid2AriaIdThenReturnInt() throws SQLException {
        PowerMockito.when(queuingManagementDAO.updateUid2AriaAppointmentId(conn, "uid", "1212")).thenReturn(1);
        Assert.assertEquals(queuingManagementService.modifyFromUid2AriaId("uid", "1212"), 1);
        PowerMockito.when(queuingManagementDAO.updateUid2AriaAppointmentId(conn, "uid", "1212")).thenThrow(new SQLException("sqlerror"));
        int r = queuingManagementService.modifyFromUid2AriaId("uid", "1212");
        Assert.assertTrue(r == 0);
    }

    @Test
    public void testCallingNext() throws SQLException {
        List<QueuingManagementDTO> list = Arrays.asList(new QueuingManagementDTO(){{
            setHisId("hisId");
            setPatientSer(1221L);
            setAppointmentId("12121");
            setCheckInStatus(CheckInStatusEnum.WAITING);
        }});
        PowerMockito.when(queuingManagementDAO.updateStatusAndIdx(Matchers.any(),Matchers.any())).thenReturn(1);
        boolean ok = queuingManagementService.callingNext(list);
        Assert.assertTrue(ok);
        PowerMockito.when(queuingManagementDAO.updateStatusAndIdx(Matchers.any(),Matchers.any())).thenThrow(new SQLException("sqlerror"));
        ok = queuingManagementService.callingNext(list);
        Assert.assertFalse(ok);
    }

    @Test
    public void testQueryCheckInList() throws SQLException {
        QueuingManagementDTO queuingManagementDTO = new QueuingManagementDTO();
        List<QueuingManagement> list = Arrays.asList(new QueuingManagement(){{
            setAppointmentId("1212");
            setCheckInStatus(CheckInStatusEnum.CALLING);
            setPatientSer(121L);
            setHisId("H20180304");
            setCheckInTime(new Date());
        }});
        PowerMockito.when(queuingManagementDAO.queryList(conn,queuingManagementDTO)).thenReturn(list);
        List<QueuingManagementVO> voList = queuingManagementService.queryCheckInList(queuingManagementDTO);
        Assert.assertNotNull(voList);
        Assert.assertTrue(voList.size()==1);
        PowerMockito.when(queuingManagementDAO.queryList(conn,queuingManagementDTO)).thenThrow(new SQLException("sqlerror"));
        voList = queuingManagementService.queryCheckInList(queuingManagementDTO);
        Assert.assertNotNull(voList);
        Assert.assertTrue(voList.isEmpty());
    }
}
