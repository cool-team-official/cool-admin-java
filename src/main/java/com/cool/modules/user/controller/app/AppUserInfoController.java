package com.cool.modules.user.controller.app;

import com.cool.core.annotation.CoolRestController;
import com.cool.core.request.R;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.core.util.EntityUtils;
import com.cool.modules.user.entity.UserInfoEntity;
import com.cool.modules.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Tag(name = "用户信息", description = "用户信息")
@CoolRestController
public class AppUserInfoController {

    private final UserInfoService userInfoService;

    @Operation(summary = "用户个人信息", description = "获得App、小程序或者其他应用的用户个人信息")
    @GetMapping("/person")
    public R person() {
        Long userId = CoolSecurityUtil.getCurrentUserId();
        UserInfoEntity userInfoEntity = userInfoService.person(userId);
        return R.ok(EntityUtils.toMap(userInfoEntity,
            "password"));
    }
}
