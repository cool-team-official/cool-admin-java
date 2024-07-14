package com.cool.core.request;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.cool.core.util.BodyReaderHttpServletRequestWrapper;
import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装请求参数 URL参数 和 body JSON 到同一个 JSONObject 方便读取
 */
@Component
@Order(2)
public class RequestParamsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        // 防止流读取一次后就没有了, 所以需要将流继续写出去
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        JSONObject requestParams = new JSONObject();
        if (StrUtil.isNotEmpty(request.getContentType()) && request.getContentType().contains("multipart/form-data")) {
            servletRequest.setAttribute("requestParams", requestParams);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
            String body = requestWrapper.getBodyString(requestWrapper);
            if (StrUtil.isNotEmpty(body) && JSONUtil.isJson(body) && !JSONUtil.isJsonArray(body)) {
                requestParams = JSONUtil.parseObj(body);
            }
            requestParams.set("body", body);
            requestParams.putAll(getAllRequestParam(request));

            Object jwtObj = request.getAttribute("tokenInfo");
            if (jwtObj != null) {
                requestParams.set("tokenInfo", ((JWT) jwtObj).getPayload().getClaimsJson());
            }
            requestWrapper.setAttribute("requestParams", requestParams);

            filterChain.doFilter(requestWrapper, servletResponse);
        }
    }

    /**
     * 获取客户端请求参数中所有的信息
     *
     * @param request
     * @return
     */
    private Map<String, Object> getAllRequestParam(final HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 如果字段的值为空，判断若值为空，则删除这个字段>
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
