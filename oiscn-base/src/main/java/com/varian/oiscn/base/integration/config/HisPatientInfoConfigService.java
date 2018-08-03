package com.varian.oiscn.base.integration.config;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class HisPatientInfoConfigService {

    private static HisPatientInfoConfiguration configuration;

    private HisPatientInfoConfigService() {
    }

    /**
     * Init configuration
     *
     * @param configFilePath
     */
    public static void init(String configFilePath) {
        if (isNotEmpty(configFilePath)) {
            loadConfiguration(configFilePath);
        }
    }

    /**
     * Load configuratin from file
     *
     * @param filePath
     * @return
     */
    private static HisPatientInfoConfiguration loadConfiguration(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists() && file.isFile()) {
            try {
                configuration = new Yaml().loadAs(new FileInputStream(file), HisPatientInfoConfiguration.class);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        }
        return configuration;
    }

    public static HisPatientInfoConfiguration getConfiguration() {
        return configuration;
    }
}
