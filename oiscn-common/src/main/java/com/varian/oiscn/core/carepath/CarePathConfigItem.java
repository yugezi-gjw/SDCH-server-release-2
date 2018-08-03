package com.varian.oiscn.core.carepath;

import com.varian.oiscn.core.encounter.EncounterCarePath;
import lombok.Data;

/**
 * Care path Template List Item.<br>
 * The item is used for care path dropdown list.<br>
 */
@Data
public class CarePathConfigItem {
    /**
     * Id : Template Id in Aria
     **/
    protected String templateId;
    /**
     * Name: template name in Chinese, configured in File
     */
    protected String templateName;
    /**
     * Description: configured in Chinese
     */
    protected String description;
    /**
     * PRIMARY OPTIONAL
     */
    protected EncounterCarePath.EncounterCarePathCategoryEnum category;
}
