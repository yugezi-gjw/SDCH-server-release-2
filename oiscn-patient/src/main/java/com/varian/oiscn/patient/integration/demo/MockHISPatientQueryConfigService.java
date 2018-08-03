package com.varian.oiscn.patient.integration.demo;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Created by gbt1220 on 1/2/2018.
 */
@Slf4j
public class MockHISPatientQueryConfigService {
    private MockHISPatientQueryConfiguration configuration;

    public MockHISPatientQueryConfigService(String filePath) {
        if (isNotEmpty(filePath)) {
            loadDemoData(filePath);
        }
    }

    private void loadDemoData(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists() && file.isFile()) {
            try {
                configuration = new Yaml().loadAs(new FileInputStream(file), MockHISPatientQueryConfiguration.class);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error("Parse error. Please check the HIS patient query demo data file.");
                log.error(e.getMessage(), e);
            }
        }
    }

    public List<MockHISPatientQueryDto> getDemoData() {
        List<MockHISPatientQueryDto> result = new ArrayList<>();
        if (configuration != null && configuration.getPatients() != null) {
            result = configuration.getPatients();
        }
        return result;
    }
}
