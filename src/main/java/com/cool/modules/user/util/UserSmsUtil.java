package com.cool.modules.user.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.cool.core.cache.CoolCache;
import com.cool.core.plugin.service.CoolPluginService;
import com.cool.core.util.CoolPluginInvokers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserSmsUtil - 用户短信工具类
 * 该类用于发送短信验证码。
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class UserSmsUtil {

    private final CoolPluginService coolPluginService;

    private final CoolCache coolCache;

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param code
     */
    void sendVerifyCode(String phone, String code) {
        // 随机生成4位验证码
        String verifyCode = RandomUtil.randomNumbers(4);
        send(phone, verifyCode);
        coolCache.set("sms:" + phone, verifyCode, 60 * 10);
    }

    /**
     * 检查验证码
     * @param phone
     * @param code
     * @return
     */
    boolean checkVerifyCode(String phone, String code) {
        String cacheCode = coolCache.get("sms:" + phone, String.class);
        return StrUtil.isNotEmpty(code) && code.equals(cacheCode);
    }


    /**
     * 发送短信
     *
     * @param phone
     * @param code
     */
    void send(String phone, String code) {
        List<String> phones = new ArrayList<>();
        phones.add("xxx");

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        // 插件key sms-tx、sms-ali，哪个实例存在就调用哪个
        if (coolPluginService.getInstance("sms-tx") != null) {
            // 调用腾讯短信插件
        } else if (coolPluginService.getInstance("sms-ali") != null) {
            // 调用阿里短信插件
            CoolPluginInvokers.invoke("sms-ali", "send", phones, params);
        } else {
            // 未找到短信插件
            log.error("未找到短信插件，请前往插件市场下载安装");
        }
    }
}
