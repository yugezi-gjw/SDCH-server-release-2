package com.varian.oiscn.encounter.setupphoto;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import com.varian.oiscn.encounter.util.MockPreparedStatement;
import com.varian.oiscn.encounter.util.MockResultSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SetupPhotoDAO.class, ConnectionPool.class})
public class SetupPhotoDAOTest {

    private Connection connection;
    private SetupPhotoDAO setupPhotoDAO;

    @Before
    public void setup(){
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        setupPhotoDAO = new SetupPhotoDAO(MockDtoUtil.givenUserContext());
    }

    @Test
    public void testQueryByDeviceIdAndPatientSer() throws SQLException {
        SetupPhotoDTO dto = MockDtoUtil.givenASetupPhoto();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getString("id")).thenReturn(dto.getId());

        SetupPhotoDTO result = setupPhotoDAO.queryByDeviceIdAndPatientSer(connection, dto.getDeviceId(), dto.getPatientSer());
        Assert.assertEquals(dto.getPatientSer(), result.getPatientSer());
        Assert.assertEquals(dto.getId(), result.getId());
        Assert.assertEquals(dto.getDeviceId(), result.getDeviceId());
        Assert.assertEquals(dto.getPatientSer(), result.getPatientSer());
    }

    @Test
    public void testQueryDetailsBySetupPhotoId() throws SQLException {
        SetupPhotoDetailDTO dto = MockDtoUtil.givenASetupPhotoDetail();
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getString("photoId")).thenReturn(dto.getPhotoId());
        PowerMockito.when(resultSet.getBytes("photo")).thenReturn(new byte[]{65, 66, 78, 65, 77});

        List<SetupPhotoDetailDTO> result = setupPhotoDAO.queryDetailsBySetupPhotoId(connection, "id");
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void givenSetupPhotoDTOThenReturnGeneratedId() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(), Matchers.eq(RETURN_GENERATED_KEYS))).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        PowerMockito.when(ps.getGeneratedKeys()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true);
        String setupPhotoId = "setupPhotoId";
        PowerMockito.when(resultSet.getString(1)).thenReturn(setupPhotoId);
        SetupPhotoDTO dto = MockDtoUtil.assembleSetupPhotoDTO();
        Assert.assertEquals(setupPhotoId, setupPhotoDAO.saveSetupPhoto(connection, dto));
    }

    @Test
    public void givenSetupPhotoDTOAndSetupPhotoIdThenSaveSetupPhotoDetails() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString(), Matchers.eq(RETURN_GENERATED_KEYS))).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{1,1});
        String setupPhotoId = "setupPhotoId";
        SetupPhotoDTO dto = MockDtoUtil.assembleSetupPhotoDTO();
        Assert.assertTrue(setupPhotoDAO.saveSetupPhotoDetail(connection, dto, setupPhotoId));
    }

    @Test
    public void testDeletePhoto() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        try {
            setupPhotoDAO.deletePhoto(connection, "testPhotoId");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQueryPhotosByDynamicFormId() throws SQLException {
        List<SetupPhotoArchiveDTO> dtos = MockDtoUtil.assembleSetupPhotoArchives();
        SetupPhotoArchiveDTO dto = dtos.get(0);
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getString("photoId")).thenReturn(dto.getPhotoId());
        PowerMockito.when(resultSet.getBytes("photo")).thenReturn(new byte[]{65, 66, 78, 65, 77});
        List<SetupPhotoArchiveDTO> results = setupPhotoDAO.queryPhotosByDynamicFormId(connection, 1);
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void givenDynamicFormIdAndPatientSerThenArchive() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        setupPhotoDAO.archiveSetupPhotoToDynamicFormRecord(connection, "1", 12121L);
    }

    @Test
    public void testDeleteSetupPhotosByPatientSer() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.when(ps.executeUpdate()).thenReturn(1);
        try {
            setupPhotoDAO.deleteSetupPhotosByPatientSer(connection, 121212L);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testBatchDeletePhotoDetails() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        PowerMockito.doNothing().when(ps).addBatch();
        PowerMockito.when(ps.executeBatch()).thenReturn(new int[]{});
        try {
            setupPhotoDAO.batchDeletePhotoDetails(connection, Arrays.asList("1"));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testQuerySetupPhotoIdsByPatientSer() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getInt(1)).thenReturn(1);
        List<String> result = setupPhotoDAO.querySetupPhotoIdsByPatientSer(connection, 121212L);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("1", result.get(0));
    }


    @Test
    public void queryAchievementPhotoIdListByDynamicFormInstanceId() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getString(1)).thenReturn("id01").thenReturn("id02");
        List<String> actual = setupPhotoDAO.queryArchivePhotoIdListByDynamicFormRecordId(connection, 1234);
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals("id01", actual.get(0));
        Assert.assertEquals("id02", actual.get(1));
    }


    @Test
    public void getAchievementPhoto() throws SQLException {
        PreparedStatement ps = PowerMockito.mock(MockPreparedStatement.class);
        PowerMockito.when(connection.prepareStatement(Matchers.anyString())).thenReturn(ps);
        ResultSet resultSet = PowerMockito.mock(MockResultSet.class);
        PowerMockito.when(ps.executeQuery()).thenReturn(resultSet);
        PowerMockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        PowerMockito.when(resultSet.getBytes("photo")).thenReturn(new byte[]{67, 66, 75, 48});
        SetupPhotoDetailDTO actual = setupPhotoDAO.getArchivePhoto(connection, "photoId");
        Assert.assertEquals(4, actual.getPhoto().length());
    }

}
