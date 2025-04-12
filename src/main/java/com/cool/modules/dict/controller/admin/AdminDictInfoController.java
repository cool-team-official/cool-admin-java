package com.cool.modules.dict.controller.admin;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.annotation.TokenIgnore;
import com.cool.core.base.BaseController;
import com.cool.core.request.CrudOption;
import com.cool.core.request.R;
import com.cool.core.util.I18nUtil;
import com.cool.modules.dict.entity.DictInfoEntity;
import com.cool.modules.dict.entity.table.DictInfoEntityTableDef;
import com.cool.modules.dict.service.DictInfoService;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 字典信息
 */
@Tag(name = "字典信息", description = "字典信息")
@CoolRestController(api = {"add", "delete", "update", "page", "list", "info"})
public class AdminDictInfoController extends BaseController<DictInfoService, DictInfoEntity> {
    @Override
    protected void init(HttpServletRequest request, JSONObject requestParams) {
        setListOption(createOp().fieldEq(DictInfoEntityTableDef.DICT_INFO_ENTITY.TYPE_ID)
            .keyWordLikeFields(DictInfoEntityTableDef.DICT_INFO_ENTITY.NAME)
            .queryWrapper(QueryWrapper.create().orderBy(DictInfoEntityTableDef.DICT_INFO_ENTITY.CREATE_TIME, false))
            .transform(o -> {
                DictInfoEntity entity = (DictInfoEntity) o;
                entity.setName(I18nUtil.getI18nDictInfo(entity.getName()));
            }));
        CrudOption<DictInfoEntity> transform = createOp().transform(o -> {
            DictInfoEntity entity = (DictInfoEntity) o;
            entity.setName(I18nUtil.getI18nDictInfo(entity.getName()));
        });
        setPageOption(transform);
        setInfoOption(transform);
    }

    @Operation(summary = "获得字典数据", description = "获得字典数据信息")
    @PostMapping("/data")
    public R data(@RequestBody Dict body) {
        return R.ok(this.service.data(body.get("types", null)));
    }

    @TokenIgnore
    @GetMapping("/types")
    @Operation(summary = "获得字典数据", description = "获得字典数据信息")
    public R types() {
        return R.ok(this.service.types());
    }
}
