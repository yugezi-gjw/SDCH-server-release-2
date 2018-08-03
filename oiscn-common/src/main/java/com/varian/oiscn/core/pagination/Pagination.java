package com.varian.oiscn.core.pagination;

import lombok.Data;

import java.util.List;

/**
 * Created by fmk9441 on 2017-08-23.
 */
@Data
public class Pagination<T> {
    private int totalCount;
    private List<T> lstObject;
}
