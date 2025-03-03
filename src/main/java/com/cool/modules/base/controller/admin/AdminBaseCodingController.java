package com.cool.modules.base.controller.admin;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.R;
import com.cool.modules.base.dto.sys.CodeContentDto;
import com.cool.modules.base.service.sys.BaseCodingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

/**
 * ai 编码
 */
@CoolRestController
@RequiredArgsConstructor
public class AdminBaseCodingController {

    private final BaseCodingService baseCodingService;

    @TokenIgnore
    @Operation(summary = "获取模块目录结构", description = "获取模块目录结构")
    @GetMapping("/getModuleTree")
    public R getModuleTree() {
        return R.ok(baseCodingService.getModuleTree());
    }

    @TokenIgnore
    @Operation(summary = "创建代码", description = "创建代码")
    @PostMapping("/createCode")
    public R createCode(@RequestAttribute JSONObject requestParams) {
        JSONArray codes = requestParams.get("codes", JSONArray.class);
        CoolPreconditions.checkEmpty(codes);
        this.baseCodingService.createCode(codes.toList(CodeContentDto.class));
        return R.ok();
    }
}
