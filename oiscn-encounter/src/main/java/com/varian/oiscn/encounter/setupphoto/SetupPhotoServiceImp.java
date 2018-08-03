package com.varian.oiscn.encounter.setupphoto;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@Slf4j
public class SetupPhotoServiceImp {
    private SetupPhotoDAO setupPhotoDAO;

    public SetupPhotoServiceImp(UserContext userContext) {
        setupPhotoDAO = new SetupPhotoDAO(userContext);
    }

    /**
     * Query setup photo with device and PatientSer
     *
     * @param deviceId device id
     * @param patientSer    patientSer
     * @return setup photo object
     */
    public SetupPhotoDTO queryByDeviceIdAndPatientSer(String deviceId, Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            SetupPhotoDTO dto = setupPhotoDAO.queryByDeviceIdAndPatientSer(con, deviceId, patientSer);
            if (dto != null) {
                dto.setPhotos(setupPhotoDAO.queryDetailsBySetupPhotoId(con, dto.getId()));
            }
            return dto;
        } catch (SQLException e) {
            log.error("SetupPhotoServiceImp.queryByDeviceIdAndPatientSer SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    /**
     * Persist setup photos to DB
     *
     * @param dto
     * @return
     */
    public String saveSetupPhotosToDB(SetupPhotoDTO dto) {
        Connection con = null;
        String setupPhotoId = null;

        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);

            if (dto.getPatientSer()!= null && StringUtils.isNotBlank(dto.getDeviceId())) {
                SetupPhotoDTO photoDTO = setupPhotoDAO.querySetupPhotoByDeviceIdAndPatientSer(con, dto);

                if (photoDTO != null) {
                    setupPhotoId = photoDTO.getId();
                    setupPhotoDAO.saveSetupPhotoDetail(con, dto, setupPhotoId);
                } else {
                    setupPhotoId = setupPhotoDAO.saveSetupPhoto(con, dto);
                    setupPhotoDAO.saveSetupPhotoDetail(con, dto, setupPhotoId);
                }
                con.commit();
            }

        } catch (SQLException e) {
            log.error("SetupPhotoServiceImp.saveSetupPhotosToDB SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(con);
            return null;
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
        return setupPhotoId;
    }

    public boolean clearSetupPhotos(Long patientSer) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(con, false);
            List<String> idList = setupPhotoDAO.querySetupPhotoIdsByPatientSer(con, patientSer);
            setupPhotoDAO.batchDeletePhotoDetails(con, idList);
            setupPhotoDAO.deleteSetupPhotosByPatientSer(con, patientSer);
            con.commit();
            return true;
        } catch (SQLException e) {
            DatabaseUtil.safeRollback(con);
            log.error("SetupPhotoServiceImp.clearSetupPhotos SQLException SQLState=[{}]", e.getSQLState());
            return false;
        } finally {
            DatabaseUtil.safeSetAutoCommit(con, true);
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public boolean deletePhoto(String photoId) {
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();
            setupPhotoDAO.deletePhoto(con, photoId);
            return true;
        } catch (SQLException e) {
            log.error("SetupPhotoServiceImp.deletePhoto SQLException SQLState=[{}]", e.getSQLState());
            return false;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
    }

    public List<SetupPhotoArchiveDTO> queryPhotosByDynamicFormId(int dynamicFormId) {
        Connection con = null;
        List<SetupPhotoArchiveDTO> photos;
        try {

            con = ConnectionPool.getConnection();
            photos = setupPhotoDAO.queryPhotosByDynamicFormId(con, dynamicFormId);

        } catch (SQLException e) {
            log.error("SetupPhotoServiceImp.queryPhotosByDynamicFormId SQLException SQLState=[{}]", e.getSQLState());
            return null;
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return photos;
    }

    public List<String> queryArchivePhotoIdListByDynamicFormRecordId(int id) {
        Connection con = null;
        List<String> IdList = new ArrayList<>();
        try {
            con = ConnectionPool.getConnection();
            IdList = setupPhotoDAO.queryArchivePhotoIdListByDynamicFormRecordId(con, id);
        } catch (SQLException e) {
            log.error("queryPhotoIdListByDynamicFormInstanceId SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return IdList;
    }

    public SetupPhotoDetailDTO getArchivePhoto(String photoId) {
        Connection con = null;
        SetupPhotoDetailDTO photo = new SetupPhotoDetailDTO();
        try {
            con = ConnectionPool.getConnection();
            photo = setupPhotoDAO.getArchivePhoto(con, photoId);
        } catch (SQLException e) {
            log.error("getArchivePhoto SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return photo;
    }
}
