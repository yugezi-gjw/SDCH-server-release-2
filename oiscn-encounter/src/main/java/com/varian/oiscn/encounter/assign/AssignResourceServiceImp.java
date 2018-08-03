package com.varian.oiscn.encounter.assign;

import com.varian.oiscn.anticorruption.resourceimps.DeviceAntiCorruptionServiceImp;
import com.varian.oiscn.base.util.DevicesReader;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.assign.AssignResource;
import com.varian.oiscn.core.assign.AssignResourceVO;
import com.varian.oiscn.core.device.DeviceDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bhp9696 on 2018/3/23.
 */
@Slf4j
public class AssignResourceServiceImp {

    public static final String PATIENT_SER_KEY = "PatientSer";

    public static final String ORDER_ID_KEY = "orderId";

    private AssignResourceDAO assignResourceDAO;

    private DeviceAntiCorruptionServiceImp deviceAntiCorruptionServiceImp;

    public AssignResourceServiceImp(UserContext userContext) {
        assignResourceDAO = new AssignResourceDAO(userContext);
        deviceAntiCorruptionServiceImp = new DeviceAntiCorruptionServiceImp();
    }

    /**
     * Assign patient to resource
     *
     * @param assignResourceList patient assign records
     * @return result of assignment.
     */
    public boolean assignPatient2Resource(List<AssignResource> assignResourceList) {
        Connection conn = null;
        int[] items = new int[]{};
        try {
            DatabaseUtil.safeSetAutoCommit(conn, false);
            conn = ConnectionPool.getConnection();
            items = assignResourceDAO.batchCreate(conn, assignResourceList);
            conn.commit();
        } catch (SQLException e) {
            log.error("create AssignResource SQLException SQLState=[{}], exception message {}, stack trace {} ", e.getSQLState(), e.getMessage(), e.getStackTrace());
            DatabaseUtil.safeRollback(conn);
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
        return items.length > 0;
    }

    /**
     * Delete patient record for assigned resource.
     * @param patientSer patient Ser
     * @return result of delete
     */
    public boolean deleteAssignedResource(Long patientSer, String activityCode) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
            return assignResourceDAO.delete(conn, patientSer, activityCode);
        } catch (SQLException e) {
            log.error("deleteAssignedResource SQLException SQLState=[{}] for patientSer[{}]", e.getSQLState(), patientSer);
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(conn);
        }
    }

    /**
     * Search assign resource summary
     *
     * @param activityCode activity code
     * @return resource summary list
     */
    public List<AssignResourceVO> listAssignResourceSummary(String activityCode) {
        List<AssignResourceVO> resourceSummary = new ArrayList<>();
        Connection con = null;
        List<AssignResourceVO> assignedResourceList;
        try {
            con = ConnectionPool.getConnection();
            assignedResourceList = assignResourceDAO.listAssignResourceSummary(con, activityCode);
//         query deviceConfig from table (TPS)
            List<DeviceDto> tpsDtoList = assignResourceDAO.getAllResourceConfig(con,activityCode);
            if(!tpsDtoList.isEmpty()){
                tpsDtoList.forEach(deviceDto -> {
                    AssignResourceVO vo = new AssignResourceVO();
                    vo.setId(deviceDto.getCode());
                    vo.setCode(deviceDto.getCode());
                    vo.setColor(deviceDto.getColor());
                    vo.setName(deviceDto.getName());
                    vo.setAmount(0);
                    assignedResourceList.forEach(assignResourceVO -> {
                        if(assignResourceVO.getId() != null && assignResourceVO.getId().equals(vo.getId())){
                            vo.setAmount(assignResourceVO.getAmount());
                        }
                    });
                    resourceSummary.add(vo);
                });
            }
            if(tpsDtoList.isEmpty()) {
//              This activityCode is assign machine
                List<DeviceDto> yamlDeviceList = DevicesReader.getDeviceByUsage(activityCode);
                if (yamlDeviceList != null) {
                    for (DeviceDto yamlDevice : yamlDeviceList) {
                        String code = yamlDevice.getId();
                        AssignResourceVO vo = new AssignResourceVO();
                        vo.setId(code);
                        vo.setCode(code);
                        vo.setColor(yamlDevice.getColor());
                        vo.setAmount(0);
                        // aria device
                        DeviceDto fhirDevice = deviceAntiCorruptionServiceImp.queryDeviceByCode(code);
                        if (fhirDevice == null) {
                            // not a valid Aria device
                            continue;
                        } else {
                            vo.setId(fhirDevice.getId());
                            vo.setCode(fhirDevice.getCode());
                            vo.setName(fhirDevice.getName());
                        }

                        // fill the assigned resource amount
                        for (AssignResourceVO assignedResource : assignedResourceList) {
                            String resourceId = assignedResource.getId();
                            if (resourceId != null && resourceId.equalsIgnoreCase(vo.getId())) {
                                vo.setAmount(assignedResource.getAmount());
                                break;
                            }
                        }
                        resourceSummary.add(vo);
                    }
                }
            }
        } catch (SQLException e) {
            log.debug(e.getMessage());
            log.error("listAssignResourceSummary SQLException SQLState=[{}]", e.getSQLState());
            return resourceSummary;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return resourceSummary;
    }

    public Map<Long, String> getPatientSerResourceMapByPatientSerList(List<Long> patientSerList, String physicistGroupingActivityCode){
        Connection con = null;
        Map<Long, String> resultMap = new HashMap<>();
        try{
            con = ConnectionPool.getConnection();
            resultMap = assignResourceDAO.getPatientSerResourceMapByPatientSerList(con, patientSerList, physicistGroupingActivityCode);
        } catch (SQLException e) {
            log.debug(e.getMessage());
            log.error("getPatientSerResourceMapByPatientSerList SQLException SQLState=[{}]", e.getSQLState());
            return resultMap;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return resultMap;
    }
}
