package com.varian.oiscn.core.order;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gbt1220 on 3/24/2017.
 */
public enum OrderStatusEnum {
    DRAFT,
    REQUESTED,
    RECEIVED,
    ACCEPTED,
    REJECTED,
    READY,
    CANCELLED,
    IN_PROGRESS,
    ON_HOLD,
    FAILED,
    COMPLETED,
    ENTERED_IN_ERROR;

    public static String getDisplay(OrderStatusEnum orderStatusEnum) {
        switch (orderStatusEnum) {
            case DRAFT:
                return "draft";
            case REQUESTED:
                return "requested";
            case RECEIVED:
                return "received";
            case ACCEPTED:
                return "accepted";
            case REJECTED:
                return "rejected";
            case READY:
                return "ready";
            case CANCELLED:
                return "cancelled";
            case IN_PROGRESS:
                return "in-progress";
            case ON_HOLD:
                return "on-hold";
            case FAILED:
                return "failed";
            case COMPLETED:
                return "completed";
            case ENTERED_IN_ERROR:
                return "entered-in-error";
            default:
                return "entered-in-error";
        }
    }

    public static OrderStatusEnum fromCode(String orderStatus) {
        if (StringUtils.equalsIgnoreCase(orderStatus, "draft")) {
            return OrderStatusEnum.DRAFT;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "requested")) {
            return OrderStatusEnum.REQUESTED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "received")) {
            return OrderStatusEnum.RECEIVED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "accepted")) {
            return OrderStatusEnum.ACCEPTED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "rejected")) {
            return OrderStatusEnum.REJECTED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "ready")) {
            return OrderStatusEnum.READY;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "cancelled")) {
            return OrderStatusEnum.CANCELLED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "in-progress")) {
            return OrderStatusEnum.IN_PROGRESS;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "on-hold")) {
            return OrderStatusEnum.ON_HOLD;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "failed")) {
            return OrderStatusEnum.FAILED;
        } else if (StringUtils.equalsIgnoreCase(orderStatus, "completed")) {
            return OrderStatusEnum.COMPLETED;
        } else {
            return OrderStatusEnum.ENTERED_IN_ERROR;
        }
    }
}
