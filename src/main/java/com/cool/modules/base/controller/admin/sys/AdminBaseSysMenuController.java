package com.cool.modules.base.controller.admin.sys;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.request.CrudOption;
import com.cool.core.request.R;
import com.cool.core.util.I18nUtil;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import com.cool.modules.base.service.sys.BaseSysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 系统菜单
 */
@Tag(name = "系统菜单", description = "系统菜单")
@CoolRestController(api = {"add", "delete", "update", "page", "list", "info"})
public class AdminBaseSysMenuController extends
    BaseController<BaseSysMenuService, BaseSysMenuEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        CrudOption<BaseSysMenuEntity> transform = createOp()
            .transform(o -> {
                BaseSysMenuEntity entity = (BaseSysMenuEntity) o;
                entity.setName(I18nUtil.getI18nMenu(entity.getName()));
            });
        setPageOption(transform);
        setListOption(transform);
        setInfoOption(transform);
    }

    @Operation(summary = "创建代码", description = "创建代码")
    @PostMapping("/create")
    public R create(@RequestBody() Map<String, Object> params) {
        CoolPreconditions.checkEmpty(params.get("module"), "module参数不能为空");
        CoolPreconditions.checkEmpty(params.get("entity"), "entity参数不能为空");
        CoolPreconditions.checkEmpty(params.get("controller"), "controller参数不能为空");
        CoolPreconditions.checkEmpty(params.get("service"), "service参数不能为空");
        CoolPreconditions.checkEmpty(params.get("service-impl"), "service-impl参数不能为空");
        CoolPreconditions.checkEmpty(params.get("mapper"), "mapper参数不能为空");
        CoolPreconditions.checkEmpty(params.get("fileName"), "fileName参数不能为空");
        this.service.create(params);
        return R.ok();
    }

    @Operation(summary = "导出", description = "导出")
    @PostMapping("/export")
    public R export(@RequestBody Map<String, Object> params) {
        return R.ok(this.service.export(getIds(params)));
    }

    @Operation(summary = "导入", description = "导入")
    @PostMapping("/import")
    public R importMenu(@RequestBody Map<String, List<BaseSysMenuEntity>> params) {
        CoolPreconditions.checkEmpty(params.get("menus"), "参数不能为空");
        return R.ok(this.service.importMenu(params.get("menus")));
    }
}