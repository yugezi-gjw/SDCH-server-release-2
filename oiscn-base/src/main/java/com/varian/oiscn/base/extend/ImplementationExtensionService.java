package com.varian.oiscn.base.extend;

import com.varian.oiscn.config.ImplementationExtendConfiguration;
import com.varian.oiscn.core.extend.ImplementationExtension;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Implementation extension service
 * Created by gbt1220 on 11/21/2017.
 */
@Slf4j
public class ImplementationExtensionService {

    protected static ImplementationExtendConfiguration configuration;

    private ImplementationExtensionService() {
    }

    /**
     * Init configuration
     * @param filePath file path
     */
    public static void init(String filePath) {
        if (isNotEmpty(filePath)) {
            loadConfiguration(filePath);
        }
    }

    /**
     * Load configuration from file
     * @param filePath file path
     * @return the configuration
     */
    private static ImplementationExtendConfiguration loadConfiguration(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists() && file.isFile()) {
            try {
                configuration = new Yaml().loadAs(new FileInputStream(file), ImplementationExtendConfiguration.class);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        }
        return configuration;
    }

    /**
     * Get implementation class of contract class name
     * @param contractClass contract class name
     * @return implementation class
     */
    public static String getImplementationClassOf(String contractClass) {
        String implementationClassName = StringUtils.EMPTY;
        if (configuration != null && configuration.getExtensions() != null) {
            for (ImplementationExtension extension : configuration.getExtensions()) {
                if (StringUtils.equals(extension.getContract(), contractClass)) {
                    implementationClassName = extension.getImplementation();
                }
            }
        }
        return implementationClassName;
    }
}
