package com.varian.oiscn.encounter.treatmentworkload;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.patient.PatientDto;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.Login;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
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
 * Created by BHP9696 on 2017/8/22.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, TreatmentWorkloadServiceImp.class, MockPreparedStatement.class, TreatmentWorkloadDAO.class, BasicDataSourceFactory.class})
public class TreatmentWorkloadServiceImpTest {
    private static final String LOGIN_USERNAME = "liguozhu";
    private static final String LOGIN_NAME = I18nReader.getLocaleValueByKey("TreatmentWorkloadServiceImpTest.liguozhu");
    private TreatmentWorkloadServiceImp treatmentWorkloadServiceImp = null;
    private TreatmentWorkloadDAO treatmentWorkloadDAO;
    private EncounterDAO encounterDAO;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    private PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;
    private Connection connection = null;
    private UserContext userContext = null;

    @Before
    public void setup() {
        try {
            Locale.setDefault(Locale.CHINA);
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            PowerMockito.mockStatic(ConnectionPool.class);
            connection = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
            userContext = PowerMockito.mock(UserContext.class);
            Login login = new Login();
            login.setUsername(LOGIN_USERNAME);
            login.setName(LOGIN_NAME);
            PowerMockito.when(userContext.getLogin()).thenReturn(login);
            treatmentWorkloadDAO = PowerMockito.mock(TreatmentWorkloadDAO.class);
            PowerMockito.whenNew(TreatmentWorkloadDAO.class).withArguments(userContext).thenReturn(treatmentWorkloadDAO);
            encounterDAO = PowerMockito.mock(EncounterDAO.class);
            PowerMockito.whenNew(EncounterDAO.class).withArguments(userContext).thenReturn(encounterDAO);
            treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withNoArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
            patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientSerWhenQueryTreatmentWorkloadByHisIdThenReturnObject() throws SQLException {
        Long patientSer = 20170818004L;
        Long encounterId = 121L;
        TreatmentWorkload treatmentWorkload = new TreatmentWorkload();
        treatmentWorkload.setPatientSer(patientSer);
        treatmentWorkload.setEncounterId(encounterId.toString());
        treatmentWorkload.setId("1");
        List<WorkloadPlan> workloadPlanList = new ArrayList<>();
        workloadPlanList.add(new WorkloadPlan(){{
            setComment("comment");
            setDeliveredFractions(2);
            setPlanId("plan1");
            setSelected((byte)1);
            setWorkloadId(1L);
        }});
        Assert.assertNotNull(workloadPlanList.get(0).getComment());
        Assert.assertNotNull(workloadPlanList.get(0).getDeliveredFractions());
        Assert.assertNotNull(workloadPlanList.get(0).getPlanId());
        Assert.assertNotNull(workloadPlanList.get(0).getSelected());
        Assert.assertNotNull(workloadPlanList.get(0).getWorkloadId());
        PowerMockito.when(treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection, patientSer,encounterId)).thenReturn(workloadPlanList);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer("234");
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(patientDto);
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setPlans(Arrays.asList(new PlanSummaryDto() {{
            setPlannedFractions(20);
            setDeliveredFractions(2);
            setDeliveredDose(20d);
            setPlannedDose(200d);
            setPlanSetupId("planId1");
        }}));
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(treatmentSummaryDto);

        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientSer.toString(),encounterId.toString())).thenReturn(treatmentSummaryDtoOptional);
        PowerMockito.when(this.encounterDAO.queryByPatientSer(connection,patientSer)).thenReturn(new Encounter(){{
            setId(encounterId.toString());
        }});
        treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        TreatmentWorkloadVO treatmentWorkload1 = treatmentWorkloadServiceImp.queryTreatmentWorkloadByPatientSer(patientSer,encounterId);
        Assert.assertNotNull(treatmentWorkload1);
    }

    @Test
    public void givenNotExistsPatientSerInTreatmentWorkloadTableWhenQueryTreatmentWorkloadByHisIdThenReturnObject() throws SQLException {
        Long patientSer = 20170818004L;
        Long encounterId = 121L;
        List<WorkloadPlan> workloadPlanList = new ArrayList<>();
        PowerMockito.when(treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection, patientSer,encounterId)).thenReturn(workloadPlanList);

        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer("234");
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(patientDto);
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setPlans(Arrays.asList(new PlanSummaryDto() {{
            setPlannedFractions(20);
            setDeliveredFractions(1);
            setPlannedDose(200.0);
            setDeliveredDose(20.0);
            setPlanSetupId("planId1");
        }}));
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(treatmentSummaryDto);
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientDto.getPatientSer(),encounterId.toString())).thenReturn(treatmentSummaryDtoOptional);

        treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        TreatmentWorkloadVO treatmentWorkload1 = treatmentWorkloadServiceImp.queryTreatmentWorkloadByPatientSer(patientSer,encounterId);
        Assert.assertNotNull(treatmentWorkload1);
    }

    @Test
    public void givenTreatmentWorkloadObjectWhenSaveOrUpdateTreatmentWorkloadThenReturnBoolean() throws SQLException {
        TreatmentWorkloadVO treatmentWorkload = new TreatmentWorkloadVO();
        Long patientSer = 20170818004L;
        Long encounterId = 121L;
        treatmentWorkload.setPatientSer(patientSer);
        treatmentWorkload.setTreatmentDate("2017-11-11 10:12:33");
        treatmentWorkload.setPlanList(Arrays.asList(new WorkloadPlanVO(){{
            setPlanId("plan1");
            setNum(1);
            setComments("comment");
            setDeliveredDose(12);
            setPlannedDose(300D);
            setPlannedFractions(20);
            setDeliveredFractions(2);
        }}));
        Assert.assertNotNull(treatmentWorkload.getPlanList().get(0).getDeliveredFractions());
        Assert.assertNotNull(treatmentWorkload.getPlanList().get(0).getDeliveredDose());
        Assert.assertNotNull(treatmentWorkload.getPlanList().get(0).getPlannedDose());
        Assert.assertNotNull(treatmentWorkload.getPlanList().get(0).getPlannedFractions());
        Collections.sort(treatmentWorkload.getPlanList());
        treatmentWorkload.setSign(Arrays.asList(new WorkloadSignatureVO(){{
                setName("kevin");
                setTime("2017-12-12");
                setType(WorkloadSignature.SignatureTypeEnum.OPERATORA);
        }},new WorkloadSignatureVO(){{
            setName("rome");
            setTime("2017-12-12");
            setType(WorkloadSignature.SignatureTypeEnum.PHYSICIAN);
        }}));

        treatmentWorkload.setWorker(Arrays.asList("Wilson","Kevin"));
        Assert.assertNotNull(treatmentWorkload.getWorker().get(0));

        Encounter encounter = new Encounter();
        encounter.setId(encounterId.toString());
        PowerMockito.when(treatmentWorkloadDAO.selectLatestWorkloadPlanByPatientSer(connection, patientSer,encounterId)).thenReturn(new ArrayList<WorkloadPlan>());
        PowerMockito.when(this.encounterDAO.queryByPatientSer(connection, patientSer)).thenReturn(encounter);
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientSer(patientSer.toString());
        PowerMockito.when(patientAntiCorruptionServiceImp.queryPatientByPatientId(String.valueOf(patientSer))).thenReturn(patientDto);
        TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
        treatmentSummaryDto.setPlans(Arrays.asList(new PlanSummaryDto() {{
            setPlannedFractions(20);
            setDeliveredFractions(1);
            setPlanSetupId("planId1");
        }}));
        Optional<TreatmentSummaryDto> treatmentSummaryDtoOptional = Optional.of(treatmentSummaryDto);
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientSer.toString(),encounterId.toString())).thenReturn(treatmentSummaryDtoOptional);
        PowerMockito.when(this.treatmentWorkloadDAO.create(Matchers.any(), Matchers.any())).thenReturn("1");
        treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        boolean result = treatmentWorkloadServiceImp.createTreatmentWorkLoad(treatmentWorkload);
        Assert.assertTrue(result);
    }

    @Test
    public void givenPatientSerWhenQueryTreatmentWorkloadListByHisIdThenReturnList() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 121L;
        java.util.Date date = new Date();
        List<TreatmentWorkload> treatmentWorkloadList = Arrays.asList(new TreatmentWorkload(){{
            setPatientSer(patientSer);
            setEncounterId("1212");
            setTreatmentDate(date);
        }});
        Optional<TreatmentSummaryDto> optional = Optional.of(new TreatmentSummaryDto(){{
            setPlans(Arrays.asList(new PlanSummaryDto() {{
                setPlannedFractions(20);
                setDeliveredFractions(1);
                setPlanSetupId("planId1");
            }}));
        }});
        PowerMockito.when(treatmentWorkloadDAO.queryTreatmentWorkloadListByPatientSer(connection,patientSer,encounterId)).thenReturn(treatmentWorkloadList);
        PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientSer.toString(),encounterId.toString())).thenReturn(optional);

        treatmentWorkloadServiceImp = new TreatmentWorkloadServiceImp(userContext);
        List<TreatmentWorkloadVO> result = treatmentWorkloadServiceImp.queryTreatmentWorkloadListByPatientSer(patientSer,encounterId);
        Assert.assertTrue(result.size() == 1);
        Assert.assertTrue(result.get(0).getPatientSer().equals(treatmentWorkloadList.get(0).getPatientSer()));

        PowerMockito.when(treatmentWorkloadDAO.queryTreatmentWorkloadListByPatientSer(connection,patientSer,encounterId)).thenReturn(new ArrayList<>());
        PowerMockito.when(encounterDAO.queryByPatientSer(connection,patientSer)).thenReturn(new Encounter(){{
            setId("22");
        }});
        result = treatmentWorkloadServiceImp.queryTreatmentWorkloadListByPatientSer(patientSer,encounterId);
    }
}
