package com.cool.modules.base.controller.admin.sys;

import cn.hutool.json.JSONObject;
import com.cool.core.annotation.CoolRestController;
import com.cool.core.base.BaseController;
import com.cool.core.request.R;
import com.cool.modules.base.entity.sys.BaseSysLogEntity;
import com.cool.modules.base.entity.sys.table.BaseSysLogEntityTableDef;
import com.cool.modules.base.entity.sys.table.BaseSysUserEntityTableDef;
import com.cool.modules.base.service.sys.BaseSysConfService;
import com.cool.modules.base.service.sys.BaseSysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

/**
 * 系统日志
 */
@RequiredArgsConstructor
@Tag(name = "系统日志", description = "系统日志")
@CoolRestController(api = {"page"})
public class AdminBaseSysLogController extends BaseController<BaseSysLogService, BaseSysLogEntity> {

	private final BaseSysConfService baseSysConfService;

	@Override
	protected void init(HttpServletRequest request, JSONObject requestParams) {
		setPageOption(
			createOp()
				.keyWordLikeFields(
					BaseSysUserEntityTableDef.BASE_SYS_USER_ENTITY.NAME,
					BaseSysLogEntityTableDef.BASE_SYS_LOG_ENTITY.PARAMS));
	}

	@Operation(summary = "清理日志")
	@PostMapping("/clear")
	public R clear() {
		service.clear(true);
		return R.ok();
	}

	@Operation(summary = "设置日志保存时间")
	@PostMapping("/setKeep")
	public R setKeep(@RequestAttribute JSONObject requestParams) {
		baseSysConfService.updateValue("logKeep", requestParams.getStr("value"));
		return R.ok();
	}

	@Operation(summary = "获得日志报错时间")
	@PostMapping("/getKeep")
	public R getKeep() {
		return R.ok(baseSysConfService.getValue("logKeep"));
	}
}
