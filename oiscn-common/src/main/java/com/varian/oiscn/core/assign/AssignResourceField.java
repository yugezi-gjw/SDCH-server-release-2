package com.varian.oiscn.core.assign;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by bhp9696 on 2018/5/7.
 */
@Data
public class AssignResourceField implements Serializable{
    private Long id;
    private String category;
    private String name;
    private String value;
    private int sortNumber;
}
