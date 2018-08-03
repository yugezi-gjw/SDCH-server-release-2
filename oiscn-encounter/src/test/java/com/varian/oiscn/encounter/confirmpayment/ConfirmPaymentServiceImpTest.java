package com.varian.oiscn.encounter.confirmpayment;

import com.varian.oiscn.anticorruption.resourceimps.AppointmentAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.CarePathAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.base.util.ActivityCodesReader;
import com.varian.oiscn.base.util.SystemConfigConstant;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.activity.ActivityCodeConfig;
import com.varian.oiscn.core.appointment.AppointmentDto;
import com.varian.oiscn.core.appointment.AppointmentStatusEnum;
import com.varian.oiscn.core.carepath.ActivityInstance;
import com.varian.oiscn.core.carepath.CarePathInstance;
import com.varian.oiscn.core.carepath.CarePathStatusEnum;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import com.varian.oiscn.core.pagination.Pagination;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.EncounterCarePathList;
import com.varian.oiscn.encounter.PatientEncounterCarePath;
import com.varian.oiscn.encounter.PatientEncounterHelper;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.treatmentworkload.TreatmentWorkloadDAO;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.util.I18nReader;
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
 * Created by BHP9696 on 2017/8/1.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfirmPaymentServiceImp.class, ConnectionPool.class, ActivityCodesReader.class, BasicDataSourceFactory.class,
        SystemConfigPool.class, PatientEncounterHelper.class})
public class ConfirmPaymentServiceImpTest {
    private ConfirmPaymentDAO confirmPaymentDAO;
    private EncounterDAO encounterDAO;
    private Connection connection;
    private ConfirmPaymentServiceImp confirmPaymentServiceImp;
    private TreatmentWorkloadDAO treatmentWorkloadDAO;
    private CarePathAntiCorruptionServiceImp carePathAntiCorruptionServiceImp;
    private AppointmentAntiCorruptionServiceImp appointmentAntiCorruptionServiceImp;

    @Before
    public void setup() {
        try {
            Locale.setDefault(Locale.CHINA);
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            connection = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
            UserContext userContext = PowerMockito.mock(UserContext.class);
            confirmPaymentDAO = PowerMockito.mock(ConfirmPaymentDAO.class);
            PowerMockito.whenNew(ConfirmPaymentDAO.class).withArguments(userContext).thenReturn(confirmPaymentDAO);
            encounterDAO = PowerMockito.mock(EncounterDAO.class);
            PowerMockito.whenNew(EncounterDAO.class).withArguments(userContext).thenReturn(encounterDAO);
            treatmentWorkloadDAO = PowerMockito.mock(TreatmentWorkloadDAO.class);
            PowerMockito.whenNew(TreatmentWorkloadDAO.class).withAnyArguments().thenReturn(treatmentWorkloadDAO);
            carePathAntiCorruptionServiceImp = PowerMockito.mock(CarePathAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(CarePathAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(carePathAntiCorruptionServiceImp);
            appointmentAntiCorruptionServiceImp = PowerMockito.mock(AppointmentAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(AppointmentAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(appointmentAntiCorruptionServiceImp);
            confirmPaymentServiceImp = new ConfirmPaymentServiceImp(userContext);
            PowerMockito.mockStatic(SystemConfigPool.class);
            PowerMockito.mockStatic(PatientEncounterHelper.class);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenQueryConfirmPaymentByHisIdThenReturnObject() {
        Long patientSer = 8341L;
        try {
            Encounter encounter = new Encounter();
            encounter.setId("9527");
            PowerMockito.when(encounterDAO.queryByPatientSer(connection, patientSer)).thenReturn(encounter);
            ConfirmPayment confirmPayment = new ConfirmPayment();

            confirmPayment.setEncounterId(encounter.getId());
            confirmPayment.setPatientSer(patientSer);
            confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L),
                    new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 0,0L)));
            confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 6));
            PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer))
                    .thenReturn(confirmPayment);

            TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);


            PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
            PatientDto patientDto = new PatientDto();
            patientDto.setPatientSer(patientSer.toString());

            TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
            Optional<TreatmentSummaryDto> treatmentSummaryDtoOpt = Optional.of(treatmentSummaryDto);
            PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(patientSer.toString())).thenReturn(treatmentSummaryDtoOpt);

            ConfirmPayment result = confirmPaymentServiceImp.queryConfirmPaymentByPatientSer(patientSer);
            Assert.assertNotNull(result);
            Assert.assertTrue(patientSer.equals(result.getPatientSer()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenQueryConfirmPaymentByHisIdThrowSQLExceptionThenReturnNull() throws SQLException {
        Long patientSer = 8341L;
        Encounter encounter = new Encounter();
        encounter.setId("9527");
        PowerMockito.when(encounterDAO.queryByPatientSer(connection, patientSer)).thenReturn(encounter);
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer)).thenThrow(SQLException.class);
        ConfirmPayment result = confirmPaymentServiceImp.queryConfirmPaymentByPatientSer(patientSer);
        Assert.assertNull(result);
    }

    @Test
    public void givenPatientSerWhenQueryInitConfirmPaymentThenReturnObject() throws Exception {
        String doTreatment = "DoTreatment";
        List<ActivityCodeConfig> activityCodeConfigList = Arrays.asList(new ActivityCodeConfig() {{
            setName("DoImmobilization");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName("DoCTSim");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName("DoRepositioning");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoRepositioning"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName(doTreatment);
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"));
            setNeedChargeBill(true);
        }});
        Long patientSer = 12121L;
        PowerMockito.mockStatic(ActivityCodesReader.class);
        PowerMockito.when(ActivityCodesReader.getNeedChargeBillActivityCodeList()).thenReturn(activityCodeConfigList);
        PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(patientSer.toString());

        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer(patientSer.toString());
            setCompletedCarePath(Arrays.asList(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(10L);
                    setCpInstanceId(12L);
                }}));
            }}));
        }});
        List<CarePathInstance> carePathInstanceList = Arrays.asList(new CarePathInstance(){{
            setId("10");
            setActivityInstances(Arrays.asList(new ActivityInstance(){{
                setActivityCode(doTreatment);
            }}));
        }});
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(patientDto.getPatientSer())).thenReturn(carePathInstanceList);


        TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);

        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setPlans(Arrays.asList(new PlanSummaryDto() {{
            setPlannedFractions(20);
        }}));
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(treatmentSummaryDto);

        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(patientSer.toString())).thenReturn(treatmentSummaryDtoOptional);
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn(doTreatment);
        PowerMockito.when(ActivityCodesReader.getActivityCode(doTreatment)).thenReturn(new ActivityCodeConfig(){{
            setName(doTreatment);
            setContent(doTreatment);
        }});
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer(patientSer.toString());
            setCompletedCarePath(Arrays.asList(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(10L);
                    setCpInstanceId(12L);
                }}));
            }}));
        }});
        ConfirmPayment confirmPayment = confirmPaymentServiceImp.queryInitConfirmPayment(patientSer.toString());
        Assert.assertNotNull(confirmPayment);
    }

    @Test
    public void givenNewConfirmPaymentWhenSaveOrUpdateConfirmPaymentThenReturnPrimarykey() throws SQLException {
        Long patientSer = 201707140001L;
        String encounterId = "533";
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer)).thenReturn(null);
        ConfirmPayment confirmPayment = new ConfirmPayment(){{
            setPatientSer(patientSer);
        }};
        Encounter encounter = new Encounter();
        encounter.setId(encounterId);
        PowerMockito.when(encounterDAO.queryByPatientSer(connection, patientSer)).thenReturn(encounter);
        PowerMockito.when(confirmPaymentDAO.create(connection, confirmPayment)).thenReturn("3");
        String primarykey = confirmPaymentServiceImp.saveOrUpdateConfirmPayment(confirmPayment);
        Assert.assertTrue(Integer.parseInt(primarykey) == 3);
    }

    @Test
    public void givenConfirmPaymentWhenSaveOrUpdateConfirmPaymentThenReturnPrimarykey() throws SQLException {
        Long patientSer = 201707140001L;
        String encounterId = "533";
        String id = "3";
        ConfirmPayment confirmPayment = new ConfirmPayment();
        confirmPayment.setId(id);
        confirmPayment.setEncounterId(encounterId);
        confirmPayment.setPatientSer(patientSer);
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSer(connection, patientSer)).thenReturn(confirmPayment);
        PowerMockito.when(encounterDAO.queryByPatientSer(connection,patientSer)).thenReturn(new Encounter(){{
            setId(encounterId);
        }});
        PowerMockito.when(confirmPaymentDAO.update(connection, confirmPayment, id)).thenReturn(true);
        String primarykey = confirmPaymentServiceImp.saveOrUpdateConfirmPayment(confirmPayment);
        Assert.assertTrue(Integer.parseInt(primarykey) == 3);
    }

    @Test
    public void givenPatientSerListWhenQueryHasContainConfirmPaymentByHisIdsThenReturnMap() throws SQLException {
        List<Long> patientSerList = Arrays.asList(201707140002L, 201707140003L, 201707140004L);
        List<String> patientSerListStr = new ArrayList<>();
        patientSerList.forEach(patientSer->patientSerListStr.add(String.valueOf(patientSer)));
        List<ConfirmPayment> confirmPaymentList = new ArrayList<>();
        ConfirmPayment confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(0));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 1,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 0));
        confirmPaymentList.add(confirmPayment);

        confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(1));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 0,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 0));
        confirmPaymentList.add(confirmPayment);


        confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(2));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 0,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 2));
        confirmPaymentList.add(confirmPayment);
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection, patientSerListStr)).thenReturn(confirmPaymentList);

        Map<String, Boolean> result = confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(patientSerListStr);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.get(patientSerList.get(0).toString()));
        Assert.assertFalse(result.get(patientSerList.get(1).toString()));
        Assert.assertTrue(result.get(patientSerList.get(2).toString()));

    }

    @Test
    public void givenPatientSerListWhenQueryHasContainConfirmPaymentByHisIdsAndCodeThenReturnMap() throws SQLException {
        List<Long> patientSerList = Arrays.asList(201707140002L, 201707140003L, 201707140004L);
        List<String> patientSerListStr = new ArrayList<>();
        patientSerList.forEach(patientSer->patientSerListStr.add(String.valueOf(patientSer)));
        List<ConfirmPayment> confirmPaymentList = new ArrayList<>();
        ConfirmPayment confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(0));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 1,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 0));
        confirmPaymentList.add(confirmPayment);

        confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(1));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 0,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 0));
        confirmPaymentList.add(confirmPayment);


        confirmPayment = new ConfirmPayment();
        confirmPayment.setPatientSer(patientSerList.get(2));
        confirmPayment.setConfirmStatusList(Arrays.asList(new ConfirmStatus("DoImmobilization", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"), 0,0L)
                , new ConfirmStatus("DoCTSim", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"), 0,0L)));
        confirmPayment.setTreatmentConfirmStatus(new TreatmentConfirmStatus("DoFirstTreatment", I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"), 30, 2));
        confirmPaymentList.add(confirmPayment);
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection, patientSerListStr)).thenReturn(confirmPaymentList);

        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("121212");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(11L);
                    setCpInstanceId(11L);
                    setCrtTime(new Date());
                }}));
            }});
            setCompletedCarePath(Arrays.asList(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(10L);
                    setCpInstanceId(12L);
                    setCrtTime(new Date());
                }}));
            }}));
        }});
        Map<String, Boolean> result = confirmPaymentServiceImp.queryHasContainConfirmPaymentByPatientSerList(patientSerListStr, "DoCTSim");
        Assert.assertNotNull(result);
        Assert.assertFalse(result.get(patientSerList.get(1).toString()));
        Assert.assertFalse(result.get(patientSerList.get(1).toString()));
        Assert.assertFalse(result.get(patientSerList.get(2).toString()));

    }

    @Test
    public void givenPatientSerListWhenQueryAppointmentHasPaymentConfirmForPhysicistThenReturnMap() throws SQLException {
        final String activityCode = "DoTreatment";
        List<Long> patientSerList = Arrays.asList(201707140002L, 201707140003L, 201707140004L);
        List<String> patientSerListStr = new ArrayList<>();
        patientSerList.forEach(patientSer->patientSerListStr.add(String.valueOf(patientSer)));
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(Arrays.asList(activityCode));
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.APPOINTMENT_STORED_TO_LOCAL)).thenReturn(Arrays.asList("true"));
        Map<String, Integer> map = new HashMap<>();
        map.put(patientSerListStr.get(0), 2);
        PowerMockito.when(confirmPaymentDAO.queryTreatmentNumForPatientSerList(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any())).thenReturn(map);
        PowerMockito.when(confirmPaymentDAO.queryTreatmentConfirmStatusByPatientSerList(connection, patientSerListStr, activityCode)).thenReturn(Arrays.asList(new ConfirmPayment() {{
            setPatientSer(patientSerList.get(0));
            setTreatmentConfirmStatus(new TreatmentConfirmStatus() {{
                setConfirmPaymentCount(1);
            }});
        }}));
        Map<String, Integer> map1 = new HashMap<>();
        map1.put(patientSerListStr.get(0), 1);
        PowerMockito.when(treatmentWorkloadDAO.queryTotalTreatmentCount(Matchers.any(), Matchers.any())).thenReturn(map1);
        Map<String, Boolean> rmap = confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(patientSerListStr, activityCode, "1212");
        Assert.assertNotNull(rmap);
        Assert.assertFalse(rmap.get(patientSerListStr.get(0)));

        final String activityCode2 = "DoCT";

        PowerMockito.when(confirmPaymentDAO.queryConfirmStatusByPatientSerList(connection, patientSerListStr, activityCode2)).thenReturn(Arrays.asList(new ConfirmPayment() {
            {
                setPatientSer(patientSerList.get(0));
                setConfirmStatusList(Arrays.asList(new ConfirmStatus() {{
                    setActivityCode(activityCode2);
                    setStatus(1);
                }}));
            }
        }));
        PowerMockito.when(SystemConfigPool.queryConfigValueByName(SystemConfigConstant.TREATMENT_ACTIVITY_CODE)).thenReturn(Arrays.asList(activityCode));
        rmap = confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(patientSerListStr, activityCode2, "1212");
        Assert.assertNotNull(rmap);
        Assert.assertTrue(rmap.get(patientSerListStr.get(0)));

        PowerMockito.when(SystemConfigPool.queryStoredTreatmentAppointment2Local()).thenReturn(false);
        Pagination<AppointmentDto> pagination = new Pagination<AppointmentDto>(){{
            setTotalCount(1);
            setLstObject(Arrays.asList(new AppointmentDto(){{
                setStatus(AppointmentStatusEnum.getDisplay(AppointmentStatusEnum.BOOKED));
                setReason(activityCode);
            }}));
        }};
        PowerMockito.when(appointmentAntiCorruptionServiceImp.queryAppointmentListByPatientIdAndActivityCodeAndDeviceIdAndDateRangeAndPagination(Matchers.anyString(),Matchers.anyString(),Matchers.anyString(),Matchers.anyString(),Matchers.anyString(),Matchers.anyInt(),Matchers.anyInt(),Matchers.anyInt())).thenReturn(pagination);
        rmap = confirmPaymentServiceImp.queryAppointmentHasPaymentConfirmForPhysicist(patientSerListStr, activityCode, "1212");

        Assert.assertNotNull(rmap);
    }

    @Test
    public void testQueryMultiCarePathConfirmPayment() throws Exception {
        String doTreatment = "DoTreatment";
        PowerMockito.when(SystemConfigPool.queryTreatmentActivityCode()).thenReturn(doTreatment);
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("1212");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(11L);
                    setCpInstanceId(11L);
                    setCrtTime(new Date());
                }}));
            }});
            setCompletedCarePath(Arrays.asList(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(10L);
                    setCpInstanceId(12L);
                    setCrtTime(new Date());
                }}));
            }}));
        }});

        List<CarePathInstance> carePathInstanceList = Arrays.asList(new CarePathInstance(){{
            setId("10");
            setActivityInstances(Arrays.asList(new ActivityInstance(){{
                setActivityCode(doTreatment);
                setStatus(CarePathStatusEnum.COMPLETED);
                setId("12");
                setPrevActivities(Arrays.asList("1212"));
                setNextActivities(Arrays.asList("1112"));
            }},new ActivityInstance(){{
                setActivityCode(doTreatment);
                setStatus(CarePathStatusEnum.ACTIVE);
                setId("13");
                setPrevActivities(Arrays.asList("12121"));
                setNextActivities(Arrays.asList("11122"));
            }}));
        }});
        PowerMockito.when(carePathAntiCorruptionServiceImp.queryAllCarePathByPatientID(Matchers.anyString())).thenReturn(carePathInstanceList);
        List<ActivityCodeConfig> activityCodeConfigList = Arrays.asList(new ActivityCodeConfig() {{
            setName("DoImmobilization");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoImmobilization"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName("DoCTSim");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoCTSim"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName("DoRepositioning");
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoRepositioning"));
            setNeedChargeBill(true);
        }}, new ActivityCodeConfig() {{
            setName(doTreatment);
            setContent(I18nReader.getLocaleValueByKey("ConfirmPaymentServiceImpTests.DoFirstTreatment"));
            setNeedChargeBill(true);
        }});
        PowerMockito.mockStatic(ActivityCodesReader.class);
        PowerMockito.when(ActivityCodesReader.getNeedChargeBillActivityCodeList()).thenReturn(activityCodeConfigList);
        TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto(){{
            setPlans(Arrays.asList(new PlanSummaryDto(){{
                setPlannedFractions(10);
            }},new PlanSummaryDto(){{
                setPlannedFractions(15);
            }}));
        }};
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOpt = Optional.of(treatmentSummaryDto);
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(Matchers.anyString())).thenReturn(treatmentSummaryDtoOpt);
        ConfirmPayment confirmPayment = new ConfirmPayment(){{
           setHisId("hisId");
           setTreatmentConfirmStatus(new TreatmentConfirmStatus(){{
               setTotalPaymentCount(20);
           }});
        }};
        confirmPaymentServiceImp.queryMultiCarePathConfirmPayment(confirmPayment);
    }
    @Test
    public void testContainConfirmPayment(){
        PowerMockito.when(PatientEncounterHelper.getEncounterCarePathByPatientSer(Matchers.anyString())).thenReturn(new PatientEncounterCarePath(){{
            setPatientSer("1212");
            setPlannedCarePath(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(11L);
                    setCpInstanceId(11L);
                    setCrtTime(new Date());
                }}));
            }});
            setCompletedCarePath(Arrays.asList(new EncounterCarePathList(){{
                setEncounterId(10L);
                setEncounterCarePathList(Arrays.asList(new EncounterCarePath(){{
                    setCategory(EncounterCarePathCategoryEnum.PRIMARY);
                    setEncounterId(10L);
                    setCpInstanceId(12L);
                    setCrtTime(new Date());
                }}));
            }}));
        }});
        ConfirmPayment confirmPayment = new ConfirmPayment(){{
            setConfirmStatusList(Arrays.asList(new ConfirmStatus(){{
                setStatus(1);
                setActivityCode("DoCT");
                setCarePathInstanceId(10L);
            }}));
        }};
        boolean conatain = confirmPaymentServiceImp.containConfirmPayment(confirmPayment,"DoCT");
        Assert.assertFalse(conatain);
    }

    @Test
    public void testQueryConfirmPaymentListByPatientSerList() throws Exception {
        List<ConfirmPayment> confirmPaymentList = Arrays.asList(new ConfirmPayment());
        List<String> patientSerList = Arrays.asList("111","112");
        PowerMockito.when(confirmPaymentDAO.selectConfirmPaymentByPatientSerList(connection,patientSerList)).thenReturn(confirmPaymentList);

        TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
        PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(new TreatmentSummaryDto());
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getActivityEncounterTxSummaryByPatientSer(Matchers.anyString())).thenReturn(treatmentSummaryDtoOptional);
        List<ConfirmPayment> list = confirmPaymentServiceImp.queryConfirmPaymentListByPatientSerList(patientSerList);
        Assert.assertEquals(confirmPaymentList,list);
    }
}
