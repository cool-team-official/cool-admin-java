package com.cool.core.init;

import com.cool.core.plugin.service.CoolPluginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 历史安装过的插件执行初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class CoolPluginInit implements ApplicationRunner {

    final private CoolPluginService coolPluginService;

    @Override
    public void run(ApplicationArguments args) {
        coolPluginService.init();
    }
}
