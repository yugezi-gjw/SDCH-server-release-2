package com.varian.oiscn.encounter.targetvolume;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.targetvolume.TargetVolume;
import com.varian.oiscn.core.targetvolume.TargetVolumeGroupVO;
import com.varian.oiscn.core.targetvolume.TargetVolumeItem;
import com.varian.oiscn.core.targetvolume.TargetVolumeVO;
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by BHP9696 on 2017/7/25.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TargetVolumeServiceImp.class, ConnectionPool.class, BasicDataSourceFactory.class})
public class TargetVolumeServiceImpTest {

    private Connection conn;
    private TargetVolumeDAO targetVolumeDAO;
    private EncounterDAO encounterDAO;
    private TargetVolumeServiceImp targetVolumeServiceImp;

    @Before
    public void setup() {
        try {
            PowerMockito.mockStatic(BasicDataSourceFactory.class);
            targetVolumeDAO = PowerMockito.mock(TargetVolumeDAO.class);
            PowerMockito.whenNew(TargetVolumeDAO.class).withAnyArguments().thenReturn(targetVolumeDAO);
            encounterDAO = PowerMockito.mock(EncounterDAO.class);
            PowerMockito.whenNew(EncounterDAO.class).withAnyArguments().thenReturn(encounterDAO);
            PowerMockito.mockStatic(ConnectionPool.class);
            conn = PowerMockito.mock(MockDatabaseConnection.class);
            PowerMockito.when(ConnectionPool.getConnection()).thenReturn(conn);
            targetVolumeServiceImp = new TargetVolumeServiceImp(new UserContext());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void givenPatientIdAndEncounterIdWhenDaoThrowExceptionThenReturnEmpty() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 121L;
        PowerMockito.when(targetVolumeDAO.selectTargetVolumeByPatientSer(conn, patientSer,encounterId)).thenThrow(SQLException.class);
        TargetVolumeGroupVO targetVolumeGroup = targetVolumeServiceImp.queryTargetVolumeGroupByPatientSer(patientSer,encounterId);
        Assert.assertNull(targetVolumeGroup.getTargetVolumeList());
    }

    @Test
    public void givenPatientIdThenReturnTargetVolumeList() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 121L;
        List<TargetVolume> list = Arrays.asList(new TargetVolume(){{
            setEncounterId(encounterId.toString());
			setPatientSer(patientSer);
            setTargetVolumeItemList(Arrays.asList(new TargetVolumeItem(){{
                setFieldValue("value");
                setFieldId("field1");
                setRNum(1);
                setSeq(1);
            }}));
        }});
        PowerMockito.when(targetVolumeDAO.selectTargetVolumeByPatientSer(conn, patientSer,encounterId)).thenReturn(list);
        TargetVolumeGroupVO targetVolumeGroupVO = targetVolumeServiceImp.queryTargetVolumeGroupByPatientSer(patientSer,encounterId);
        Assert.assertTrue(targetVolumeGroupVO.getTargetVolumeList().size()==1);
    }

    @Test
    public void givenPatientSerThenSaveTargetVolumeThenReturnTrue() throws SQLException {
        TargetVolumeGroupVO targetVolumeGroupVO = new TargetVolumeGroupVO(){{
            setTargetVolumeList(Arrays.asList(new TargetVolumeVO(){{
                setName("GTV");
                setMemo("memo");
                setTargetVolumeItemList(Arrays.asList(new LinkedHashMap<String, String>(){{
                    put("field1","value1");
                    put("field2","value2");
                }}));
            }}));
            setPatientSer(111L);
            setEncounterId("1212");
        }};
        PowerMockito.when(targetVolumeDAO.selectTargetVolumeExceptItemByPatientSer(conn,targetVolumeGroupVO
                .getPatientSer(),new Long(targetVolumeGroupVO.getEncounterId()))).thenReturn(Arrays.asList(new TargetVolume(){{
                    setId(111L);
        }}));
        PowerMockito.when(targetVolumeDAO.batchDelete(conn,Arrays.asList(111L))).thenReturn(true);

        PowerMockito.when(targetVolumeDAO.create(Matchers.any(Connection.class),Matchers.any(List.class)))
                .thenReturn(true);
        boolean ok = targetVolumeServiceImp.saveTargetVolume(targetVolumeGroupVO);
        Assert.assertTrue(ok);

    }

    @Test
    public void testQueryTargetVolumeGroupOnlyTargetVolumeExceptItemByPatientSerThenReturnObject() throws SQLException {
        Long patientSer = 1212L;
        Long encounterId = 121L;
        List<TargetVolume> list = Arrays.asList(new TargetVolume(){{
            setPatientSer(patientSer);
            setEncounterId(encounterId.toString());
            setTargetVolumeItemList(Arrays.asList(new TargetVolumeItem(){{
                setFieldValue("value");
                setFieldId("field1");
                setRNum(1);
                setSeq(1);
            }}));
        }});
        PowerMockito.when(targetVolumeDAO.selectTargetVolumeExceptItemByPatientSer(conn, patientSer,encounterId)).thenReturn(list);
        TargetVolumeGroupVO targetVolumeGroupVO = targetVolumeServiceImp.queryTargetVolumeGroupOnlyTargetVolumeExceptItemByPatientSer(patientSer,encounterId);
        Assert.assertTrue(targetVolumeGroupVO.getTargetVolumeList().size()==1);

    }

}
