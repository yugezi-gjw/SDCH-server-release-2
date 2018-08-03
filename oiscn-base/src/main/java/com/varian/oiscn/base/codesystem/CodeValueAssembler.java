package com.varian.oiscn.base.codesystem;

import com.varian.oiscn.core.codesystem.CodeSystem;
import com.varian.oiscn.core.codesystem.CodeValue;
import com.varian.oiscn.core.codesystem.CodeValueDTO;

import static ca.uhn.fhir.util.ElementUtil.isEmpty;

/**
 * Created by gbt1220 on 6/19/2017.
 */
public class CodeValueAssembler {

    private CodeValueAssembler(){

    }

    public static CodeValueDTO assemblerCodeValueDTO(CodeSystem codeSystem, CodeValue codeValue) {
        CodeValueDTO codeValueDTO = new CodeValueDTO();
        codeValueDTO.setCode(codeSystem.getCode());
        codeValueDTO.setSystem(codeSystem.getSystem());
        codeValueDTO.setValue(codeValue.getCode());
        codeValueDTO.setDesc(htmlEscape(codeValue.getDesc()));
        return codeValueDTO;
    }

    public static String htmlEscape(String input) {
        if (isEmpty(input)) {
            return input;
        }
        String tmpInput = input;
        tmpInput = tmpInput.replaceAll("&", "&amp;");
        tmpInput = tmpInput.replaceAll("<", "&lt;");
        tmpInput = tmpInput.replaceAll(">", "&gt;");
        tmpInput = tmpInput.replaceAll(" ", "&nbsp;");
        tmpInput = tmpInput.replaceAll("'", "&#39;");   //IE暂不支持单引号的实体名称,而支持单引号的实体编号,故单引号转义成实体编号,其它字符转义成实体名称
        tmpInput = tmpInput.replaceAll("\"", "&quot;"); //双引号也需要转义，所以加一个斜线对其进行转义
        tmpInput = tmpInput.replaceAll("\n", "<br/>");  //不能把\n的过滤放在前面，因为还要对<和>过滤，这样就会导致<br/>失效了
        return tmpInput;
    }

    public static String htmlDescape(String input) {
        if (isEmpty(input)) {
            return input;
        }
        String tmpInput = input;
        tmpInput = tmpInput.replaceAll("&amp;", "&");
        tmpInput = tmpInput.replaceAll("&lt;", "<");
        tmpInput = tmpInput.replaceAll("&gt;", ">");
        tmpInput = tmpInput.replaceAll("&nbsp;", " ");
        tmpInput = tmpInput.replaceAll("&#39;", "'");   //IE暂不支持单引号的实体名称,而支持单引号的实体编号,故单引号转义成实体编号,其它字符转义成实体名称
        tmpInput = tmpInput.replaceAll("&quot;", "\""); //双引号也需要转义，所以加一个斜线对其进行转义
        tmpInput = tmpInput.replaceAll("<br/>", "\n");  //不能把\n的过滤放在前面，因为还要对<和>过滤，这样就会导致<br/>失效了
        return tmpInput;
    }
}
