package com.varian.oiscn.encounter.confirmpayment;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadDAO;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by bhp9696 on 2017/7/31.
 */
@Slf4j
public class ConfirmPaymentServiceImp {
    private ConfirmPaymentDAO confirmPaymentDAO;
    private EncounterDAO encounterDAO;
    private TreatmentWorkloadDAO treatmentWorkloadDAO;
    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;
    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;
    public ConfirmPaymentServiceImp(UserContext userContext) {
        confirmPaymentDAO = new ConfirmPaymentDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
        treatmentWorkloadDAO = new TreatmentWorkloadDAO(userContext);
        appointmentAntiCorruptionServiceImp = new AppointmentAntiCorruptionServiceImp();
        carePathAntiCorruptionServiceImp = new CarePathAntiCorruptionServiceImp();
    }

    /**
     * 排除已经结束治疗的
     * @param patientSer
     * @return
     */
    public ConfirmPayment queryConfirmPaymentByPatientSer(Long patientSer) {
        ConfirmPayment confirmPayment = null;
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            confirmPayment = this.confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer);
            if (confirmPayment != null) {
                int treatmentCount = this.getTotalTreatmentByPatientSer(String.valueOf(patientSer));
                if (treatmentCount > 0 && treatmentCount != confirmPayment.getTreatmentConfirmStatus().getTotalPaymentCount().intValue()) {
                    confirmPayment.getTreatmentConfirmStatus().setTotalPaymentCount(treatmentCount);
                }
            }
        } catch (SQLException e) {
            log.error("queryConfirmPaymentByPatientSer SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return confirmPayment;
    }
    /**
     * 排除已经结束治疗的
     * @param patientSerList
     * @return
     */
    public List<ConfirmPayment> queryConfirmPaymentListByPatientSerList(List<String> patientSerList) {
        List<ConfirmPayment> confirmPaymentList;
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            confirmPaymentList = this.confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection, patientSerList);
            if (confirmPaymentList != null) {
                confirmPaymentList.forEach(confirmPayment -> {
                    int treatmentCount = this.getTotalTreatmentByPatientSer(String.valueOf(confirmPayment.getPatientSer()));
                    if (treatmentCount > 0 && treatmentCount != confirmPayment.getTreatmentConfirmStatus().getTotalPaymentCount().intValue()) {
                        confirmPayment.getTreatmentConfirmStatus().setTotalPaymentCount(treatmentCount);
                    }
                });
            }
        } catch (SQLException e) {
            log.error("queryConfirmPaymentListByPatientSerList SQLException SQLState=[{}]", e.getSQLState());
            confirmPaymentList = new ArrayList<>();
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return confirmPaymentList;
    }
    public ConfirmPayment queryInitConfirmPayment(String patientSer) {
        ConfirmPayment confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(new Long(patientSer));

        List<ActivityCodeConfig> activityCodeConfigList = ActivityCodesReader.getNeedChargeBillActivityCodeList();
        List<ConfirmStatus> confirmStatusList = new ArrayList<>();
//      获取进行中的Encounter对应的CarePathInstanceId
        PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer);
        if(patientEncounterCarePath == null) {
            log.error("No PatientEncounterCarePath Data of patientSer:[{}] ", patientSer);
            return confirmPayment;
        }

        EncounterCarePathList encounterCarePathList = patientEncounterCarePath.getPlannedCarePath();
        if (encounterCarePathList == null) {
            log.error("No EncounterCarePath Data of patientSer:[{}] ", patientSer);
            return confirmPayment;
        }
//        获取该patient对应所有的carePathList
        List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer);
        List<CarePathInstance> activeCarePathList = new ArrayList<>();
        encounterCarePathList.getEncounterCarePathList().forEach(instanceId -> {
            carePathInstanceList.forEach(carePathInstance -> {
                if (carePathInstance.getId().equals(String.valueOf(instanceId.getCpInstanceId()))) {
                    activeCarePathList.add(carePathInstance);
                }
            });
        });
        boolean[] hasDoTreatmentCode = {false};
        String doTreatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
        activeCarePathList.forEach(activityInstance -> {
            activityCodeConfigList.forEach((ActivityCodeConfig activityCodeConfig) -> {
                        // 判断activiteCode是否在activityInstance中
                        if (activityCodeExistCarePath(activityInstance, activityCodeConfig.getName())) {
                            if (!doTreatmentActivityCode.equalsIgnoreCase(activityCodeConfig.getName())) {
                                confirmStatusList.add(new ConfirmStatus(activityCodeConfig.getName(), activityCodeConfig.getContent(), 0, new Long(activityInstance.getId())));
                            } else {
                                hasDoTreatmentCode[0] = true;
                            }
                        }
                    }
            );
        });
        confirmPayment.setConfirmStatusList(confirmStatusList);
        if (StringUtils.isNotEmpty(doTreatmentActivityCode) && hasDoTreatmentCode[0]) {
            ActivityCodeConfig activityCodeConfig = ActivityCodesReader.getActivityCode(doTreatmentActivityCode);
            Integer totalTreatment = getTotalTreatmentByPatientSer(patientSer);
            TreatmentConfirmStatus treatmentConfirmStatus = new TreatmentConfirmStatus(activityCodeConfig.getName(), activityCodeConfig.getContent(), totalTreatment, 0);
            confirmPayment.setTreatmentConfirmStatus(treatmentConfirmStatus);
        }

        return confirmPayment;
    }

    /**
     * 处理多CarePath情况
     * @param confirmPayment
     */
    public void queryMultiCarePathConfirmPayment(ConfirmPayment confirmPayment){
        String patientSer = String.valueOf(confirmPayment.getPatientSer());
        //      获取进行中的Encounter对应的CarePathInstanceId
        PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(patientSer);
        if(patientEncounterCarePath != null) {
            EncounterCarePathList encounterCarePathList = patientEncounterCarePath.getPlannedCarePath();

//        获取该patient对应所有的carePathList
            List<CarePathInstance> carePathInstanceList = carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientSer);
            if (carePathInstanceList.size() <= 1) {
                return;
            }
            List<CarePathInstance> activeCarePathList = new ArrayList<>();
            encounterCarePathList.getEncounterCarePathList().forEach(instanceId -> {
                carePathInstanceList.forEach(carePathInstance -> {
                    if (carePathInstance.getId().equals(String.valueOf(instanceId.getCpInstanceId()))) {
                        activeCarePathList.add(carePathInstance);
                    }
                });
            });

            List<ConfirmStatus> confirmStatusList = confirmPayment.getConfirmStatusList();
            List<ActivityCodeConfig> activityCodeConfigList = ActivityCodesReader.getNeedChargeBillActivityCodeList();
            String doTreatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();

            activeCarePathList.forEach(activityInstance -> {
                activityCodeConfigList.forEach((ActivityCodeConfig activityCodeConfig) -> {
                            if (!doTreatmentActivityCode.equalsIgnoreCase(activityCodeConfig.getName())) {
//             判断activityCode是否在activityInstance中  判断该carePath对应的activityCode是否再confirmStatusList已经存在
                                if (activityCodeExistCarePath(activityInstance, activityCodeConfig.getName()) &&
                                        !existsInConfirmStatusList(activityInstance.getId(), activityCodeConfig.getName(), confirmStatusList)) {
                                    confirmStatusList.add(new ConfirmStatus(activityCodeConfig.getName(), activityCodeConfig.getContent(), 0, new Long(activityInstance.getId())));
                                }
                            }
                        }
                );
            });

            Integer totalTreatment = getTotalTreatmentByPatientSer(patientSer);
            if (confirmPayment.getTreatmentConfirmStatus() != null) {
                if (totalTreatment.compareTo(confirmPayment.getTreatmentConfirmStatus().getTotalPaymentCount()) > 0) {
                    confirmPayment.getTreatmentConfirmStatus().setTotalPaymentCount(totalTreatment);
                }
            }
        }
    }

    private boolean existsInConfirmStatusList(String instanceId,String activityCode,List<ConfirmStatus> confirmStatusList){
        for(ConfirmStatus confirmStatus : confirmStatusList){
            if(confirmStatus.getActivityCode().equals(activityCode) && instanceId.equals(String.valueOf(confirmStatus.getCarePathInstanceId()))){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断activityCode是否在activityInstance中
     * @param carePathInstance
     * @param activityCode
     * @return
     */
    private boolean activityCodeExistCarePath(CarePathInstance carePathInstance ,String activityCode){
        for(ActivityInstance activityInstance:carePathInstance.getActivityInstances()){
            if(activityCode.equals(activityInstance.getActivityCode())){
                return true;
            }
        }
        return false;
    }

    public String saveOrUpdateConfirmPayment(ConfirmPayment confirmPayment) {
        Connection connection = null;
        String saveOrUpdateKey;
        try {
            connection = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(connection,false);
            ConfirmPayment tmp = this.confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, confirmPayment.getPatientSer());
            if (tmp == null) {
                confirmPayment.setEncounterId(encounterDAO.queryByPatientSer(connection, confirmPayment.getPatientSer()).getId());
                saveOrUpdateKey = this.confirmPaymentDAO.create(connection, confirmPayment);
            } else {
                saveOrUpdateKey = tmp.getId();
                confirmPayment.setId(tmp.getId());
                confirmPayment.setEncounterId(tmp.getEncounterId());
                this.confirmPaymentDAO.update(connection, confirmPayment, tmp.getId());
            }
            connection.commit();
        } catch (SQLException e) {
            saveOrUpdateKey = "-1";
            log.error("saveOrUpdateConfirmPayment SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(connection);
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return saveOrUpdateKey;
    }

    public Map<String, Boolean> queryHasContainConfirmPaymentByPatientSerList(List<String> hisIdList) {
        return queryHasContainConfirmPaymentByPatientSerList(hisIdList, null);
    }

    public Map<String, Boolean> queryHasContainConfirmPaymentByPatientSerList(List<String> patientSerList, String code) {
        Map<String, Boolean> confirmPaymentMap = new HashMap<>();
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection, patientSerList);
            if (StringUtils.isNotEmpty(code)) {
                for (ConfirmPayment confirmPayment : confirmPaymentList) {
                    confirmPaymentMap.put(String.valueOf(confirmPayment.getPatientSer()), containConfirmPayment(confirmPayment, code));
                }
            } else {
                for (ConfirmPayment confirmPayment : confirmPaymentList) {
                    confirmPaymentMap.put(String.valueOf(confirmPayment.getPatientSer()), containConfirmPayment(confirmPayment));
                }
            }
            patientSerList.forEach((String patientSer) -> {
                if (!confirmPaymentMap.containsKey(patientSer)) {
                    confirmPaymentMap.put(patientSer, false);
                }
            });
        } catch (SQLException e) {
            log.error("queryHasContainConfirmPaymentByPatientSerList SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return confirmPaymentMap;
    }

    private Boolean containConfirmPayment(ConfirmPayment confirmPayment) {
        List<ConfirmStatus> confirmStatusList = confirmPayment.getConfirmStatusList();
        if (confirmStatusList != null && !confirmStatusList.isEmpty()) {
            for (ConfirmStatus confirmStatus : confirmStatusList) {
                if (confirmStatus.getStatus() > 0) {
                    return true;
                }
            }
        }
        TreatmentConfirmStatus treatmentConfirmStatus = confirmPayment.getTreatmentConfirmStatus();
        return treatmentConfirmStatus != null && treatmentConfirmStatus.getConfirmPaymentCount() > 0;

    }
    public Boolean containConfirmPayment(ConfirmPayment confirmPayment, String code) {
        List<ConfirmStatus> confirmStatusList = confirmPayment.getConfirmStatusList();
        PatientEncounterCarePath patientEncounterCarePath = PatientEncounterHelper.getEncounterCarePathByPatientSer(String.valueOf(confirmPayment.getPatientSer()));
        if(patientEncounterCarePath != null){
            Long currentCarePathInstanceId = patientEncounterCarePath.getPlannedCarePath().getMasterCarePathInstanceId();
            if (confirmStatusList != null && !confirmStatusList.isEmpty()) {
                for (ConfirmStatus confirmStatus : confirmStatusList) {
                    if (confirmStatus.getStatus() > 0 && code.equals(confirmStatus.getActivityCode()) && currentCarePathInstanceId.intValue() == confirmStatus.getCarePathInstanceId().intValue()) {
                        return true;
                    }
                }
            }
        }
        TreatmentConfirmStatus treatmentConfirmStatus = confirmPayment.getTreatmentConfirmStatus();
        return treatmentConfirmStatus != null && treatmentConfirmStatus.getConfirmPaymentCount() > 0
                && code.equals(treatmentConfirmStatus.getActivityCode());
    }
    private Integer getTotalTreatmentByPatientSer(String patientSer) {
        int totalTreatmentCount = 0;
        if (patientSer != null) {
            TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = new TreatmentSummaryAntiCorruptionServiceImp();
            Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(patientSer);
            if (treatmentSummaryDtoOptional.isPresent()) {
                TreatmentSummaryDto treatmentSummaryDto = treatmentSummaryDtoOptional.get();
                List<PlanSummaryDto> plans = treatmentSummaryDto.getPlans();
                if (plans != null && !plans.isEmpty()) {
                    for(PlanSummaryDto planSummaryDto:plans){
                        totalTreatmentCount += planSummaryDto.getPlannedFractions();
                    }
                }
            }
        }
        return totalTreatmentCount;
    }

    /**
     * 对技师列表页面显示是否确费标志
     * @param patientSerList
     * @param activityCode
     * @return
     */
    public Map<String,Boolean> queryAppointmentHasPaymentConfirmForPhysicist(List<String> patientSerList,String activityCode,String deviceId){
        String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
        Map<String,Boolean> confirmStatusMap = new HashMap<>();
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            if (activityCode.equals(treatmentActivityCode)) {
//                Map<String,Integer> hisIdCurTreatmentNumMap = getHisIdTreatmentCount(hisIdList,deviceId);
                List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.queryTreatmentConfirmStatusByPatientSerList(connection,patientSerList,activityCode);
//              查询hisId对应的治疗次数
                Map<String,Integer> patientSerTreatmentCountMap = treatmentWorkloadDAO.queryTotalTreatmentCount(connection,patientSerList);
                confirmPaymentList.forEach(confirmPayment -> {
//                  已经确费次数
                    Integer confirmCount = confirmPayment.getTreatmentConfirmStatus().getConfirmPaymentCount();
                    if(confirmCount != null){
//                      已经治疗的次数
                        Integer treatmentedCount = patientSerTreatmentCountMap.get(String.valueOf(confirmPayment.getPatientSer()));
                        if(treatmentedCount == null){
                            treatmentedCount = 0;
                        }
//                      计算当前患者当天需要治疗的次数
//                        Integer num = hisIdCurTreatmentNumMap.get(confirmPayment.getHisId());
//                        if(num == null){
//                            num = 0;
//                        }
                        if(!confirmStatusMap.containsKey(confirmPayment.getPatientSer())) {
                            if (confirmCount > treatmentedCount) {
                                confirmStatusMap.put(String.valueOf(confirmPayment.getPatientSer()), true);
                            } else {
                                confirmStatusMap.put(String.valueOf(confirmPayment.getPatientSer()), false);
                            }
                        }
                    }
                });
            } else {
                List<ConfirmPayment> confirmPaymentList = confirmPaymentDAO.queryConfirmStatusByPatientSerList(connection,patientSerList,activityCode);
                confirmPaymentList.forEach(confirmPayment ->{
                    if(confirmPayment.getConfirmStatusList() != null && !confirmPayment.getConfirmStatusList().isEmpty()){
                        confirmStatusMap.put(String.valueOf(confirmPayment.getPatientSer()), confirmPayment.getConfirmStatusList().get(0).getStatus() > 0);
                    }else{
                        confirmStatusMap.put(String.valueOf(confirmPayment.getPatientSer()),false);
                    }
                });
            }

            patientSerList.forEach(patientSer -> {
                if(!confirmStatusMap.containsKey(patientSer)){
                    confirmStatusMap.put(patientSer,false);
                }
            });
        }catch (SQLException e){
            log.error("queryAppointmentHasPaymentConfirmForPhysicist SQLException SQLState=[{}]", e.getSQLState());
        }finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return confirmStatusMap;
    }

    /**
     * 根据hisId获取当天治疗的预约次数
     * @param hisIds
     * @return
     */
//    private Map<String,Integer> getHisIdTreatmentCount(List<String> hisIds,String deviceId){
//        Map<String, Integer> map = new HashMap<>();
//        boolean appointmentStoredToLocal = SystemConfigPool.queryStoredTreatmentAppointment2Local();
//        Connection connection = null;
//        try {
//            connection = ConnectionPool.getConnection();
//            if (appointmentStoredToLocal) {//预约存储在本地
//                Date currDate = DateUtil.parse(DateUtil.getCurrentDate());
//                java.util.Calendar calendar = Calendar.getInstance();
//                calendar.setTime(currDate);
//                calendar.add(Calendar.DAY_OF_MONTH, 1);
//                Date tomorrowDate = calendar.getTime();
//
//                map = confirmPaymentDAO.queryTreatmentNumForPatientSerList(connection, hisIds,deviceId, currDate, tomorrowDate);
//            } else {
//                String treatmentActivityCode = SystemConfigPool.queryTreatmentActivityCode();
//                Pagination<AppointmentDto> pagination;
//                String start = DateUtil.getCurrentDate();
//                String end = start;
//                for(String hisId:hisIds) {
//                    pagination = appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPagination(
//                            PatientHelper.getPatientSerByHisId(hisId), treatmentActivityCode,deviceId, start, end, 10, 1, 1);
//                    if (pagination != null && pagination.getLstObject().size() > 0) {
//                        map.put(hisId,pagination.getTotalCount());
//                    }else{
//                        map.put(hisId,0);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            log.error("SQLException SQLState=[{}]", e.getSQLState());
//        } catch (ParseException e) {
//            log.error("getHisIdTreatmentCount Exception: {}", e.getMessage());
//        } finally {
//            DatabaseUtil.safeCloseConnection(connection);
//        }
//        return map;
//    }

}
