package com.cool.core.plugin.event;

import com.cool.modules.plugin.entity.PluginInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PluginEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(String key, PluginActionEnum actionEnum, PluginInfoEntity data) {
        PluginEvent event = new PluginEvent(this, key, actionEnum, data);
        applicationEventPublisher.publishEvent(event);
    }


}
