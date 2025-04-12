package com.cool.modules.plugin.controller.admin;

import static com.cool.modules.plugin.entity.table.PluginInfoEntityTableDef.PLUGIN_INFO_ENTITY;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.IgnoreRecycleData;
import com.cool.core.base.BaseController;
import com.cool.core.plugin.service.CoolPluginService;
import com.cool.core.request.R;
import com.cool.modules.plugin.entity.PluginInfoEntity;
import com.cool.modules.plugin.service.PluginInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "插件信息", description = "插件信息")
@CoolRestController(api = {"add", "delete", "update", "page", "list", "info"})
@RequiredArgsConstructor
public class AdminPluginInfoController extends BaseController<PluginInfoService, PluginInfoEntity> {

    final private CoolPluginService coolPluginService;

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

        setPageOption(createOp().queryWrapper(
                QueryWrapper.create().orderBy(PLUGIN_INFO_ENTITY.UPDATE_TIME, false))
            .select(PLUGIN_INFO_ENTITY.DEFAULT_COLUMNS));
    }

    @Override
    @Operation(summary = "修改", description = "根据ID修改")
    @PostMapping("/update")
    protected R update(@RequestBody PluginInfoEntity t,
        @RequestAttribute() JSONObject requestParams) {
        if (ObjUtil.isNotEmpty(t.getConfig())) {
            t.setConfig(JSONUtil.parseObj(t.getConfig()));
        } else {
            t.setConfig(new HashMap<>());
        }
        coolPluginService.updatePlugin(t);
        return R.ok();
    }

    @Operation(summary = "安装插件")
    @PostMapping(value = "/install", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R install(
        @RequestParam(value = "files") @Parameter(description = "文件") MultipartFile[] files,
        @RequestParam(value = "force") @Parameter(description = "是否强制安装") boolean force) {
        coolPluginService.install(files[0], force);
        return R.ok();
    }

    @Override
    @Operation(summary = "卸载插件")
    @PostMapping("/delete")
    @IgnoreRecycleData()
    public R delete(HttpServletRequest request, @RequestBody Map<String, Object> params,
        @RequestAttribute() JSONObject requestParams) {
        coolPluginService.uninstall(Convert.toLongArray(getIds(params))[0]);
        return R.ok();
    }
}
