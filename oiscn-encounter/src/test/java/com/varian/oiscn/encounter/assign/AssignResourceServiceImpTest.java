package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.assign.AssignResource;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.device.DeviceJobTypeEnum;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * @Author: Allen
 * @Description:
 * @Date: Created in 3/26/2018
 * @Modified By:
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AssignResourceServiceImp.class, DevicesReader.class,
        ConnectionPool.class, BasicDataSourceFactory.class, SystemConfigPool.class})
public class AssignResourceServiceImpTest {

    private Connection connection;

    private AssignResourceServiceImp assignResourceServiceImp;

    private AssignResourceDAO assignResourceDAO;

    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp;

    @Before
    public void setup() throws Exception {
        assignResourceDAO = PowerMockito.mock(AssignResourceDAO.class);
        PowerMockito.whenNew(AssignResourceDAO.class).withAnyArguments().thenReturn(assignResourceDAO);
        PowerMockito.mockStatic(ConnectionPool.class);
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
        deviceAntiCorruptionServiceImp = PowerMockito.mock(DeviceAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(DeviceAntiCorruptionServiceImp.class).withNoArguments().thenReturn(deviceAntiCorruptionServiceImp);
        assignResourceServiceImp = new AssignResourceServiceImp(new UserContext());
    }

    @Test
    public void testListAssignDeviceSummaryReturnList() throws Exception {
        String activityCode = "AssignDevice";
        PowerMockito.when(assignResourceDAO.listAssignResourceSummary(connection,activityCode)).thenReturn(new ArrayList<AssignResourceVO>());
         List<DeviceDto> deviceDtos = givenDeviceDtos();
        PowerMockito.mockStatic(DevicesReader.class);
        PowerMockito.when(DevicesReader.getDeviceByUsage(activityCode)).thenReturn(deviceDtos);
        DeviceDto dto = givenDeviceDto();
        PowerMockito.when(deviceAntiCorruptionServiceImp.queryDeviceByCode(Matchers.anyString())).thenReturn(dto);
        List<AssignResourceVO> assignResourceVOS = assignResourceServiceImp.listAssignResourceSummary(activityCode);
        Assert.assertNotNull(assignResourceVOS);
    }

    @Test
    public void testListAssignTPSSummaryReturnList() throws Exception {
        String activityCode = "AssignTPS";
        PowerMockito.when(assignResourceDAO.listAssignResourceSummary(connection,activityCode)).thenReturn(Arrays.asList(new AssignResourceVO(){{
            setCode("Eclipse");
            setId("Eclipse");
            setAmount(12);
            setName("Eclipse");
        }}));
        List<DeviceDto> deviceDtos = givenTPSDtos();
        PowerMockito.when(assignResourceDAO.getAllResourceConfig(connection,activityCode)).thenReturn(deviceDtos);
        List<AssignResourceVO> assignResourceVOS = assignResourceServiceImp.listAssignResourceSummary(activityCode);
        Assert.assertNotNull(assignResourceVOS);
    }

    @Test
    public void testDeleteFromAssignedTPS() throws SQLException {
        Long patientSer = 1L;
        PowerMockito.when(assignResourceDAO.delete(connection, patientSer, null)).thenReturn(true);
        Assert.assertTrue(assignResourceServiceImp.deleteAssignedResource(patientSer, null));
    }

    @Test
    public void testAssignPatient2Resource() throws SQLException {
        List<AssignResource> assignResources = assembleAssignResources();
        PowerMockito.when(assignResourceDAO.batchCreate(connection,assignResources)).thenReturn(new int[1]);
        boolean ok = assignResourceServiceImp.assignPatient2Resource(assignResources);
        Assert.assertTrue(ok);
    }

    @Test
    public void testGetAssignResourceByPatientSerList() throws SQLException {
        String activityCode = "AssignTPS";
        List<Long> patientSerList = Arrays.asList(11L,12L);
        Map<Long, String> resultMap = new HashMap<>();
        resultMap.put(patientSerList.get(0),"1221");
        resultMap.put(patientSerList.get(1),"2345");
        PowerMockito.when(assignResourceDAO.getPatientSerResourceMapByPatientSerList(connection,patientSerList,activityCode)).thenReturn(resultMap);
        Map<Long,String> result = assignResourceServiceImp.getPatientSerResourceMapByPatientSerList(patientSerList,activityCode);
        Assert.assertTrue(resultMap == result);
    }

    private List<AssignResource> assembleAssignResources() {
        List<AssignResource> assignResources = new ArrayList<>();
        AssignResource assignResource1 = new AssignResource();
        assignResource1.setId("id1");
        assignResource1.setPatientSer(12345L);
        assignResource1.setResourceId("resourceId");
        assignResource1.setActivityCode("activityCode");
        assignResource1.setEncounterId(123l);
        assignResource1.setAmount(10);
        assignResources.add(assignResource1);
        return assignResources;
    }

    private List<DeviceDto> givenDeviceDtos() {
        List<DeviceDto> deviceDtos = new ArrayList<>();
        DeviceDto deviceDto1 = new DeviceDto();
        deviceDto1.setId("deviceId1");
        deviceDto1.setJobType(DeviceJobTypeEnum.TREATMENT);
        deviceDto1.setName("deviceName1");
        deviceDto1.setUsage("AssignDevice");
        deviceDto1.setColor("color1");
        deviceDtos.add(deviceDto1);
        return deviceDtos;
    }
    private List<DeviceDto> givenTPSDtos() {
        List<DeviceDto> deviceDtos = new ArrayList<>();
        DeviceDto deviceDto1 = new DeviceDto();
        deviceDto1.setId("Eclipse");
        deviceDto1.setCode("Eclipse");
        deviceDto1.setJobType(DeviceJobTypeEnum.TREATMENT);
        deviceDto1.setName("Eclipse");
        deviceDto1.setUsage("AssignTPS");
        deviceDto1.setColor("color1");
        deviceDtos.add(deviceDto1);
        return deviceDtos;
    }
    private DeviceDto givenDeviceDto() {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId("deviceId1");
        deviceDto.setJobType(DeviceJobTypeEnum.TREATMENT);
        deviceDto.setName("deviceName1");
        deviceDto.setColor("color1");
        return deviceDto;
    }

}
