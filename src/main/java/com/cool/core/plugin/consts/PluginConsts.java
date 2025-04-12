package com.cool.core.plugin.consts;

/**
 * 常量工具
 */
public interface PluginConsts {
    /**
     * 国际化插件
     */
    String i18n = "i18n";
    /**
     * 上传文件hook
     */
    String uploadHook = "upload";

    /**
     * 验证码hook
     */
    String captchaHook = "captcha";

    /**
     * 插件调用入口方法
     */
    String invokePluginMethodName = "invokePlugin";

    /**
     * 设置插件信息
     */
    String setPluginJson = "setPluginJson";

    /**
     * 设置 ApplicationContext，使得插件能够调用主应用spring容器中的bean
     */
    String setApplicationContext = "setApplicationContext";
}
