package com.varian.oiscn.core.order;

/**
 * Created by fmk9441 on 2017-06-28.
 */
public enum OrderRankEnum {
    TASK_CREATION_DATE;

    public static String getDisplay(OrderRankEnum orderRankEnum) {
        if (TASK_CREATION_DATE.equals(orderRankEnum)) {
            return "CreationDate";
        } else {
            return "entered-in-error";
        }
    }
}
