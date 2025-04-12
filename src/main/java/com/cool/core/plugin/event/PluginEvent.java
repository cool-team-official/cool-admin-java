package com.cool.core.plugin.event;

import com.cool.modules.plugin.entity.PluginInfoEntity;
import java.time.Clock;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PluginEvent extends ApplicationEvent {
    @Getter
    private String key;
    @Getter
    private PluginActionEnum actionEnum;
    @Getter
    private PluginInfoEntity pluginInfoEntity;
    public PluginEvent(Object source, String key, PluginActionEnum actionEnum, PluginInfoEntity data) {
        super(source);
        this.key = key;
        this.actionEnum = actionEnum;
        this.pluginInfoEntity = data;
    }

    public PluginEvent(Object source, Clock clock) {
        super(source, clock);
    }
}

