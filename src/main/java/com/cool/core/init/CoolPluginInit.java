package com.cool.core.init;

import com.cool.core.plugin.service.CoolPluginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 历史安装过的插件执行初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CoolPluginInit {

    final private CoolPluginService coolPluginService;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        coolPluginService.init();
    }
}
