package com.cool.modules.base.filter;

import cn.hutool.json.JSONObject;
import com.cool.modules.base.service.sys.BaseSysLogService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
@RequiredArgsConstructor
public class BaseLogFilter implements Filter {

    final private BaseSysLogService baseSysLogService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain)
        throws IOException, ServletException {
        // 记录日志
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        baseSysLogService.record(request, (JSONObject) request.getAttribute("requestParams"));
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
