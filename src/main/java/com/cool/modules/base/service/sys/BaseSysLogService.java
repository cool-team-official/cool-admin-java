package com.cool.modules.base.service.sys;

import cn.hutool.json.JSONObject;
import com.cool.core.base.BaseService;
import com.cool.modules.base.entity.sys.BaseSysLogEntity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 系统日志
 */
public interface BaseSysLogService extends BaseService<BaseSysLogEntity> {
    /**
     * 清理日志
     *
     * @param isAll 是否全部清除
     */
    void clear(boolean isAll);

    /**
     * 日志记录
     *
     * @param requestParams 请求参数
     * @param request       请求
     */
    void record(HttpServletRequest request, JSONObject requestParams);
}
