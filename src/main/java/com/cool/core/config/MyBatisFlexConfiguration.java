package com.cool.core.config;

import com.cool.core.tenant.CoolTenantFactory;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // 我们可以在这里进行一些列的初始化配置

        // 指定多租户列的列名
        FlexGlobalConfig.getDefaultConfig().setTenantColumn("tenant_id");
    }

    @Bean
    @ConditionalOnProperty(name = "cool.multi-tenant.enable", havingValue = "true")
    public TenantFactory tenantFactory(){
        return new CoolTenantFactory();
    }
}