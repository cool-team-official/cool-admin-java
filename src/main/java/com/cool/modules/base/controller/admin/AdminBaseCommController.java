package com.cool.modules.base.controller.admin;

import cn.hutool.core.lang.Dict;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.eps.CoolEps;
import com.cool.core.file.FileUploadStrategyFactory;
import com.cool.core.request.R;
import com.cool.core.util.I18nUtil;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import com.cool.modules.base.service.sys.BaseSysLoginService;
import com.cool.modules.base.service.sys.BaseSysPermsService;
import com.cool.modules.base.service.sys.BaseSysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统通用接口， 每个人都有权限操作
 */
@RequiredArgsConstructor
@Tag(name = "系统通用", description = "系统通用")
@CoolRestController()
public class AdminBaseCommController {

    final private BaseSysPermsService baseSysPermsService;

    final private BaseSysUserService baseSysUserService;

    final private BaseSysLoginService baseSysLoginService;

    final private CoolEps coolEps;

    final private FileUploadStrategyFactory fileUploadStrategyFactory;

    @TokenIgnore
    @Operation(summary = "实体信息与路径", description = "系统所有的实体信息与路径，供前端自动生成代码与服务")
    @GetMapping("/eps")
    public R eps() {
        return R.ok(coolEps.getAdmin());
    }

    @Operation(summary = "个人信息")
    @GetMapping("/person")
    public R person(@RequestAttribute() Long adminUserId) {
        BaseSysUserEntity baseSysUserEntity = baseSysUserService.getById(adminUserId);
        baseSysUserEntity.setPassword(null);
        baseSysUserEntity.setPasswordV(null);
        return R.ok(baseSysUserEntity);
    }

    @Operation(summary = "修改个人信息")
    @PostMapping("/personUpdate")
    public R personUpdate(@RequestAttribute Long adminUserId, @RequestBody Dict body) {
        baseSysUserService.personUpdate(adminUserId, body);
        return R.ok();
    }

    @Operation(summary = "权限与菜单")
    @GetMapping("/permmenu")
    public R permmenu(@RequestAttribute() Long adminUserId) {
        Dict permmenu = baseSysPermsService.permmenu(adminUserId);
        List<BaseSysMenuEntity> list = (List<BaseSysMenuEntity>) permmenu.getObj("menus");
        list.forEach(o -> o.setName(I18nUtil.getI18nMenu(o.getName())));
        return R.ok(permmenu);
    }

    @Operation(summary = "文件上传")
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
        MediaType.ALL_VALUE})
    public R upload(
        @RequestPart(value = "file", required = false) @Parameter(description = "文件") MultipartFile[] files,
        HttpServletRequest request) {
        return R.ok(fileUploadStrategyFactory.upload(files, request));
    }

    @Operation(summary = "文件上传模式")
    @GetMapping("/uploadMode")
    public R uploadMode() {
        return R.ok(fileUploadStrategyFactory.getMode());
    }

    @Operation(summary = "退出")
    @PostMapping("/logout")
    public R logout(@RequestAttribute Long adminUserId, @RequestAttribute String adminUsername) {
        baseSysLoginService.logout(adminUserId, adminUsername);
        return R.ok();
    }

    @TokenIgnore
    @Operation(summary = "编程")
    @GetMapping("/program")
    public R program() {
        return R.ok("Java");
    }
}
