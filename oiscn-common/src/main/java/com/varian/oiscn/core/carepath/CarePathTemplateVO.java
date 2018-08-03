package com.varian.oiscn.core.carepath;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.varian.oiscn.core.encounter.EncounterCarePath;
import lombok.Data;

/**
 * Care path Template List Item for view.<br>
 */
@Data
public class CarePathTemplateVO {
    /**
     * Id : Template Id in Aria
     **/
    protected String id;
    /**
     * Name: template name in Chinese, configured in File
     */
    protected String name;
    /**
     * Description: configured in Chinese
     */
    protected String description;

    @JsonIgnore
    protected EncounterCarePath.EncounterCarePathCategoryEnum category;
}
