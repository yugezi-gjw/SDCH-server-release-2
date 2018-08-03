package com.varian.oiscn.encounter.setupphoto;

import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by gbt1220 on 1/5/2018.
 */
@Slf4j
public class SetupPhotoDAO {

    private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    private UserContext userContext;

    public SetupPhotoDAO(UserContext userContext) {
        this.userContext = userContext;
    }

    public SetupPhotoDTO queryByDeviceIdAndPatientSer(Connection con, String deviceId, Long patientSer) throws SQLException {
        SetupPhotoDTO dto = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            String selectSetupPhoto = "SELECT id, createdUser, createdDT FROM SetupPhoto WHERE deviceId=? AND patientSer=?";
            ps = con.prepareStatement(selectSetupPhoto);
            ps.setString(1, deviceId);
            ps.setLong(2, patientSer);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                dto = new SetupPhotoDTO();
                dto.setId(resultSet.getString("id"));
                dto.setDeviceId(deviceId);
                dto.setPatientSer(patientSer);
            }
            return dto;
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
    }

    public List<SetupPhotoDetailDTO> queryDetailsBySetupPhotoId(Connection con, String setupPhotoId) throws SQLException {
        List<SetupPhotoDetailDTO> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT DISTINCT photoId, photo FROM SetupPhotoDetail WHERE setupPhotoId=? ORDER BY photoId ASC ";
            ps = con.prepareStatement(sql);
            ps.setString(1, setupPhotoId);
            resultSet = ps.executeQuery();
            SetupPhotoDetailDTO dto;
            while (resultSet.next()) {
                dto = new SetupPhotoDetailDTO();
                dto.setPhotoId(resultSet.getString("photoId"));
                String photo;
                try {
                    photo = new String(resultSet.getBytes("photo"), CHARSET_ISO_8859_1);
                } catch (UnsupportedEncodingException e) {
                    log.warn("queryDetailsBySetupPhotoId - BAD Photo Encode {}", e.getMessage());
                    photo = "BAD PHOTO ENCODE in Database";
                }
                dto.setPhoto(photo);
                result.add(dto);
            }
            return result;
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
    }

    public SetupPhotoDTO querySetupPhotoByDeviceIdAndPatientSer(Connection con, SetupPhotoDTO dto) throws SQLException {
        SetupPhotoDTO photoDTO = null;
        if (dto != null && StringUtils.isNotBlank(dto.getDeviceId())
                && dto.getPatientSer() != null) {

            PreparedStatement ps = null;
            ResultSet resultSet = null;
            try {
                String sql = "SELECT id FROM SetupPhoto WHERE deviceId = ? AND patientSer = ? ORDER BY id DESC ";
                ps = con.prepareStatement(sql);
                ps.setString(1, dto.getDeviceId());
                ps.setLong(2, dto.getPatientSer());
                resultSet = ps.executeQuery();
                if (resultSet.next()) {
                    photoDTO = new SetupPhotoDTO();
                    photoDTO.setId(resultSet.getString("id"));
                    photoDTO.setPatientSer(dto.getPatientSer());
                    photoDTO.setDeviceId(dto.getDeviceId());
                }
            } finally {
                DatabaseUtil.safeCloseAll(null, ps, resultSet);
            }
        }
        return photoDTO;
    }

    public String saveSetupPhoto(Connection con, SetupPhotoDTO dto) throws SQLException {
        String setupPhotoId = StringUtils.EMPTY;
        if (dto == null) {
            return setupPhotoId;
        }

        Timestamp createDateTime = new Timestamp(new java.util.Date().getTime());
        String createUser = userContext.getLogin().getName();

        PreparedStatement ps = null;
        ResultSet rs = null;

        String insert_setup_photo_sql = "INSERT INTO SetupPhoto ( deviceId, patientSer, createdUser, createdDT) VALUES (?,?,?,?)";
        int idx = 1;

        try {
            ps = con.prepareStatement(insert_setup_photo_sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(idx++, dto.getDeviceId());
            ps.setLong(idx++,dto.getPatientSer());
            ps.setString(idx++, createUser);
            ps.setTimestamp(idx++, createDateTime);

            int result = ps.executeUpdate();
            if (result > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    setupPhotoId = rs.getString(1);
                }
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }

        return setupPhotoId;
    }

    public boolean saveSetupPhotoDetail(Connection con, SetupPhotoDTO dto, String setupPhotoId) throws SQLException {
        if (dto != null && StringUtils.isNotBlank(setupPhotoId) &&
                dto.getPhotos() != null && dto.getPhotos().size() > 0) {

            PreparedStatement ps = null;
            ResultSet rs = null;

            String insertSetupPhotoDetailSql = "INSERT INTO SetupPhotoDetail (setupPhotoId, photoId, photo) VALUES (?,?,?)";
            int idx = 1;

            try {

                for (Iterator<SetupPhotoDetailDTO> it = dto.getPhotos().iterator(); it.hasNext(); ) {
                    SetupPhotoDetailDTO photo = it.next();

                    ps = con.prepareStatement(insertSetupPhotoDetailSql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setString(idx++, setupPhotoId);
                    ps.setString(idx++, photo.getPhotoId());
                    byte[] photoBytes = null;
                    try {
                        photoBytes = photo.getPhoto().getBytes(CHARSET_ISO_8859_1);
                    } catch (UnsupportedEncodingException e) {
                        log.warn("saveSetupPhotoDetail - BAD Photo Encode {}", e.getMessage());
                    }
                    ps.setBytes(idx++, photoBytes);
                    ps.addBatch();
                    idx = 1;
                }
                ps.executeBatch();

            } finally {
                DatabaseUtil.safeCloseAll(null, ps, rs);
            }
            return true;
        }
        return false;
    }

    public void deletePhoto(Connection con, String photoId) throws SQLException {
        PreparedStatement ps = null;
        try {
            String deleteSql = "DELETE FROM SetupPhotoDetail WHERE photoId=?";
            ps = con.prepareStatement(deleteSql);
            ps.setString(1, photoId);
            ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
    }

    public void deleteSetupPhotosByPatientSer(Connection connection, Long patientSer) throws SQLException {
        PreparedStatement ps = null;
        try {
            String deleteSql = "DELETE FROM SetupPhoto WHERE patientSer=?";
            ps = connection.prepareStatement(deleteSql);
            ps.setLong(1, patientSer);
            ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
    }

    public List<String> querySetupPhotoIdsByPatientSer(Connection connection, Long patientSer) throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String querySql = "SELECT id FROM SetupPhoto WHERE patientSer=?";
            ps = connection.prepareStatement(querySql);
            ps.setLong(1, patientSer);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(String.valueOf(rs.getInt(1)));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    public void batchDeletePhotoDetails(Connection connection, List<String> setupPhotoIdList) throws SQLException {
        PreparedStatement ps = null;
        try {
            String deletePhotoDetailsSql = "DELETE FROM SetupPhotoDetail WHERE setupPhotoId=?";
            ps = connection.prepareStatement(deletePhotoDetailsSql);
            for (String id : setupPhotoIdList) {
                ps.setInt(1, Integer.parseInt(id));
                ps.addBatch();
            }
            ps.executeBatch();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
    }

    public List<SetupPhotoArchiveDTO> queryPhotosByDynamicFormId(Connection con, int dynamicFormId) throws SQLException {
        List<SetupPhotoArchiveDTO> archivePhotos = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        String photo = null;
        if (dynamicFormId < 0) {
            return archivePhotos;
        }

        try {
            String sql = "select * from SetupPhotoArchive where dynamicFormRecordId = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, dynamicFormId);
            resultSet = ps.executeQuery();
            SetupPhotoArchiveDTO dto;
            while (resultSet.next()) {
                dto = new SetupPhotoArchiveDTO();
                dto.setDynamicFormRecordId(resultSet.getInt("dynamicFormRecordId"));
                dto.setPhotoId(resultSet.getString("photoId"));
                try {
                    photo = new String(resultSet.getBytes("photo"), CHARSET_ISO_8859_1);
                } catch (UnsupportedEncodingException e) {
                    log.warn("queryDetailsBySetupPhotoId - BAD Photo Encode {}", e.getMessage());
                    photo = "BAD PHOTO ENCODE in Database";
                }
                dto.setPhoto(photo);
                archivePhotos.add(dto);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, resultSet);
        }
        return archivePhotos;
    }

    public void archiveSetupPhotoToDynamicFormRecord(Connection con, String dynamicFormRecordId, Long patientSer) throws SQLException {
        PreparedStatement psSelect = null;
        PreparedStatement psInsert = null;
        PreparedStatement psDeleteSetupPhotoDetail = null;
        PreparedStatement psDeleteSetupPhoto = null;
        ResultSet rs = null;
        try {
            Timestamp curTime = new Timestamp(new java.util.Date().getTime());
            String querySql = "select spd.photoId as photoId, spd.photo as photo, sp.id " +
                    "from SetupPhoto sp, SetupPhotoDetail spd where spd.setupPhotoId = sp.id and patientSer = ?";
            psSelect = con.prepareStatement(querySql);
            psSelect.setLong(1, patientSer);
            rs = psSelect.executeQuery();
            String insertSql = "insert into SetupPhotoArchive(dynamicFormRecordId, photoId, photo, createdUser, createdDT) " +
                    "values (?,?,?,?,?)";
            psInsert = con.prepareStatement(insertSql);
            int setupPhotoId = Integer.MIN_VALUE;
            while (rs.next()) {
                psInsert.setInt(1, Integer.parseInt(dynamicFormRecordId));
                psInsert.setString(2, rs.getString(1));
                psInsert.setBytes(3, rs.getBytes(2));
                psInsert.setString(4, userContext.getLogin().getUsername());
                psInsert.setTimestamp(5, curTime);
                psInsert.addBatch();
                psInsert.clearParameters();
                setupPhotoId = rs.getInt(3);
            }

            if (setupPhotoId != Integer.MIN_VALUE) {
                psInsert.executeBatch();

                String deleteFromSetupPhotoDetail = "delete from SetupPhotoDetail where setupPhotoId = ?";
                psDeleteSetupPhotoDetail = con.prepareStatement(deleteFromSetupPhotoDetail);
                psDeleteSetupPhotoDetail.setInt(1, setupPhotoId);
                psDeleteSetupPhotoDetail.executeUpdate();

                String deleteFromSetupPhoto = "delete from SetupPhoto where id = ?";
                psDeleteSetupPhoto = con.prepareStatement(deleteFromSetupPhoto);
                psDeleteSetupPhoto.setInt(1, setupPhotoId);
                psDeleteSetupPhoto.executeUpdate();
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, psSelect, rs);
            DatabaseUtil.safeCloseStatement(psInsert);
            DatabaseUtil.safeCloseStatement(psDeleteSetupPhotoDetail);
            DatabaseUtil.safeCloseStatement(psDeleteSetupPhoto);
        }
    }

    public List<String> queryArchivePhotoIdListByDynamicFormRecordId(Connection con, int id) throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String querySql = "SELECT DISTINCT photoId FROM SetupPhotoArchive WHERE dynamicFormRecordId = ? ";
            ps = con.prepareStatement(querySql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getString(1));
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return result;
    }

    public SetupPhotoDetailDTO getArchivePhoto(Connection con, String photoId) throws SQLException {
        SetupPhotoDetailDTO photoEntity = new SetupPhotoDetailDTO();
        photoEntity.setPhotoId(photoId);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String querySql = "SELECT TOP 1 photo FROM SetupPhotoArchive WHERE photoId = ? ";
            ps = con.prepareStatement(querySql);
            ps.setString(1, photoId);
            rs = ps.executeQuery();
            String photo = null;
            if (rs.next()) {
                try {
                    photo = new String(rs.getBytes("photo"), CHARSET_ISO_8859_1);
                } catch (UnsupportedEncodingException e) {
                    log.warn("queryDetailsBySetupPhotoId - BAD Photo Encode {}", e.getMessage());
                    photo = "BAD PHOTO ENCODE in Database";
                }
            }
            photoEntity.setPhoto(photo);
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return photoEntity;
    }

    public boolean updateArchiveSetupPhotoWithNewRecordId(Connection con, String oldDynamicFormRecordId, String newDynamicFormRecordId) throws SQLException{
        PreparedStatement ps = null;
        int affectedRows = 0;
        try{
            String updateSql = "update SetupPhotoArchive set dynamicFormRecordId = ? where dynamicFormRecordId = ?";
            ps = con.prepareStatement(updateSql);
            ps.setInt(1, Integer.parseInt(newDynamicFormRecordId));
            ps.setInt(2, Integer.parseInt(oldDynamicFormRecordId));
            affectedRows = ps.executeUpdate();
        } finally {
            DatabaseUtil.safeCloseStatement(ps);
        }
        return affectedRows > 0;

    }
}
