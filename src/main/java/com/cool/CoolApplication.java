package com.cool;

import com.cool.core.util.PathUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.autotable.springboot.EnableAutoTable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * CoolApplication - 应用程序的主类
 * 该类配置并运行应用程序。
 */
@Slf4j
@EnableAutoTable // 开启自动建表
@EnableAsync // 开启异步处理
@EnableCaching // 开启缓存
@SpringBootApplication
@MapperScan("com.cool.**.mapper") // 扫描指定包中的MyBatis映射器
public class CoolApplication {

    private static volatile ConfigurableApplicationContext context;
    private static ClassLoader mainThreadClassLoader;

    public static void main(String[] args) {
        mainThreadClassLoader = Thread.currentThread().getContextClassLoader();
        context = SpringApplication.run(CoolApplication.class, args);
    }

    /**
     * 通过关闭当前上下文并启动新上下文来重启应用程序。
     */
    public static void restart(List<String> javaPathList) {
        // 从当前上下文获取应用程序参数
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        // 创建新线程来重启应用程序
        Thread thread = new Thread(() -> {
            try {
                // 关闭当前应用程序上下文
                context.close();
                // 等待上下文完全关闭
                while (context.isActive()) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 加载动态生成的代码
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            javaPathList.forEach(javaPath -> {
                try {
                    classLoader.loadClass(PathUtils.getClassName(javaPath));
                } catch (ClassNotFoundException e) {
                    log.error("loadClassErr {}", javaPath, e);
                }
            });
            // 使用相同的参数运行Spring Boot应用程序并设置上下文
            context = SpringApplication.run(CoolApplication.class, args.getSourceArgs());
        });
        // 设置线程的上下文类加载器
        thread.setContextClassLoader(mainThreadClassLoader);
        // 确保线程不是守护线程
        thread.setDaemon(false);
        // 启动线程
        thread.start();
    }

}
