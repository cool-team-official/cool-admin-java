package com.cool.modules.base.service.sys.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.core.config.LogProperties;
import com.cool.core.security.IgnoredUrlsProperties;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.core.util.IPUtils;
import com.cool.core.util.PathUtils;
import com.cool.modules.base.entity.sys.BaseSysLogEntity;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import com.cool.modules.base.entity.sys.table.BaseSysLogEntityTableDef;
import com.cool.modules.base.entity.sys.table.BaseSysUserEntityTableDef;
import com.cool.modules.base.mapper.sys.BaseSysLogMapper;
import com.cool.modules.base.service.sys.BaseSysConfService;
import com.cool.modules.base.service.sys.BaseSysLogService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统日志
 */
@RequiredArgsConstructor
@Service
public class BaseSysLogServiceImpl extends BaseServiceImpl<BaseSysLogMapper, BaseSysLogEntity>
	implements BaseSysLogService {

	private final BaseSysConfService baseSysConfService;

	private final IgnoredUrlsProperties ignoredUrlsProperties;

	private final IPUtils ipUtils;

	private final LogProperties logProperties;

	private final Executor logTaskExecutor;

	@Override
	public Object page(
		JSONObject requestParams, Page<BaseSysLogEntity> page, QueryWrapper queryWrapper) {
		queryWrapper
			.select(
				BaseSysLogEntityTableDef.BASE_SYS_LOG_ENTITY.ALL_COLUMNS,
				BaseSysUserEntityTableDef.BASE_SYS_USER_ENTITY.NAME)
			.from(BaseSysLogEntityTableDef.BASE_SYS_LOG_ENTITY)
			.leftJoin(BaseSysUserEntityTableDef.BASE_SYS_USER_ENTITY)
			.on(BaseSysLogEntity::getUserId, BaseSysUserEntity::getId);
		return mapper.paginate(page, queryWrapper);
	}

	@Override
	public void clear(boolean isAll) {
		if (isAll) {
			this.remove(QueryWrapper.create().ge(BaseSysLogEntity::getId, 0));
		} else {
			String keepDay = baseSysConfService.getValue("logKeep");
			int keepDays = Integer.parseInt(StrUtil.isNotEmpty(keepDay) ? keepDay : "30");
			Date beforeDate = DateUtil.offsetDay(new Date(), -keepDays);
			this.remove(QueryWrapper.create().lt(BaseSysLogEntity::getCreateTime, beforeDate));
		}
	}

	@Override
	public void record(HttpServletRequest request, JSONObject requestParams) {
		String requestURI = request.getRequestURI();
		if (isIgnoreUrl(requestURI)) {
			// 配置了忽略记录请求日志
			return;
		}
		String ipAddr = ipUtils.getIpAddr(request);
		// 异步记录日志
		recordAsync(ipAddr, requestURI, requestParams);
	}

	private boolean isIgnoreUrl(String requestURI) {
		return PathUtils.isMatch(ignoredUrlsProperties.getLogUrls(), requestURI);
	}

	public void recordAsync(String ipAddr, String requestURI, JSONObject requestParams) {
		logTaskExecutor.execute(() -> {
			JSONObject userInfo = CoolSecurityUtil.getAdminUserInfo(requestParams);

			Long userId = null;
			if (userInfo != null) {
				userId = userInfo.getLong("userId");
			}

			JSONObject newJSONObject = JSONUtil.parseObj(JSONUtil.toJsonStr(requestParams));
			newJSONObject.remove("tokenInfo");
			newJSONObject.remove("refreshToken");
			newJSONObject.remove("body");
			if (newJSONObject.toString().getBytes().length > logProperties.getMaxByteLength()) {
				// 超过指定
				newJSONObject.clear();
			}
			BaseSysLogEntity logEntity = new BaseSysLogEntity();
			logEntity.setAction(requestURI);
			logEntity.setIp(ipAddr);
			if (userId != null) {
				logEntity.setUserId(userId);
			}
			logEntity.setParams(newJSONObject);
			save(logEntity);

		});
	}
}
