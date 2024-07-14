package com.cool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import com.tangzc.autotable.springboot.EnableAutoTable;

/**
 * CoolApplication - 应用程序的主类
 * 该类配置并运行应用程序。
 */
@EnableAutoTable // 开启自动建表
@EnableAsync // 开启异步处理
@EnableCaching // 开启缓存
@SpringBootApplication
@MapperScan("com.cool.modules.*.mapper") // 扫描指定包中的MyBatis映射器
public class CoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoolApplication.class, args);
    }
}