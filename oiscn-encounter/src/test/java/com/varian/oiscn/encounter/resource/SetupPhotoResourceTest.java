package com.varian.oiscn.encounter.resource;

import com.varian.oiscn.base.cache.CacheManager;
import com.varian.oiscn.base.cache.CacheManagerUtils;
import com.varian.oiscn.base.cache.CacheNameConstants;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoArchiveDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoDetailDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoServiceImp;
import com.varian.oiscn.encounter.util.MockDtoUtil;
import io.dropwizard.setup.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;

/**
 * Created by gbt1220 on 1/8/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SetupPhotoResource.class, CacheManagerUtils.class})
public class SetupPhotoResourceTest {

    private Configuration configuration;

    private Environment environment;

    private SetupPhotoServiceImp serviceImp;

    private SetupPhotoResource resource;

    @Before
    public void setup() throws Exception {
        configuration = PowerMockito.mock(Configuration.class);
        environment = PowerMockito.mock(Environment.class);
        serviceImp = PowerMockito.mock(SetupPhotoServiceImp.class);
        PowerMockito.whenNew(SetupPhotoServiceImp.class).withAnyArguments().thenReturn(serviceImp);
        resource = new SetupPhotoResource(configuration, environment);
        PowerMockito.mockStatic(CacheManagerUtils.class);
    }

    @Test
    public void testInitCache() {
        SetupPhotoDTO dto = MockDtoUtil.givenASetupPhoto();
        PowerMockito.when(serviceImp.queryByDeviceIdAndPatientSer(anyString(), anyLong())).thenReturn(dto);
        Response response = resource.initCache(new UserContext(), "deviceId", 123456L);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));

        response = resource.initCache(new UserContext(), "deviceId", null);
        assertThat(response.getStatus(),equalTo(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void testInitCacheWhenDTOIsNull() {
        PowerMockito.when(serviceImp.queryByDeviceIdAndPatientSer(anyString(), anyLong())).thenReturn(null);
        Response response = resource.initCache(new UserContext(), "deviceId", 123456L);
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
    }

    @Test
    public void testUploadNormal() {
        String deviceId = "deviceId";
        SetupPhotoDTO cache = new SetupPhotoDTO();
        PowerMockito.mockStatic(CacheManagerUtils.class);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);

        Response actual = resource.upload(deviceId, new SetupPhotoDetailDTO("no", "2Ji75KA2qBcJxEkwv3SCeQ=="));
        Assert.assertEquals(Response.Status.OK, actual.getStatusInfo());
    }

    @Test
    public void testUploadBadRequest() {
        String deviceId = "deviceId";
        SetupPhotoDTO cache = new SetupPhotoDTO();
        CacheManagerUtils.put(CacheNameConstants.SETUP_PHOTO, deviceId, cache);

        Response actual = resource.upload(null, null);
        Assert.assertEquals(Response.Status.BAD_REQUEST, actual.getStatusInfo());
    }

    @Test
    public void testUploadWithBadCache() {
        String deviceId = "deviceId";

        PowerMockito.mockStatic(CacheManagerUtils.class);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(null);

        Response actual = resource.upload(deviceId, new SetupPhotoDetailDTO("abcd", "2Ji75KA2qBcJxEkwv3SCeQ=="));
        Assert.assertEquals(Response.Status.BAD_REQUEST, actual.getStatusInfo());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetPhotoIdListFromCacheNormal() {
        String deviceId = "deviceId";
        SetupPhotoDTO cache = new SetupPhotoDTO();
        cache.addPhoto("1111", "1111111111111111111");
        cache.addPhoto("2222", "2222222222222222222222222");
        cache.addPhoto("3333", "333333333333333333");
        cache.addPhoto("4444", "44444444444444444444444");
        cache.addPhoto("5555", "5555555555555555555555544444");
        PowerMockito.mockStatic(CacheManagerUtils.class);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);

        Response actual = resource.listPhotoIds(deviceId);
        Assert.assertEquals(Response.Status.OK, actual.getStatusInfo());
        Object entity = actual.getEntity();
        Assert.assertNotNull(entity);
        if (entity instanceof List) {
            List<String> result = (List<String>) entity;
            Assert.assertNotNull(result);
            Assert.assertEquals(5, result.size());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetPhotoFromCacheNormal() {
        String deviceId = "deviceId";
        String photoId = "2222";
        SetupPhotoDTO cache = new SetupPhotoDTO();
        cache.addPhoto("1111", "1111111111111111111");
        cache.addPhoto("2222", "2222222222222222222222222");
        cache.addPhoto("3333", "333333333333333333");
        cache.addPhoto("4444", "44444444444444444444444");
        cache.addPhoto("5555", "5555555555555555555555544444");
        PowerMockito.mockStatic(CacheManagerUtils.class);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);

        Response actual = resource.getPhoto(deviceId, photoId);
        Assert.assertEquals(Response.Status.OK, actual.getStatusInfo());
        Object entity = actual.getEntity();
        Assert.assertNotNull(entity);
        if (entity instanceof Map) {
            Map<String, String> result = (Map<String, String>) entity;
            Assert.assertEquals(photoId, result.get("id"));
            Assert.assertEquals("2222222222222222222222222", result.get("photo"));
        }
    }

    @Test
    public void testGetPhotoFromCacheWithBadCache() {
        String deviceId = "deviceId";
        String photoId = "photoId";

        PowerMockito.mockStatic(CacheManagerUtils.class);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(null);

        Response actual = resource.getPhoto(deviceId, photoId);
        Assert.assertEquals(Response.Status.BAD_REQUEST, actual.getStatusInfo());
    }

//    @Test
//    public void testSave() {
//        SetupPhotoDTO dto = MockDtoUtil.givenASetupPhoto();
//        String deviceId = PowerMockito.mock(String.class);
//        PowerMockito.mockStatic(CacheManagerUtils.class);
//        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(dto);
//        Response response = resource.save(new UserContext(), "deviceId", "hisId");
//        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
//    }

//    @Test
//    public void testSaveWhenDTOIsNull() {
//        String deviceId = PowerMockito.mock(String.class);
//        PowerMockito.mockStatic(CacheManagerUtils.class);
//        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(null);
//        Response response = resource.save(new UserContext(), "deviceId", "hisId");
//        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
//    }

    @Test
    public void testDeleteWithoutAuthWhenDeviceIdOrPhotoIdIsNull() {
        Response response = resource.deletePhoto("", "photoId");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));

        response = resource.deletePhoto("deviceId", "");
        assertThat(response.getStatusInfo(), equalTo(Response.Status.BAD_REQUEST));
    }

    @Test
    public void testDeleteWithoutAuth() {
        SetupPhotoDTO dto = givenAPhotoDto();
        PowerMockito.when(CacheManagerUtils.get(anyString(), any())).thenReturn(dto);

        Response response = resource.deletePhoto(dto.getDeviceId(), dto.getPhotos().get(0).getPhotoId());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        Assert.assertEquals(0, dto.getPhotos().size());
    }

    @Test
    public void testDelete() throws Exception {
        SetupPhotoDTO dto = givenAPhotoDto();
        PowerMockito.when(CacheManagerUtils.get(anyString(), any())).thenReturn(dto);
        PowerMockito.when(serviceImp.deletePhoto(anyString())).thenReturn(true);

        Response response = resource.deletePhoto(new UserContext(), dto.getDeviceId(), dto.getPhotos().get(0).getPhotoId());
        assertThat(response.getStatusInfo(), equalTo(Response.Status.OK));
        Assert.assertEquals(0, dto.getPhotos().size());
    }

    private SetupPhotoDTO givenAPhotoDto() {
        SetupPhotoDTO dto = MockDtoUtil.givenASetupPhoto();
        SetupPhotoDetailDTO detailDTO = MockDtoUtil.givenASetupPhotoDetail();
        List<SetupPhotoDetailDTO> detailDTOList = new ArrayList<>();
        detailDTOList.add(detailDTO);
        dto.setPhotos(detailDTOList);
        return dto;
    }

    @Test
    public void listAchievementPhotoIds() throws Exception {

        List<String> idList = new ArrayList<>(2);
        idList.add("photo001");
        idList.add("photo002");
        PowerMockito.when(serviceImp.queryArchivePhotoIdListByDynamicFormRecordId(anyInt())).thenReturn(idList);

        Response response = resource.listArchivePhotoIds(new UserContext(), "123456");
        Assert.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Object entity = response.getEntity();
        List<String> actual = (List) entity;
        Assert.assertEquals(idList.size(), actual.size());

        response = resource.listArchivePhotoIds(new UserContext(), "");
        Assert.assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
    }

    @Test
    public void getAchievementPhoto() throws Exception {
        SetupPhotoDetailDTO photo = new SetupPhotoDetailDTO();
        PowerMockito.when(serviceImp.getArchivePhoto(anyString())).thenReturn(photo);

        Response response = resource.getArchivePhoto(new UserContext(), "123456");
        Assert.assertEquals(response.getStatusInfo(), Response.Status.OK);
        Object entity = response.getEntity();
        SetupPhotoDetailDTO actual = (SetupPhotoDetailDTO) entity;
        Assert.assertEquals(photo, actual);

        response = resource.getArchivePhoto(new UserContext(), "");
        Assert.assertEquals(response.getStatusInfo(), Response.Status.BAD_REQUEST);
    }

    @Test
    public void givenDeviceIdAndHisIdThenRemoveFromCacheManager(){
        CacheManager<String, SetupPhotoDTO> cacheManager = new CacheManager<>(CacheNameConstants.SETUP_PHOTO);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO)).thenReturn(cacheManager);
        String deviceId = "deviceId";
        Long patientSerStr = 123456L;
        cacheManager.put(deviceId, new SetupPhotoDTO());
        Assert.assertEquals(new HashMap<>(), resource.disposeCache(new UserContext(), deviceId, patientSerStr).getEntity());
        Assert.assertEquals(false, cacheManager.isContains(deviceId));

        Response response = resource.disposeCache(new UserContext(),deviceId,null);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO)).thenReturn(null);
        response = resource.disposeCache(new UserContext(), deviceId, patientSerStr);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
    }
    @Test
    public void testListPhotoIds(){
        String deviceId = "deviceId";
        Response response = resource.listPhotoIds(new UserContext(),null);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        SetupPhotoDTO setupPhotoDTO  = new SetupPhotoDTO();
        List<SetupPhotoDetailDTO> detailList = Arrays.asList(new SetupPhotoDetailDTO(){{
            setPhotoId("12121");
        }});
        setupPhotoDTO.setPhotos(detailList);
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO,deviceId)).thenReturn(setupPhotoDTO);
        response = resource.listPhotoIds(new UserContext(),deviceId);
        List<String> list = (List<String>) response.getEntity();
        assertThat("12121",equalTo(list.get(0)));
    }

    @Test
    public void testGetPhoto(){
        String deviceId = "121212";
        String photoId = "1111";
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(null);
        Response response = resource.getPhoto(new UserContext(),deviceId,photoId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        SetupPhotoDTO cache = new SetupPhotoDTO(){{
            setPhotos(Arrays.asList(new SetupPhotoDetailDTO(){{
                setPhotoId(photoId);
                setPhoto("photo");
            }}));
        }};
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);
        response = resource.getPhoto(new UserContext(),deviceId,photoId);
        Map<String, String> resp = (Map<String, String>) response.getEntity();
        assertThat(resp.get("id"),equalTo(photoId));
    }

    @Test
    public void testDeletePhoto() throws Exception {
        String deviceId = "";
        String photoId = "1111";
        Response response = resource.deletePhoto(new UserContext(),deviceId,photoId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        SetupPhotoDTO cache = null;
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);
        response = resource.deletePhoto(new UserContext(),deviceId,photoId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());

        deviceId = "12121";
        List<SetupPhotoDetailDTO> list = new ArrayList<>();
        list.add(new SetupPhotoDetailDTO(){{
            setPhotoId(photoId);
            setPhoto("photo");
        }});
        cache = new SetupPhotoDTO(){{
            setPhotos(list);
        }};
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);
        PowerMockito.when(serviceImp.deletePhoto(photoId)).thenReturn(true);
        response = resource.deletePhoto(new UserContext(),deviceId,photoId);
        assertThat(true,equalTo(response.getEntity()));
    }

    @Test
    public void testSave() throws Exception {
        String deviceId = "2345";
        String photoId = "1111";
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(null);
        Response response = resource.save(new UserContext(),deviceId,photoId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        SetupPhotoDTO cache = new SetupPhotoDTO(){{
            setPhotos(Arrays.asList(new SetupPhotoDetailDTO(){{
                setPhotoId("dsfasdf");
                setPhoto("photo");
            }}));
        }};
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);
        response = resource.save(new UserContext(),deviceId,photoId);
        assertThat(false,equalTo(response.getEntity()));
        cache = new SetupPhotoDTO(){{
            setPhotos(Arrays.asList(new SetupPhotoDetailDTO(){{
                setPhotoId(photoId);
                setPhoto("photo");
            }}));
        }};
        PowerMockito.when(CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId)).thenReturn(cache);
        PowerMockito.when(serviceImp.saveSetupPhotosToDB(Matchers.any())).thenReturn("12");
        response = resource.save(new UserContext(),deviceId,photoId);
        assertThat(true,equalTo(response.getEntity()));
    }

    @Test
    public void testQuerySetupPhotoByExistingDynamicForm() throws Exception {
        String dynamicFormId = "";
        Response response = resource.querySetupPhotoByExistingDynamicForm(new UserContext(),dynamicFormId);
        Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        dynamicFormId = "1212";
        PowerMockito.when(serviceImp.queryPhotosByDynamicFormId(Integer.parseInt(dynamicFormId)))
                .thenReturn(Arrays.asList(new SetupPhotoArchiveDTO()));
        response = resource.querySetupPhotoByExistingDynamicForm(new UserContext(),dynamicFormId);
        List<SetupPhotoArchiveDTO> list = (List<SetupPhotoArchiveDTO>) response.getEntity();
        Assert.assertTrue(list.size() == 1);
    }
}
