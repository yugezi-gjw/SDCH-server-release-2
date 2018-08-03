package com.varian.oiscn.core.assign;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by bhp9696 on 2018/3/23.
 */
@Data
public class AssignResourceVO {
//  资源的ID
    private String id;
//  资源编码
    private String code;
//    资源名称
    private String name;
//    资源中的数量
    private int amount;
//    资源的颜色值
    private String color;

    private List<Map<String, String>> patientSerInstanceIdPairList;
}
