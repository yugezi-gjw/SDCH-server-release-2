package com.varian.oiscn.base.common;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

/**
 * Created by gbt1220 on 4/10/2017.
 */
public class EventHandlerRegistryTest {
    @Test
    public void givenWhenRegisterEventHandlerThenReturnTheHandler() {
        EventHandler handler = PowerMockito.mock(EventHandler.class);
        EventHandlerRegistry.registerEvent(EventHandlerConstants.PATIENT_REGISTER_TOPIC, handler);
        Assert.assertEquals(1, EventHandlerRegistry.getEventHandlers(EventHandlerConstants.PATIENT_REGISTER_TOPIC).size());
        Assert.assertEquals(handler, EventHandlerRegistry.getEventHandlers(EventHandlerConstants.PATIENT_REGISTER_TOPIC).get(0));
    }
}
