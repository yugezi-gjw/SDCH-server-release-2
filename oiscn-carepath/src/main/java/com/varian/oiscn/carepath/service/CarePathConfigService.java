package com.varian.oiscn.carepath.service;

import com.varian.oiscn.appointment.calling.CallingServiceException;
import com.varian.oiscn.base.codesystem.CodeSystemPool;
import com.varian.oiscn.config.CarePathConfig;
import com.varian.oiscn.config.Configuration;
import com.varian.oiscn.core.carepath.CarePathConfigItem;
import com.varian.oiscn.core.carepath.CarePathTemplateVO;
import com.varian.oiscn.core.codesystem.CodeValue;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Care Path Configuration Service.<br>
 */
@Slf4j
public class CarePathConfigService {

    protected static List<CarePathConfigItem> carePathConfigItemList;
    protected static List<CarePathTemplateVO> carePathTemplateVO;
    protected static boolean ready = false;

    /**
     * Initialize the Care Path Configuration.<br>
     *
     * @param rootConfig System Configuration
     */
    public static void init(Configuration rootConfig) {
        String configFile = rootConfig.getCarePathConfigFile();
        if (configFile == null) {
            log.error("No CarePath Configuration in LocaL File !!!");
            // throw new RuntimeException("No CarePath Configuration in LocaL File !!!");
            return;
        } else {
            CarePathConfig config = loadConfigFile(configFile);
            if (config == null) {
                // throw new RuntimeException("CarePath Configuration Error!!!");
                log.error("CarePath Configuration Error!!!");
                return;
            }
            rootConfig.setCarePathConfig(config);
            rootConfig.setDefaultCarePathTemplateName(config.getDefaultCarePathTemplateName());
            carePathConfigItemList = config.getCarePath();
        }
    }

    /**
     * Load Calling Configuration file into CallingSystemConfiguration class.<br>
     *
     * @param callingConfigFile Calling Config File Name
     * @return CallingSystemConfiguration
     * @throws CallingServiceException
     */
    protected static CarePathConfig loadConfigFile(String callingConfigFile) {
        CarePathConfig config = null;
        if (callingConfigFile != null) {
            File file = new File(callingConfigFile);
            if (file != null && file.exists() && file.isFile()) {
                try {
                    config = new Yaml().loadAs(new FileInputStream(file), CarePathConfig.class);
                } catch (FileNotFoundException e) {
                    log.error("No CarePath Configuration File !!! {}", e.getMessage());
                    // throw new RuntimeException("No CarePath Configuration File !!!");
                }
            }
        }
        return config;
    }

    public static boolean isReady() {
        return ready;
    }

    public static List<CarePathTemplateVO> getCarePathTemplateList() {
        if (!ready) {
            List<CodeValue> carePathList = CodeSystemPool.getCarePathList();
            carePathTemplateVO = new ArrayList<>(carePathList.size());
            for (CarePathConfigItem item : carePathConfigItemList) {
                boolean found = false;
                for (CodeValue codeValue : carePathList) {
                    if (item.getTemplateId().equals(codeValue.getDesc())) {
                        CarePathTemplateVO vo = new CarePathTemplateVO();
                        vo.setId(item.getTemplateId());
                        vo.setName(item.getTemplateName());
                        vo.setDescription(item.getDescription());
                        vo.setCategory(item.getCategory());
                        carePathTemplateVO.add(vo);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    log.error("Not found the Template with Id [{}]", item.getTemplateId());
                }
            }
            ready = true;
        }
        return carePathTemplateVO;
    }

    public static String getTemplateNameById(String carePathTemplateId){
        for(CarePathConfigItem carePathConfigItem : carePathConfigItemList){
            if(carePathConfigItem.getTemplateId().equals(carePathTemplateId)){
                return carePathConfigItem.getTemplateName();
            }
        }
        return null;
    }
}
