package com.varian.oiscn.encounter.isocenter;

import com.varian.oiscn.anticorruption.resourceimps.PatientAntiCorruptionServiceImp;
import com.varian.oiscn.anticorruption.resourceimps.TreatmentSummaryAntiCorruptionServiceImp;
import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.treatmentsummary.PlanSummaryDto;
import com.varian.oiscn.core.treatmentsummary.TreatmentSummaryDto;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ISOCenterServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class ISOCenterServiceImpTest {
    private Connection conn;
    private ISOCenterDAO isoCenterDAO;
    private EncounterDAO encounterDAO;
    private ISOCenterServiceImp isoCenterServiceImp;
    private TreatmentSummaryAntiCorruptionServiceImp treatmentSummaryAntiCorruptionServiceImp;
    PatientAntiCorruptionServiceImp patientAntiCorruptionServiceImp;

    @Before
    public void setup() {
        try {
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            isoCenterDAO = PowerMockito.mock(ISOCenterDAO.class);
            PowerMockito.whenNew(ISOCenterDAO.class).withAnyArguments().thenReturn(isoCenterDAO);
            encounterDAO = PowerMockito.mock(EncounterDAO.class);
            PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
            PowerMockito.mockStatic(ConnectionPool.class);
            conn = PowerMockito.mock(Connection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(conn);
            treatmentSummaryAntiCorruptionServiceImp = PowerMockito.mock(TreatmentSummaryAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(TreatmentSummaryAntiCorruptionServiceImp.class).withAnyArguments().thenReturn(treatmentSummaryAntiCorruptionServiceImp);
            patientAntiCorruptionServiceImp = PowerMockito.mock(PatientAntiCorruptionServiceImp.class);
            PowerMockito.whenNew(PatientAntiCorruptionServiceImp.class).withNoArguments().thenReturn(patientAntiCorruptionServiceImp);
            isoCenterServiceImp = new ISOCenterServiceImp(new UserContext());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientIdThenSearchPlanTrementEmptyWhenThrowsSQLException() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 111L;
        List<ISOPlanTretment> ISOPlanTrementList = new ArrayList<>();
        try {
            TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto();
             Optional<TreatmentSummaryDto> optional = Optional.of(treatmentSummaryDto);
            PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientSer.toString(),encounterId.toString())).thenReturn(optional);
            PowerMockito.when(isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,encounterId)).thenThrow(SQLException.class);
            List<ISOPlanTretment> rlist = isoCenterServiceImp.queryPlanTreatmentByPatientSer(patientSer,encounterId);
            Assert.assertEquals(rlist.size(), 0);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientIdThenSearchPlanTreatmentListNotEmpty() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 111L;
        try {
            List<ISOPlanTretment> ISOPlanTreatmentList = getPlanTreatment();
            ISOCenter isoCenter = new ISOCenter();
            isoCenter.setPatientSer(patientSer);
            isoCenter.setPlanList(ISOPlanTreatmentList);
            PowerMockito.when(isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,encounterId)).thenReturn(isoCenter);

            TreatmentSummaryDto treatmentSummaryDto = new TreatmentSummaryDto(){{
                setPlans(Arrays.asList(new PlanSummaryDto(){{
                    setPlanSetupId("plan1");
                    setPlannedDose(200d);
                    setDeliveredFractions(1);
                    setDeliveredDose(10d);
                }}));
            }};
            Optional<TreatmentSummaryDto> optional = Optional.of(treatmentSummaryDto);
            PowerMockito.when(treatmentSummaryAntiCorruptionServiceImp.getApproveTxSummaryByPatientIdAndEncounterId(patientSer.toString(),encounterId.toString())).thenReturn(optional);
            List<ISOPlanTretment> rlist = isoCenterServiceImp.queryPlanTreatmentByPatientSer(patientSer,encounterId);
            Assert.assertTrue(rlist.size() > 0);
            isoCenter.setPlanList(new ArrayList<>());
            rlist = isoCenterServiceImp.queryPlanTreatmentByPatientSer(patientSer,encounterId);
            Assert.assertTrue(rlist.size() > 0);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientIdAndPlanTrementListThenSuccessSaveOrUpdate() throws SQLException {
        Long patientSer = 1212L;
        String saveOrUpdateId = "100100";
        Long encounterId = 111L;
        List<ISOPlanTretment> ISOPlanTrementList = getPlanTreatment();
        Assert.assertNotNull(ISOPlanTrementList.get(0).getSiteList().get(0).getIsoName());
        Assert.assertNotNull(ISOPlanTrementList.get(0).getSiteList().get(0).getLat());
        Assert.assertNotNull(ISOPlanTrementList.get(0).getSiteList().get(0).getVrt());
        Assert.assertNotNull(ISOPlanTrementList.get(0).getSiteList().get(0).getLng());
        ISOCenter isoCenter = new ISOCenter();
        isoCenter.setPatientSer(patientSer);
        isoCenter.setEncounterId(encounterId.toString());
        isoCenter.setPlanList(ISOPlanTrementList);
        isoCenter.setId(saveOrUpdateId);
        PowerMockito.when(isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,encounterId)).thenReturn(isoCenter);
        String r = isoCenterServiceImp.saveOrUpdateISOCenter(isoCenter);
        Assert.assertNotNull(r);
        Assert.assertTrue(r.equals(saveOrUpdateId));
        PowerMockito.when(isoCenterDAO.selectISOCenterByPatientSer(conn, patientSer,encounterId)).thenReturn(null);
        PowerMockito.when(isoCenterDAO.create(conn,isoCenter)).thenReturn(saveOrUpdateId);
        r = isoCenterServiceImp.saveOrUpdateISOCenter(isoCenter);
        Assert.assertNotNull(r);
        Assert.assertTrue(r.equals(saveOrUpdateId));
    }

    public List<ISOPlanTretment> getPlanTreatment() {
        return Arrays.asList(new ISOPlanTretment() {{
            setPlanId("Lung RA");
            setSiteList(Arrays.asList(new ISOCenterVO() {{
                setIsoName("ISO1");
                setLat(90d);
                setLng(90.2);
                setVrt(34.5);
            }}));
        }}, new ISOPlanTretment() {{
            setPlanId("Node RA");
            setSiteList(Arrays.asList(new ISOCenterVO() {{
                setIsoName("ISO1");
                setLat(80d);
                setLng(30.2);
                setVrt(64.5);
            }}));
        }});
    }
}
