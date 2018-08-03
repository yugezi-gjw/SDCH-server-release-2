package com.varian.oiscn.base.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gbt1220 on 4/4/2017.
 */
public class EventHandlerRegistry {
    private static Map<String, List<EventHandler>> eventHandlerMap = new ConcurrentHashMap<>();

    private EventHandlerRegistry(){

    }

    public static void registerEvent(String eventType, EventHandler eventHandler) {
        if (!eventHandlerMap.containsKey(eventType)) {
            eventHandlerMap.put(eventType, new ArrayList<>());
        }
        eventHandlerMap.get(eventType).add(eventHandler);
    }

    public static List<EventHandler> getEventHandlers(String eventType) {
        return eventHandlerMap.get(eventType);
    }
}
