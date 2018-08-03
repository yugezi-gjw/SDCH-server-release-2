package com.varian.oiscn.anticorruption.converter;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by fmk9441 on 2017-05-18.
 */
public enum EnumMatchQuery {
    MATCHES,
    MATCHESEXACTLY,
    ASC,
    DESC;

    public static EnumMatchQuery fromCode(String enumMatchQuery) {
        if (StringUtils.equalsIgnoreCase(enumMatchQuery, "matches")) {
            return EnumMatchQuery.MATCHES;
        } else if (StringUtils.equalsIgnoreCase(enumMatchQuery, "matchesexactly")) {
            return EnumMatchQuery.MATCHESEXACTLY;
        } else if (StringUtils.equalsIgnoreCase(enumMatchQuery, "asc")) {
            return EnumMatchQuery.ASC;
        } else if (StringUtils.equalsIgnoreCase(enumMatchQuery, "desc")) {
            return EnumMatchQuery.DESC;
        }
        return null;
    }
}
