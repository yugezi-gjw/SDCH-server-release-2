package com.varian.oiscn.base.dynamicform;

import com.varian.oiscn.base.systemconfig.SystemConfigPool;
import com.varian.oiscn.util.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gbt1220 on 6/28/2017.
 */
public class DynamicFormTemplateDAO {

    public Map<String, String> queryTemplateJsonByTemplateIds(Connection connection, List<String> templateIdList) throws SQLException, IOException {
        Map<String, String> jsonMap = new HashMap<>();
        String json;
        List<DynamicFormTemplate> list = this.queryTemplateFormListByTemplateIds(connection, templateIdList);
        if (!list.isEmpty()) {
            byte[] bytes;
            for (DynamicFormTemplate dynamicFormTemplate : list) {
                bytes = Files.readAllBytes(Paths.get(dynamicFormTemplate.getTemplatePath()));
                json = new String(bytes, "utf-8");
                jsonMap.put(dynamicFormTemplate.getTemplateId(), json);
            }
        }
        return jsonMap;
    }

    public List<DynamicFormTemplate> queryTemplateFormListByTemplateIds(Connection conn, List<String> templateIds)
            throws SQLException {
        List<DynamicFormTemplate> list = new ArrayList<>();
        if (templateIds == null || templateIds.isEmpty()) {
            return list;
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        DynamicFormTemplate dynamicFormTemplate;
        try {
            String category = SystemConfigPool.getDynamicFormTemplateCategory();
            StringBuilder querySql = new StringBuilder("select id, templateId, templateName, templatePath FROM DynamicFormTemplate WHERE ");
            // for multiple hospital template exists.
            if (StringUtils.isBlank(category)) {
                querySql.append(" category IS NULL AND ");
            } else {
                querySql.append(" category = '").append(category).append("' AND ");
            }
            
            querySql.append("( templateId = ? ");
            for (int i = 1; i < templateIds.size(); i++) {
                querySql.append(" OR templateId = ?");
            }
            querySql.append(")");

            ps = conn.prepareStatement(querySql.toString());
            for (int i = 0; i < templateIds.size(); i++) {
                ps.setString(i + 1, templateIds.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                dynamicFormTemplate = new DynamicFormTemplate();
                dynamicFormTemplate.setId(rs.getString("id"));
                dynamicFormTemplate.setTemplateId(rs.getString("templateid"));
                dynamicFormTemplate.setTemplateName(rs.getString("templatename"));
                dynamicFormTemplate.setTemplatePath(rs.getString("templatepath"));
                list.add(dynamicFormTemplate);
            }
        } finally {
            DatabaseUtil.safeCloseAll(null, ps, rs);
        }
        return list;
    }
}
