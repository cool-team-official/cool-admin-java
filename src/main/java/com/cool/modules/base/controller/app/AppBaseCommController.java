package com.cool.modules.base.controller.app;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.eps.CoolEps;
import com.cool.core.request.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * app通用接口
 */
@RequiredArgsConstructor
@Tag(name = "应用通用", description = "应用通用")
@CoolRestController
public class AppBaseCommController {

    final private CoolEps coolEps;

    @TokenIgnore
    @Operation(summary = "实体信息与路径", description = "系统所有的实体信息与路径，供前端自动生成代码与服务")
    @GetMapping("/eps")
    public R eps() {
        return R.ok(coolEps.getApp());
    }
}
