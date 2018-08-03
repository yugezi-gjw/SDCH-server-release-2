package com.varian.oiscn.base.diagnosis;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by bhp9696 on 2017/12/19.
 */
@AllArgsConstructor
@Data
public class BodyPartVO {
    private String code;
    private String desc;
    private String pinyin;
}
