package com.cool.modules.space.controller.admin;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.modules.space.entity.SpaceTypeEntity;
import com.cool.modules.space.service.SpaceTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 文件空间信息
 */
@Tag(name = "文件空间信息", description = "文件空间信息")
@CoolRestController(api = { "add", "delete", "update", "page", "list", "info" })
public class AdminSpaceTypeController extends BaseController<SpaceTypeService, SpaceTypeEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {

    }
}