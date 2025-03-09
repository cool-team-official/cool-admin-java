package com.cool.core.init;

import com.cool.core.leaf.IDGenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 唯一ID 组件初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class IDGenInit {

    final private IDGenService idGenService;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        idGenService.init();
    }
}
