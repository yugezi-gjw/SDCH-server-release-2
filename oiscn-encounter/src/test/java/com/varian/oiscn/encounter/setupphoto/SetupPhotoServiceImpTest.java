package com.varian.oiscn.encounter.setupphoto;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.util.MockDatabaseConnection;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectionPool.class, SetupPhotoServiceImp.class})
public class SetupPhotoServiceImpTest {

    private SetupPhotoDAO setupPhotoDAO;
    private Connection connection;
    private SetupPhotoServiceImp serviceImp;

    @Before
    public void setup() throws Exception{
        setupPhotoDAO = PowerMockito.mock(SetupPhotoDAO.class);
        PowerMockito.whenNew(SetupPhotoDAO.class).withAnyArguments().thenReturn(setupPhotoDAO);
        serviceImp = new SetupPhotoServiceImp(new UserContext());
        connection = PowerMockito.mock(MockDatabaseConnection.class);
        PowerMockito.mockStatic(ConnectionPool.class);
        PowerMockito.when(ConnectionPool.getConnection()).thenReturn(connection);
    }

    @Test
    public void testQueryByDeviceIdAndHisId() throws SQLException {
        SetupPhotoDTO dto = MockDtoUtil.givenASetupPhoto();
        PowerMockito.when(setupPhotoDAO.queryByDeviceIdAndPatientSer(anyObject(), anyString(), anyLong())).thenReturn(dto);
        SetupPhotoDTO result = serviceImp.queryByDeviceIdAndPatientSer("testDeviceId", 123456L);
        Assert.assertEquals(dto.getPatientSer(), result.getPatientSer());
        Assert.assertEquals(dto.getDeviceId(), result.getDeviceId());
        Assert.assertEquals(dto.getId(), result.getId());
    }

    @Test
    public void testQueryByDeviceIdAndHisIdWhenDAOThrowSQLException() throws SQLException {
        PowerMockito.doThrow(new SQLException()).when(setupPhotoDAO).queryByDeviceIdAndPatientSer(anyObject(), anyString(), anyLong());
        Assert.assertNull(serviceImp.queryByDeviceIdAndPatientSer("testDeviceId", 123456L));
    }

    @Test
    public void givenSetupPhotoDTOThenReturnSetupPhotoId() throws Exception {
        SetupPhotoDTO dto = MockDtoUtil.assembleSetupPhotoDTO();
        String setupPhotoId = "setupPhotoId";
        String resultId;
        boolean result = true;
        String deviceId = "deviceId";
        String hisId = "hisId";
        Long patientSer = 123456L;
        PowerMockito.when(setupPhotoDAO.queryByDeviceIdAndPatientSer(connection, deviceId, patientSer)).thenReturn(dto);
        PowerMockito.when(setupPhotoDAO.saveSetupPhoto(connection, dto)).thenReturn(setupPhotoId);
        PowerMockito.when(setupPhotoDAO.saveSetupPhotoDetail(connection, dto, setupPhotoId)).thenReturn(result);
        resultId = serviceImp.saveSetupPhotosToDB(dto);
        Assert.assertEquals(resultId, setupPhotoId);
    }

    @Test
    public void testDeletePhoto() throws SQLException {
        PowerMockito.doNothing().when(setupPhotoDAO).deletePhoto(anyObject(), anyString());
        Assert.assertTrue(serviceImp.deletePhoto("photoId"));
    }

    @Test
    public void testDeletePhotoWhenSQLException() throws SQLException {
        PowerMockito.doThrow(new SQLException()).when(setupPhotoDAO).deletePhoto(anyObject(), anyString());
        Assert.assertFalse(serviceImp.deletePhoto("photoId"));
    }

    @Test
    public void testClearPhotos() throws SQLException {
        PowerMockito.when(setupPhotoDAO.querySetupPhotoIdsByPatientSer(anyObject(), anyLong())).thenReturn(new ArrayList());
        PowerMockito.doNothing().when(setupPhotoDAO).batchDeletePhotoDetails(anyObject(), anyObject());
        PowerMockito.doNothing().when(setupPhotoDAO).deleteSetupPhotosByPatientSer(anyObject(), anyLong());
        Assert.assertTrue(serviceImp.clearSetupPhotos(13242L));
    }

    @Test
    public void testClearPhotosWhenSQLException() throws SQLException {
        PowerMockito.doThrow(new SQLException()).when(setupPhotoDAO).batchDeletePhotoDetails(anyObject(), anyObject());
        Assert.assertFalse(serviceImp.clearSetupPhotos(13242L));
    }

    @Test
    public void testQueryPhotoByDynamicFormId() throws SQLException {
        List<SetupPhotoArchiveDTO> dtos = MockDtoUtil.assembleSetupPhotoArchives();
        PowerMockito.when(setupPhotoDAO.queryPhotosByDynamicFormId(anyObject(), anyInt())).thenReturn(dtos);
        List<SetupPhotoArchiveDTO> results = setupPhotoDAO.queryPhotosByDynamicFormId(connection, 1);
        SetupPhotoArchiveDTO dto = dtos.get(0);
        SetupPhotoArchiveDTO result = results.get(0);
        Assert.assertEquals(dto.getDynamicFormRecordId(), result.getDynamicFormRecordId());
        Assert.assertEquals(dto.getPhotoId(), result.getPhotoId());
        Assert.assertEquals(dto.getPhoto(), result.getPhoto());
    }

    @Test
    public void queryAchievementPhotoIdListByDynamicFormInstanceId() throws Exception {

        List<String> idList = new ArrayList<>(2);
        idList.add("photo001");
        idList.add("photo002");
        PowerMockito.when(setupPhotoDAO.queryArchivePhotoIdListByDynamicFormRecordId(anyObject(), anyInt())).thenReturn(idList);

        List<String> actual = serviceImp.queryArchivePhotoIdListByDynamicFormRecordId(1234);
        Assert.assertEquals(idList.size(), actual.size());
    }

    @Test
    public void getAchievementPhoto() throws Exception {
        SetupPhotoDetailDTO photo = new SetupPhotoDetailDTO();
        PowerMockito.when(setupPhotoDAO.getArchivePhoto(anyObject(), anyString())).thenReturn(photo);

        SetupPhotoDetailDTO actual = serviceImp.getArchivePhoto("123456");
        Assert.assertEquals(photo, actual);
    }
}
