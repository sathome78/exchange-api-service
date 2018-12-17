package me.exrates.openapi.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Initializer {

    @Autowired
    private Map<String, InitNeeded> map;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        for (Map.Entry<String, InitNeeded> stringInitNeededEntry : map.entrySet()) {
            stringInitNeededEntry.getValue().init();
        }
    }
}
