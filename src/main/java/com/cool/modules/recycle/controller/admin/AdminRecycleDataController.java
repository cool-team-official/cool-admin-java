package com.cool.modules.recycle.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.request.R;
import com.cool.modules.recycle.entity.RecycleDataEntity;
import com.cool.modules.recycle.entity.table.RecycleDataEntityTableDef;
import com.cool.modules.recycle.service.RecycleDataService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


/**
 * 数据回收站
 */
@Tag(name = "数据回收站", description = "数据回收站")
@CoolRestController(api = { "add", "delete", "update", "page", "list", "info" })
@RequiredArgsConstructor
public class AdminRecycleDataController extends BaseController<RecycleDataService, RecycleDataEntity> {
    final private RecycleDataService recycleDataService;

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setListOption(createOp().queryWrapper(QueryWrapper.create().orderBy(RecycleDataEntityTableDef.RECYCLE_DATA_ENTITY.CREATE_TIME, false)));
    }

    @Operation(summary = "恢复数据", description = "恢复数据")
    @PostMapping("/restore")
    public R restore(@RequestBody Map<String, Object> params) {
        return R.ok(this.service.restore(getIds(params)));
    }
}
