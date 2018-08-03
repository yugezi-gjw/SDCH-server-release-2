package com.varian.oiscn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.varian.oiscn.core.appointment.calling.CallingConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class is located in the oiscn-common package temporarily.
 * In the future it would be moved to another separated project as a micro-service
 */
@NoArgsConstructor
@Getter
@Setter
public class Configuration extends io.dropwizard.Configuration {

    @JsonProperty
    public io.dropwizard.client.JerseyClientConfiguration httpClientConfiguration = new io.dropwizard.client.JerseyClientConfiguration();

    @JsonProperty
    @NotNull
    private DbConfiguration database = new DbConfiguration();

    @JsonProperty
    private int defaultTokenCacheTimeoutInMinutes = 30;

    @JsonProperty
    private int resourceAutoUnlockInMinutes = 5;
    
    @JsonProperty
    @NotNull
    private String fhirServerBaseUri = "";

    @JsonProperty
    private FhirServerConfiguration fhirServerConfiguration = new FhirServerConfiguration();

    @JsonProperty
    private LocaleConfiguration locale = new LocaleConfiguration();

    @NotNull
    private String defaultCarePathTemplateName = "QinDemo";

    @JsonProperty
    @NotNull
    private String diagnosisCodeScheme = "http://hl7.org/fhir/sid/icd-10";

    @JsonProperty
    @NotNull
    private String urgentStatusIconDesc = "Urgent";

    @JsonProperty
    @NotNull
    private String activeStatusIconDesc = "Patient Portal";

    @JsonProperty
    private String alertPatientLabelDesc = "Alert";

    @JsonProperty
    private String stagingCodeScheme = "";

    @JsonProperty
    @NotNull
    private String ospAuthenticationWsdlUrl = "";

    @JsonProperty
    @NotNull
    private String ospAuthorizationWsdlUrl = "";

    @JsonProperty
    private int ospTokenValidationInterval = 5;

    @JsonProperty
    private String serverAddressOfCCIP = "";

    @JsonProperty
    @NotNull
    private String keyActivityType = "keyActivityByLane";

    @JsonProperty
    @NotNull
    private String keyActivityTypeValue = "Contouring";

    @Deprecated
    @JsonProperty
    @NotNull
    private DeviceTimeSettingConfiguration deviceTimeSettingConfiguration = new DeviceTimeSettingConfiguration();

    @JsonProperty
    private String callingConfigFile = "integration/CallingSystem.yaml";

    @JsonProperty
    private Integer fhirTokenAuthEnabled = 1;

    @JsonProperty
    private String carePathConfigFile = "config/CarePath.yaml";
    private CarePathConfig carePathConfig = new CarePathConfig();
    
    private CallingConfig callingConfig = new CallingConfig();

    @JsonProperty
    private String activityCodeConfigFile = "config/activitycode.yaml";
    
    @JsonProperty
    private String permissionConfigFile = "config/Permission.yaml";
    
    @JsonProperty
    private String dynamicFormTemplateCategory = "CCIP";
    
    public String getFhirApiUri() {
        return fhirServerBaseUri + "/fhir";
    }

    public String getFhirMetadataUri() {
        return fhirServerBaseUri + "/fhir/metadata";
    }
}
