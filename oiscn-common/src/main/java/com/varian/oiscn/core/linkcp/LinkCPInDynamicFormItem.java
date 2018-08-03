package com.varian.oiscn.core.linkcp;

import lombok.Data;

@Data
public class LinkCPInDynamicFormItem {

    /**
     * activity code to link carepath
     */
    private String activityCode;

    /**
     * the dynamic form tag whether link carepath or not
     */
    private String dynamicFormTag;

    /**
     * the carepath template id
     */
    private String cpTemplateId;
}
