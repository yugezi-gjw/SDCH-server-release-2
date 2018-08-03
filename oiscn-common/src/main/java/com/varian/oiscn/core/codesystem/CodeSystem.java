package com.varian.oiscn.core.codesystem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gbt1220 on 3/28/2017.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeSystem {
    private String code;
    private String system;
    private List<CodeValue> codeValues;

    public void addCodeValue(CodeValue codeValue){
        if(null == codeValues){
            codeValues = new ArrayList<>();
        }

        codeValues.add(codeValue);
    }
}
