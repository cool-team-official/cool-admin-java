package com.cool.modules.base.controller.admin;

import static com.cool.core.plugin.consts.PluginConsts.captchaHook;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.eps.CoolEps;
import com.cool.core.plugin.service.CoolPluginService;
import com.cool.core.request.R;
import com.cool.core.util.CoolPluginInvokers;
import com.cool.modules.base.dto.sys.BaseSysLoginDto;
import com.cool.modules.base.service.sys.BaseSysLoginService;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统开放接口，无需权限校验
 */
@RequiredArgsConstructor
@Tag(name = "系统开放", description = "系统开放")
@CoolRestController()
public class AdminBaseOpenController {

    final private BaseSysLoginService baseSysLoginService;
    final private CoolPluginService coolPluginService;
    final private CoolEps coolEps;
    final private CoolCache coolCache;

    @Operation(summary = "实体信息与路径", description = "系统所有的实体信息与路径，供前端自动生成代码与服务")
    @GetMapping("/eps")
    public R eps() {
        return R.ok(coolEps.getAdmin());
    }

    @Operation(summary = "获得网页内容的参数值")
    @GetMapping("/html")
    public R html() {
        return R.ok();
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R login(@RequestBody BaseSysLoginDto baseSysLoginDto) {
        return R.ok(baseSysLoginService.login(baseSysLoginDto));
    }

    @Operation(summary = "验证码")
    @GetMapping("/captcha")
    public R captcha(@Parameter(description = "类型：svg|base64") @RequestParam(defaultValue = "base64") String type,
        @Parameter(description = "宽度") @RequestParam(defaultValue = "150") Integer width,
        @Parameter(description = "高度") @RequestParam(defaultValue = "50") Integer height) {
        return R.ok(baseSysLoginService.captcha(UserTypeEnum.ADMIN, type, width, height));
    }

    @Operation(summary = "刷新token")
    @GetMapping("/refreshToken")
    public R refreshToken(String refreshToken) {
        return R.ok(baseSysLoginService.refreshToken(refreshToken));
    }

    @RequestMapping("/gen")
    @ResponseBody
    public Object genCaptcha(@RequestParam(value = "type", required = false)String type) {
        if (StringUtils.isBlank(type)) {
            type = "SLIDER";
        }
        if ("RANDOM".equals(type)) {
            int i = ThreadLocalRandom.current().nextInt(0, 4);
            if (i == 0) {
                type = "SLIDER";
            } else if (i == 1) {
                type = "CONCAT";
            } else if (i == 2) {
                type = "ROTATE";
            } else{
                type = "WORD_IMAGE_CLICK";
            }

        }
        return CoolPluginInvokers.invoke("tianai", "generateCaptcha", type);
    }

    @PostMapping("/check")
    @ResponseBody
    public Object checkCaptcha(@RequestAttribute() JSONObject requestParams) {
        Object result = CoolPluginInvokers.invoke("tianai", "matching", requestParams);
        Map<String, Object> map = BeanUtil.beanToMap(result);
        if (ObjUtil.equals(map.get("code"), 200)) {
            String code = ThreadLocalRandom.current().nextInt(100000, 999999) + "";
            coolCache.set("verify:img:" + requestParams.getStr("id"), code, 1800);
            R r = new R();
            r.put("data", Map.of("id", requestParams.getStr("id"),
                "code", code));
            r.put("code", map.get("code"));
            return r;
        }
        return result;
    }

    @Operation(summary = "验证码类型")
    @GetMapping("/captchaMode")
    public R captchaMode() {
        PluginInfoEntity pluginInfoEntity = coolPluginService.getPluginInfoEntityByHook(
            captchaHook);
        if (pluginInfoEntity != null) {
            return R.ok(CoolPluginInvokers.invoke(pluginInfoEntity.getKey(), "getMode"));
        }
        return R.ok(Map.of("mode", "common"));
    }
}
