package com.varian.oiscn.core.order;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by gbt1220 on 3/24/2017.
 */
public class OrderStatusEnumTest {
    @Test
    public void givenOrderStatusEnumWhenGetDisplayThenReturnStatus() {
        Assert.assertEquals("draft", OrderStatusEnum.getDisplay(OrderStatusEnum.DRAFT));
        Assert.assertEquals("requested", OrderStatusEnum.getDisplay(OrderStatusEnum.REQUESTED));
        Assert.assertEquals("received", OrderStatusEnum.getDisplay(OrderStatusEnum.RECEIVED));
        Assert.assertEquals("accepted", OrderStatusEnum.getDisplay(OrderStatusEnum.ACCEPTED));
        Assert.assertEquals("rejected", OrderStatusEnum.getDisplay(OrderStatusEnum.REJECTED));
        Assert.assertEquals("ready", OrderStatusEnum.getDisplay(OrderStatusEnum.READY));
        Assert.assertEquals("cancelled", OrderStatusEnum.getDisplay(OrderStatusEnum.CANCELLED));
        Assert.assertEquals("in-progress", OrderStatusEnum.getDisplay(OrderStatusEnum.IN_PROGRESS));
        Assert.assertEquals("on-hold", OrderStatusEnum.getDisplay(OrderStatusEnum.ON_HOLD));
        Assert.assertEquals("failed", OrderStatusEnum.getDisplay(OrderStatusEnum.FAILED));
        Assert.assertEquals("completed", OrderStatusEnum.getDisplay(OrderStatusEnum.COMPLETED));
        Assert.assertEquals("entered-in-error", OrderStatusEnum.getDisplay(OrderStatusEnum.ENTERED_IN_ERROR));
    }

    @Test
    public void givenStatusWhenFromCodeThenReturnEnum() {
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.DRAFT), "draft");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.REQUESTED), "requested");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.RECEIVED), "received");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.ACCEPTED), "accepted");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.REJECTED), "rejected");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.READY), "ready");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.CANCELLED), "cancelled");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.IN_PROGRESS), "in-progress");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.ON_HOLD), "on-hold");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.FAILED), "failed");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.COMPLETED), "completed");
        Assert.assertEquals(OrderStatusEnum.getDisplay(OrderStatusEnum.ENTERED_IN_ERROR), "entered-in-error");
    }
}
