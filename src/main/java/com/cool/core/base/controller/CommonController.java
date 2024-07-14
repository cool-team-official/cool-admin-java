package com.cool.core.base.controller;

import cn.hutool.core.util.ObjUtil;
import com.cool.core.plugin.service.CoolPluginService;
import com.cool.core.util.ConvertUtil;
import com.cool.core.util.CoolPluginInvokers;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class CommonController {

    final private CoolPluginService coolPluginService;

    @RequestMapping("/")
    public String welcome() {
        return "welcome";
    }

    @PostMapping("/testPlugin/invokeMethod")
    @ResponseBody
    public String invokeMethod(@RequestParam String key, @RequestParam String methodName) {
        Object result = null;
        if (ObjUtil.isEmpty(methodName)) {
            result = CoolPluginInvokers.invokePlugin(key);
        } else {
            result = CoolPluginInvokers.invokePlugin(key, methodName);
        }
        System.out.println(result);
        return "invokeMethod Result: " + result;
    }

    /**
     * 指定目录加载插件
     */
    @PostMapping("/testPlugin/reload")
    @ResponseBody
    public String reload() {
        // 替换掉自己插件编译的路径，无需在页面上上传
        File file = new File(
            "/Users/mac/work/cool_new/cool-admin-java-plugin/target/my_cool_plugin.cool");
        MultipartFile multipartFile = ConvertUtil.convertToMultipartFile(file);
        coolPluginService.install(multipartFile, true);
        return "reload Success";
    }
}
