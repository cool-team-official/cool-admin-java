package com.cool.core.eps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 事件监听
 */
@Slf4j
@Component
@Profile({"local"})
@RequiredArgsConstructor
public class EpsEvent {

    final private CoolEps coolEps;

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        coolEps.init();
        log.info("构建eps信息");
    }
}
