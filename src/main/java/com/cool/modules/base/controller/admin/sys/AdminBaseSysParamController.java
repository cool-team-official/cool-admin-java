package com.cool.modules.base.controller.admin.sys;

import static com.cool.modules.base.entity.sys.table.BaseSysParamEntityTableDef.BASE_SYS_PARAM_ENTITY;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.modules.base.entity.sys.BaseSysParamEntity;
import com.cool.modules.base.service.sys.BaseSysParamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 系统参数配置
 */
@Tag(name = "系统参数配置", description = "系统参数配置")
@CoolRestController(api = { "add", "delete", "update", "page", "info" })
public class AdminBaseSysParamController extends BaseController<BaseSysParamService, BaseSysParamEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(createOp().fieldEq(BASE_SYS_PARAM_ENTITY.DATA_TYPE)
            .keyWordLikeFields(BASE_SYS_PARAM_ENTITY.NAME, BASE_SYS_PARAM_ENTITY.KEY_NAME));
    }

    @Operation(summary = "根据键返回网页的参数值")
    @GetMapping("/html")
    public String html(String key) {
        return service.htmlByKey(key);
    }
}