package com.cool.modules.dict.controller.admin;

import static com.cool.modules.dict.entity.table.DictTypeEntityTableDef.DICT_TYPE_ENTITY;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.modules.dict.entity.DictTypeEntity;
import com.cool.modules.dict.service.DictTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 字典类型
 */
@Tag(name = "字典类型", description = "字典类型")
@CoolRestController(api = {"add", "delete", "update", "page", "list", "info"})
public class AdminDictTypeController extends BaseController<DictTypeService, DictTypeEntity> {

    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setPageOption(
            createOp().select(DICT_TYPE_ENTITY.ID, DICT_TYPE_ENTITY.KEY, DICT_TYPE_ENTITY.NAME));
    }
}