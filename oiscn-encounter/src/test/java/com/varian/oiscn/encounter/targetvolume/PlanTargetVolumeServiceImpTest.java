package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.targetvolume.PlanTargetVolume;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeInfo;
import com.varian.oiscn.core.targetvolume.PlanTargetVolumeVO;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.EncounterDAO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bhp9696 on 2018/3/1.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PlanTargetVolumeServiceImp.class,ConnectionPool.class, BasicDataSourceFactory.class})
public class PlanTargetVolumeServiceImpTest {
    private Connection conn;
    private PlanTargetVolumeDAO planTargetVolumeDAO;
    private EncounterDAO encounterDAO;
    private PlanTargetVolumeServiceImp planTargetVolumeServiceImp;


    @Before
    public void setup() {
        try {
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            planTargetVolumeDAO = PowerMockito.mock(PlanTargetVolumeDAO.class);
            PowerMockito.whenNew(PlanTargetVolumeDAO.class).withAnyArguments().thenReturn(planTargetVolumeDAO);
            encounterDAO = PowerMockito.mock(EncounterDAO.class);
            PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
            PowerMockito.mockStatic(ConnectionPool.class);
            conn = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(conn);
            planTargetVolumeServiceImp = new PlanTargetVolumeServiceImp(new UserContext());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSavePlanTargetVolumeNameThenReturnTrue() throws SQLException {
        Long patientSer = 12121L;
        Long encounterId = 121212L;
        PlanTargetVolumeVO planTargetVolumeVO = new PlanTargetVolumeVO(){{
            setPatientSer(String.valueOf(patientSer));
            setPlanTargetVolumeList(Arrays.asList(new PlanTargetVolumeInfo(){{
                setPlanId("planId1");
                setNameList(Arrays.asList("baqu1","baqu2"));
            }}));
        }};
        PowerMockito.when(encounterDAO.queryByPatientSer(conn,patientSer)).thenReturn(new Encounter(){{
            setId(String.valueOf(encounterId));
        }});
        PowerMockito.when(planTargetVolumeDAO.create(Matchers.any(Connection.class), Matchers.any(List.class))).thenReturn(true);
        boolean ok = planTargetVolumeServiceImp.savePlanTargetVolumeName(planTargetVolumeVO);
        Assert.assertTrue(ok);

        planTargetVolumeVO = new PlanTargetVolumeVO(){{
            setPatientSer(String.valueOf(patientSer));
            setPlanTargetVolumeList(Arrays.asList(new PlanTargetVolumeInfo(){{
                setPlanId("planId1");
                setNameList(new ArrayList<>());
            }}));
        }};
        PowerMockito.when(planTargetVolumeDAO.delete(conn,patientSer,encounterId)).thenReturn(1);
        ok = planTargetVolumeServiceImp.savePlanTargetVolumeName(planTargetVolumeVO);
        Assert.assertTrue(ok);
    }

    @Test
    public void testQueryPlanTargetVolumeMappingByPatientSer() throws SQLException {
        Long patientSer = 12121L;
        Long encounterId = 1900L;
        List<PlanTargetVolume> planTargetVolumeList = Arrays.asList(new PlanTargetVolume(){{
            setPlanId("planId");
            setPatientSer(patientSer);
            setEncounterId(encounterId.toString());
            setTargetVolumeName("baqu1");
        }},new PlanTargetVolume(){{
            setPlanId("planId");
            setPatientSer(patientSer);
            setEncounterId(encounterId.toString());
            setTargetVolumeName("baqu2");
        }});
        PowerMockito.when(planTargetVolumeDAO.queryPlanTargetVolumeListByPatientSer(conn,patientSer,encounterId)).thenReturn(planTargetVolumeList);
        PlanTargetVolumeVO planTargetVolumeVO = planTargetVolumeServiceImp.queryPlanTargetVolumeMappingByPatientSer(patientSer,encounterId);
        Assert.assertNotNull(planTargetVolumeVO);
        Assert.assertNotNull(planTargetVolumeVO.getPlanTargetVolumeList());
        Assert.assertNotNull(planTargetVolumeVO.getPlanTargetVolumeList().size() == 1);
        Assert.assertNotNull(planTargetVolumeVO.getPlanTargetVolumeList().get(0).getNameList());
        Assert.assertNotNull(planTargetVolumeVO.getPlanTargetVolumeList().get(0).getNameList().size() == 2);
    }

}
