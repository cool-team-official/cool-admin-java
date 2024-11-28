package com.cool.modules.task.controller.admin;

import static com.cool.modules.task.entity.table.TaskInfoEntityTableDef.TASK_INFO_ENTITY;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.request.R;
import com.cool.modules.task.entity.TaskInfoEntity;
import com.cool.modules.task.service.TaskInfoService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

/**
 * 任务
 */
@Tag(name = "任务管理", description = "统一管理任务")
@CoolRestController(api = { "add", "delete", "update", "info", "page" })
public class AdminTaskInfoController extends BaseController<TaskInfoService, TaskInfoEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(createOp().fieldEq(TASK_INFO_ENTITY.STATUS, TASK_INFO_ENTITY.TYPE));
    }

    @Operation(summary = "执行一次")
    @PostMapping("/once")
    public R once(@RequestAttribute JSONObject requestParams) {
        service.once(requestParams.getLong("id"));
        return R.ok();
    }

    @Operation(summary = "开始任务")
    @PostMapping("/start")
    public R start(@RequestAttribute JSONObject requestParams) {
        service.start(requestParams.getLong("id"), requestParams.getInt("type"));
        return R.ok();
    }

    @Operation(summary = "停止任务")
    @PostMapping("/stop")
    public R stop(@RequestAttribute JSONObject requestParams) {
        service.stop(requestParams.getLong("id"));
        return R.ok();
    }

    @Operation(summary = "任务日志")
    @GetMapping("/log")
    public R log(@RequestAttribute JSONObject requestParams) {
        Integer page = requestParams.getInt("page", 0);
        Integer size = requestParams.getInt("size", 20);
        return R.ok(pageResult((Page) service.log(new Page<>(page, size), requestParams.getLong("id"),
                requestParams.getInt("status"))));
    }
}
