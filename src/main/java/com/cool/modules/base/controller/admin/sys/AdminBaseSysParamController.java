package com.cool.modules.base.controller.admin.sys;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.modules.base.entity.sys.BaseSysParamEntity;
import com.cool.modules.base.service.sys.BaseSysParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统参数配置
 */
@Tag(name = "系统参数配置", description = "系统参数配置")
@CoolRestController(api = { "add", "delete", "update", "page", "info" })
public class AdminBaseSysParamController extends BaseController<BaseSysParamService, BaseSysParamEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
    }

    @Operation(summary = "根据键返回网页的参数值")
    @GetMapping("/html")
    public String html(String key) {
        return service.htmlByKey(key);
    }
}