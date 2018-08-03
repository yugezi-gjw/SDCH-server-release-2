package com.varian.oiscn.base.dynamicform;

import com.varian.oiscn.connection.ConnectionPool;
import com.varian.oiscn.util.DatabaseUtil;
import com.varian.oiscn.core.common.KeyValuePair;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/28/2017.
 */
@Slf4j
public class DynamicFormTemplateServiceImp {

    private DynamicFormTemplateDAO dynamicFormTemplateDAO;

    public DynamicFormTemplateServiceImp() {
        dynamicFormTemplateDAO = new DynamicFormTemplateDAO();
    }

    public Map<String, String> queryTemplateJsonByTemplateId(List<String> templateIdList) {
        Connection con = null;
        Map<String, String> jsonMap = new HashMap<>();
        try {
            con = ConnectionPool.getConnection();
            jsonMap = dynamicFormTemplateDAO.queryTemplateJsonByTemplateIds(con, templateIdList);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
        } catch (IOException e) {
            log.error("IOException {}", e.getMessage());
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return jsonMap;
    }

    public List<DynamicFormTemplate> queryTemplateListByTemplateIds(List<String> templateIds) {
        List<DynamicFormTemplate> list;
        Connection con = null;
        try {
        	con = ConnectionPool.getConnection();
            list = dynamicFormTemplateDAO.queryTemplateFormListByTemplateIds(con, templateIds);
        } catch (SQLException e) {
            log.error("SQLException SQLState=[{}]", e.getSQLState());
            list = new ArrayList<>();
        } finally {
            DatabaseUtil.safeCloseConnection(con);
        }
        return list;
    }

    public List<KeyValuePair> queryTemplateNamesByTemplateIds(List<String> templateIds) {
        List<KeyValuePair> result = new ArrayList<>();
        List<DynamicFormTemplate> list = this.queryTemplateListByTemplateIds(templateIds);
        KeyValuePair keyValuePair;
        for (DynamicFormTemplate template : list) {
            keyValuePair = new KeyValuePair(template.getTemplateId(), template.getTemplateName());
            result.add(keyValuePair);
        }
        return result;
    }
}

