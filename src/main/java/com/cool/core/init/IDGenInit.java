package com.cool.core.init;

import com.cool.core.leaf.IDGenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 唯一ID 组件初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class IDGenInit implements ApplicationRunner {

    final private IDGenService idGenService;

    @Override
    public void run(ApplicationArguments args) {
        idGenService.init();
    }
}
