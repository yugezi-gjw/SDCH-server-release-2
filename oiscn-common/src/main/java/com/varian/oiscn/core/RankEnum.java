package com.varian.oiscn.core;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by fmk9441 on 2017-06-20.
 */
public enum RankEnum {
    ASC,
    DESC;

    public static String getDisplay(RankEnum rankEnum) {
        if (ASC.equals(rankEnum)) {
            return "asc";
        } else {
            return "desc";
        }
    }

    public static RankEnum fromCode(String rank) {
        if (StringUtils.equalsIgnoreCase(rank, "asc")) {
            return RankEnum.ASC;
        } else {
            return RankEnum.DESC;
        }
    }
}
