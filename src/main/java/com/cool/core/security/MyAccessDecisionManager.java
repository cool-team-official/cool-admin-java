package com.cool.core.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

/**
 * 权限管理决断器 判断用户拥有的权限或角色是否有资源访问权限
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class MyAccessDecisionManager implements AccessDecisionManager {
    // 忽略权限控制的地址
    final private IgnoredUrlsProperties ignoredUrlsProperties;

    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (configAttributes == null) {
            return;
        }
        List<String> urls = ignoredUrlsProperties.getAdminAuthUrls();
        String url = ((FilterInvocation) o).getRequestUrl().split("[?]")[0];
        if (urls.contains(url)) {
            return;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute c = iterator.next();
            String needPerm = c.getAttribute();
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                // 匹配用户拥有的ga 和 系统中的needPerm
                if (needPerm.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("抱歉，您没有访问权限");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
