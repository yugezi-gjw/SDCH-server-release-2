package com.varian.oiscn.encounter.resource;

import com.varian.oiscn.base.cache.CacheManager;
import com.varian.oiscn.base.cache.CacheManagerUtils;
import com.varian.oiscn.base.cache.CacheNameConstants;
import com.varian.oiscn.base.util.PhotoUtil;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoArchiveDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoDetailDTO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoServiceImp;
import com.varian.oiscn.resource.AbstractResource;
import io.dropwizard.auth.Auth;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@Slf4j
@Path("/setup-photo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SetupPhotoResource extends AbstractResource {

    private static final long MAX_PHOTO_SIZE = 10485760L;

    public SetupPhotoResource(Configuration configuration, Environment environment) {
        super(configuration, environment);
    }

    @POST
    @Path("/init/{deviceId}/{patientSer}")
    public synchronized Response initCache(@Auth UserContext userContext,
                                           @PathParam("deviceId") String deviceId,
                                           @PathParam("patientSer") Long patientSer) {
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        SetupPhotoServiceImp serviceImp = new SetupPhotoServiceImp(userContext);
        SetupPhotoDTO dto = serviceImp.queryByDeviceIdAndPatientSer(deviceId, patientSer);
        // no data in database yet for this patient.
        if (dto == null) {
            dto = new SetupPhotoDTO();
            dto.setPatientSer(patientSer);
            dto.setDeviceId(deviceId);
        }

        CacheManagerUtils.put(CacheNameConstants.SETUP_PHOTO, deviceId, dto);

        return Response.ok(new HashMap<>(0)).build();
    }

    @POST
    @Path("/dispose/{deviceId}/{patientSer}")
    public Response disposeCache(@Auth UserContext userContext,
                                 @PathParam("deviceId") String deviceId,
                                 @PathParam("patientSer") Long patientSer){
        if (patientSer == null) {
            log.warn("Parameter patientSer must not be null");
            return build400Response(patientSer);
        }
        CacheManager cacheManager = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO);
        if(cacheManager == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            cacheManager.invalidate(deviceId);
            return Response.ok(new HashMap<>()).build();
        }
    }


    /**
     * Query photo id list for UI, need authorization.
     * @param userContext user context
     * @param deviceId device id
     * @return response
     */
    @GET
    @Path("/list/{deviceId}")
    public Response listPhotoIds(@Auth UserContext userContext,
                                 @PathParam("deviceId") String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);

        List<String> photoIdList = getPhotoIdListFromCache(cache, deviceId);
        return Response.status(Response.Status.OK).entity(photoIdList).build();
    }

    /**
     * Retrieve Photo Data with Authorization.<br>
     *
     * @param deviceId device id
     * @param photoId  photo Id
     * @return response
     */
    @GET
    @Path("/photo/{deviceId}/{photoId}")
    public Response getPhoto(@Auth UserContext userContext,
                             @PathParam("deviceId") String deviceId,
                             @PathParam("photoId") String photoId) {
        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.error("getPhoto - cache is NULL!");
            return buildBadCacheResponse();
        }
        Map<String, String> resp = new HashMap<>(2);
        String photo = getPhotoFromCache(cache, photoId);
        if (photo != null) {
            resp.put("id", photoId);
            resp.put("photo", photo);
        }
        return Response.ok(resp).build();
    }

    /**
     * Delete photo from cache and database.
     * This interface is for UI, need authorization.
     * @param userContext user context
     * @param deviceId device id
     * @param photoId photo id
     * @return response
     */
    @Path("/photo/{deviceId}/{photoId}")
    @DELETE
    public Response deletePhoto(@Auth UserContext userContext,
                                @PathParam("deviceId") String deviceId,
                                @PathParam("photoId") String photoId) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(photoId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.error("deletePhoto - cache is NULL!");
            return buildBadCacheResponse();
        }

        deletePhotoFromCache(cache, photoId);
        boolean dbResult = deletePhotoFromDB(userContext, photoId);

        return Response.ok(dbResult).build();
    }

    /**
     * Get photo from cache and persist it to DB
     * @param userContext
     * @param deviceId
     * @param photoId
     * @return
     */
    @Path("/save/{deviceId}/{photoId}")
    @PUT
    public Response save(@Auth UserContext userContext,
                         @PathParam("deviceId") String deviceId,
                         @PathParam("photoId") String photoId) {
        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.error("save - cache is NULL!");
            return buildBadCacheResponse();
        }

        SetupPhotoDetailDTO detailDTO = getPhotosFromCache(deviceId, photoId, cache);

        if(detailDTO == null) {
            return Response.status(Response.Status.OK).entity(false).build();
        }

        SetupPhotoServiceImp serviceImp = new SetupPhotoServiceImp(userContext);

        SetupPhotoDTO dto = new SetupPhotoDTO();
        dto.setDeviceId(deviceId);
        dto.setPatientSer(cache.getPatientSer());
        List<SetupPhotoDetailDTO> detailDTOs = new ArrayList<>();
        detailDTOs.add(detailDTO);
        dto.setPhotos(detailDTOs);

        serviceImp.saveSetupPhotosToDB(dto);
        return Response.status(Response.Status.OK).entity(true).build();
    }

    @POST
    @Path("/no-auth/upload/{deviceId}")
    public Response upload(@PathParam("deviceId") String deviceId, SetupPhotoDetailDTO entity) {
        Map<String, String> resp = new HashMap<>(1);

        if (deviceId == null || entity == null || entity.getPhoto() == null || entity.getPhoto().length() == 0) {
            log.warn("upload - Bad Parameter, deviceId:[{}], photo:[{}]", deviceId, entity);
            resp.put("message", "error-050"); // request parameter is null
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        String photoBase64 = entity.getPhoto();
        int photoSize = (photoBase64.length()) / 4 * 3;
        log.debug("upload - photo size: [] bytes", photoSize);

        if (photoSize > MAX_PHOTO_SIZE) {
            log.warn("upload - Photo is too big, photo size: [] bytes", photoSize);
            resp.put("message", "error-052"); // photo is too big
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.warn("upload - cache is NULL!");
            return buildBadCacheResponse();
        }

        String photoId = PhotoUtil.generateUID(deviceId);
        cache.addPhoto(photoId, photoBase64);

        resp.put("id", photoId);
        return Response.ok(resp).build();
    }

    /**
     * Query photo id list for mobile, without Auth.
     *
     * @param deviceId device id
     * @return response
     */
    @Path("/no-auth/list/{deviceId}")
    @GET
    public Response listPhotoIds(@PathParam("deviceId") String deviceId) {
        if (StringUtils.isEmpty(deviceId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        List<String> photoIdList = getPhotoIdListFromCache(cache, deviceId);
        return Response.status(Response.Status.OK).entity(photoIdList).build();
    }

    @GET
    @Path("/no-auth/photo/{deviceId}/{photoId}")
    public Response getPhoto(@PathParam("deviceId") String deviceId, @PathParam("photoId") String photoId) {
        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.warn("getPhoto - cache is NULL!");
            return buildBadCacheResponse();
        }
        HashMap<String, Object> resp = new HashMap<>(2);
        String photo = getPhotoFromCache(cache, photoId);
        if (photo != null) {
            //String photo = PhotoUtil.encode(photoBytes);
            resp.put("id", photoId);
            resp.put("photo", photo);
        }
        return Response.ok(resp).build();
    }

    /**
     * Delete photo in cache.
     * This interface is for mobile, without auth.
     * @param photoId photo id
     * @return response
     */
    @Path("/no-auth/photo/{deviceId}/{photoId}")
    @DELETE
    public Response deletePhoto(@PathParam("deviceId") String deviceId,
                                @PathParam("photoId") String photoId) {
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(photoId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SetupPhotoDTO cache = CacheManagerUtils.get(CacheNameConstants.SETUP_PHOTO, deviceId);
        if (cache == null) {
            log.warn("deletePhoto - cache is NULL!");
            return buildBadCacheResponse();
        }

        boolean result = deletePhotoFromCache(cache, photoId);
        return Response.ok(result).build();
    }

    protected Response buildBadCacheResponse() {
        Map<String, String> resp = new HashMap<>();
        // 表单未打开或已提交，请重新打开表单！
        resp.put("message", "error-040");
        return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
    }

    protected synchronized boolean deletePhotoFromCache(SetupPhotoDTO cache, String photoId) {
        if (cache.getPhotos() != null) {
            SetupPhotoDetailDTO detailDTO;
            Iterator<SetupPhotoDetailDTO> iterator = cache.getPhotos().iterator();
            while (iterator.hasNext()) {
                detailDTO = iterator.next();
                if (StringUtils.equals(photoId, detailDTO.getPhotoId())) {
                    iterator.remove();
                    return true;
                }
            }
        }
        return false;
    }

    protected List<String> getPhotoIdListFromCache(SetupPhotoDTO cache, String deviceId) {
        List<String> photoIdList = new ArrayList<>();
        if(cache != null){
            List<SetupPhotoDetailDTO> detailList = cache.getPhotos();
            if (detailList != null && !detailList.isEmpty()) {
                detailList.forEach(detailDto -> photoIdList.add(detailDto.getPhotoId()));
            }
        }
        return photoIdList;
    }

    protected String getPhotoFromCache(SetupPhotoDTO cache, String photoId) {
        return cache.getPhoto(photoId);
    }

    protected boolean deletePhotoFromDB(UserContext userContext, String photoId) {
        SetupPhotoServiceImp serviceImp = new SetupPhotoServiceImp(userContext);
        return serviceImp.deletePhoto(photoId);
    }

    /**
     * Query photos by dynamic form id
     * @param userContext
     * @param dynamicFormId
     * @return
     */
    @Path("/queryByDynamicForm")
    @GET
    public Response querySetupPhotoByExistingDynamicForm(@Auth UserContext userContext,
                                                         @QueryParam("dynamicFormId") String dynamicFormId) {
        if (StringUtils.isEmpty(dynamicFormId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        SetupPhotoServiceImp serviceImp = new SetupPhotoServiceImp(userContext);
        List<SetupPhotoArchiveDTO> photos = serviceImp.queryPhotosByDynamicFormId(Integer.parseInt(dynamicFormId));
        return Response.status(Response.Status.OK).entity(photos).build();
    }

    /**
     * Query photo id list for Dynamic Form, need authorization.
     *
     * @param userContext user context
     * @param id          dynamicFormInstanceId
     * @return response
     */
    @GET
    @Path("/archive/list/{dynamicFormRecordId}")
    public Response listArchivePhotoIds(@Auth UserContext userContext,
                                        @PathParam("dynamicFormRecordId") String id) {
        if (StringUtils.isEmpty(id) || id.length() > 11 || !StringUtils.isNumeric(id)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Integer dynamicFormRecordId = Integer.parseInt(id);
        SetupPhotoServiceImp service = new SetupPhotoServiceImp(userContext);
        List<String> idList = service.queryArchivePhotoIdListByDynamicFormRecordId(dynamicFormRecordId);
        return Response.status(Response.Status.OK).entity(idList).build();
    }

    /**
     * Query photo id list for Dynamic Form, need authorization.
     *
     * @param userContext user context
     * @param photoId     Photo id
     * @return response
     */
    @GET
    @Path("/archive/photo/{photoId}")
    public Response getArchivePhoto(@Auth UserContext userContext,
                                    @PathParam("photoId") String photoId) {
        if (StringUtils.isEmpty(photoId)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        SetupPhotoServiceImp service = new SetupPhotoServiceImp(userContext);
        SetupPhotoDetailDTO photo = service.getArchivePhoto(photoId);
        return Response.status(Response.Status.OK).entity(photo).build();
    }

    /**
     * Get photo from cache by photoId
     * @param deviceId
     * @param photoId
     */
    private SetupPhotoDetailDTO getPhotosFromCache(String deviceId, String photoId, SetupPhotoDTO dto) {

        if(StringUtils.isNotBlank(deviceId) && StringUtils.isNotBlank(photoId)) {

            if(dto != null && dto.getPhotos() != null) {
                List<SetupPhotoDetailDTO> details = dto.getPhotos();
                SetupPhotoDetailDTO detailDTO;

                for(Iterator<SetupPhotoDetailDTO> it = details.iterator(); it.hasNext();) {
                    detailDTO = it.next();

                    if(StringUtils.equals(photoId, detailDTO.getPhotoId())) {
                        return detailDTO;
                    }
                }
            }
        }
        return null;
    }
}
