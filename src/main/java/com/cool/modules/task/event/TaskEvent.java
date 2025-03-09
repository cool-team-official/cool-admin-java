package com.cool.modules.task.event;

import com.cool.modules.task.service.TaskInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 事件监听
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEvent {

    final private TaskInfoService taskInfoService;

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws Exception {
        taskInfoService.init();
        log.info("初始化任务");
    }
}
