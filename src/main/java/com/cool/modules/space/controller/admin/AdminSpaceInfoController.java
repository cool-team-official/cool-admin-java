package com.cool.modules.space.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.modules.space.entity.SpaceInfoEntity;
import com.cool.modules.space.service.SpaceInfoService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import static com.cool.modules.space.entity.table.SpaceInfoEntityTableDef.SPACE_INFO_ENTITY;

/**
 * 文件空间信息
 */
@Tag(name = "文件空间信息", description = "文件空间信息")
@CoolRestController(api = { "add", "delete", "update", "page", "list", "info" })
public class AdminSpaceInfoController extends BaseController<SpaceInfoService, SpaceInfoEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(createOp().fieldEq(SPACE_INFO_ENTITY.TYPE, SPACE_INFO_ENTITY.CLASSIFY_ID));
    }
}
