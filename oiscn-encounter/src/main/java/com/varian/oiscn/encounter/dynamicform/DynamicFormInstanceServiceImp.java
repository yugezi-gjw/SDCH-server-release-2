package com.varian.oiscn.encounter.dynamicform;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.core.common.KeyValuePair;
import com.varian.oiscn.core.encounter.Encounter;
import com.varian.oiscn.core.user.UserContext;
import com.varian.oiscn.encounter.dao.DynamicFormRecordDAO;
import com.varian.oiscn.encounter.dao.EncounterDAO;
import com.varian.oiscn.encounter.setupphoto.SetupPhotoDAO;
import com.varian.oiscn.encounter.view.DynamicFormItemsAndTemplateInfo;
import com.varian.oiscn.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by gbt1220 on 6/30/2017.
 */
@Slf4j
public class DynamicFormInstanceServiceImp {

    private DynamicFormInstanceDAO dynamicFormInstanceDAO;

    private DynamicFormRecordDAO dynamicFormRecordDAO;

    private EncounterDAO encounterDAO;

    private SetupPhotoDAO setupPhotoDAO;

    public DynamicFormInstanceServiceImp(UserContext userContext) {
        dynamicFormInstanceDAO = new DynamicFormInstanceDAO(userContext);
        encounterDAO = new EncounterDAO(userContext);
        dynamicFormRecordDAO = new DynamicFormRecordDAO(userContext);
        setupPhotoDAO = new SetupPhotoDAO(userContext);
    }

    public DynamicFormInstance queryByPatientSer(Long patientSer) {
        Connection connection = null;
        try {
        	connection = ConnectionPool.getConnection();
            return dynamicFormInstanceDAO.queryByPatientSer(connection, patientSer);
        } catch (SQLException e) {
            log.error("queryByPatientSer SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return null;
    }

    public String saveOrUpdate(Long patientSer, DynamicFormItemsAndTemplateInfo dynamicFormItemsAndTemplateInfo, String templateId, String carePathInstanceId, String existingDynamicFormRecordId) {
        Connection connection = null;
        String saveOrUpdateId = "";
        try {
        	connection = ConnectionPool.getConnection();
            DatabaseUtil.safeSetAutoCommit(connection, false);
            DynamicFormInstance dynamicFormInstance = dynamicFormInstanceDAO.queryByPatientSer(connection, patientSer);
            Encounter encounter = encounterDAO.queryByPatientSer(connection, patientSer);
            if (dynamicFormInstance == null) {
                dynamicFormInstance = new DynamicFormInstance();
                dynamicFormInstance.setPatientSer(patientSer.toString());
                if (encounter == null) {
                    // encounter should not be empty.
                    log.error("There is No Patient Encounter Information !!!");
                    return saveOrUpdateId;
                }
                dynamicFormInstance.setEncounterId(encounter.getId());
                dynamicFormInstance.setDynamicFormItems(dynamicFormItemsAndTemplateInfo.getRecordInfo());
            } else {
                List<KeyValuePair> items = dynamicFormInstance.getDynamicFormItems();
                List<KeyValuePair> cloneRequestedItems = new ArrayList<>();
                cloneRequestedItems.addAll(dynamicFormItemsAndTemplateInfo.getRecordInfo());
                items.forEach(eachItem ->
                        dynamicFormItemsAndTemplateInfo.getRecordInfo().forEach(requestedDynamicFormItem -> {
                            if (StringUtils.equals(requestedDynamicFormItem.getKey(), eachItem.getKey())) {
                                eachItem.setValue(requestedDynamicFormItem.getValue());
                                cloneRequestedItems.remove(requestedDynamicFormItem);
                            }
                        }));
                if (!cloneRequestedItems.isEmpty()) {
                    cloneRequestedItems.forEach(eachItem -> items.add(new KeyValuePair(eachItem.getKey(), eachItem.getValue())));
                }
            }
            if (isEmpty(dynamicFormInstance.getId())) {
                saveOrUpdateId = dynamicFormInstanceDAO.create(connection, dynamicFormInstance);
            } else {
                dynamicFormInstanceDAO.update(connection, dynamicFormInstance, dynamicFormInstance.getId());
                saveOrUpdateId = dynamicFormInstance.getId();
            }

            DynamicFormRecord dynamicFormRecord = new DynamicFormRecord();
            dynamicFormRecord.setPatientSer(new Long(patientSer));
            dynamicFormRecord.setEncounterId(new Long(encounter.getId()));
            dynamicFormRecord.setCarePathInstanceId(carePathInstanceId);
            dynamicFormRecord.setTemplateId(templateId);
            dynamicFormRecord.setDynamicFormRecordInfo(dynamicFormItemsAndTemplateInfo.getRecordInfo());
            dynamicFormRecord.setTemplateInfo(dynamicFormItemsAndTemplateInfo.getTemplateInfo());
            String dynamicFormRecordId = dynamicFormRecordDAO.create(connection, dynamicFormRecord);

            boolean hasPhotos = false;
            for(KeyValuePair keyValuePair : dynamicFormItemsAndTemplateInfo.getRecordInfo()){
                if(keyValuePair.getKey().equals("photos")){
                    hasPhotos = true;
                    break;
                }
            }
            if(hasPhotos){
                if(StringUtils.isNotEmpty(existingDynamicFormRecordId)){
                    setupPhotoDAO.updateArchiveSetupPhotoWithNewRecordId(connection, existingDynamicFormRecordId, dynamicFormRecordId);
                } else {
                    setupPhotoDAO.archiveSetupPhotoToDynamicFormRecord(connection, dynamicFormRecordId, patientSer);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            DatabaseUtil.safeRollback(connection);
        } finally {
            DatabaseUtil.safeSetAutoCommit(connection, true);
            DatabaseUtil.safeCloseConnection(connection);
        }
        return saveOrUpdateId;
    }

    public String queryFieldValueByPatientSerListAndFieldName(String patientSer, String FieldName) {
        Connection connection = null;
        String value = null;
        try {
        	connection = ConnectionPool.getConnection();
            value = this.dynamicFormInstanceDAO.selectFieldValueByPatientSerAndName(connection, patientSer, FieldName);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return value;
    }


    public Map<String,String> queryFieldValueByPatientSerListAndFieldName(List<String> patientSerList, String FieldName) {
        Connection connection = null;
        Map<String,String> map = null;
        try {
            connection = ConnectionPool.getConnection();
            map = this.dynamicFormInstanceDAO.selectFieldValueByPatientSerListAndName(connection, patientSerList, FieldName);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return map;
    }

    public Map<String, Map<String, String>> queryFieldsValueByPatientSerListAndFieldNames(List<String> patientSerList,
                                                                                          List<String> fieldNames) {
        Connection connection = null;
        Map<String, Map<String, String>> map = null;
        try {
            connection = ConnectionPool.getConnection();
            map = this.dynamicFormInstanceDAO.selectFieldNameValuePairsByPatientSerList(connection, patientSerList, fieldNames);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } finally {
            DatabaseUtil.safeCloseConnection(connection);
        }
        return map;
    }

}
