package com.cool.modules.base.security;

import cn.hutool.core.util.ObjectUtil;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.base.service.sys.BaseSysPermsService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

/**
 * 权限资源管理器 为权限决断器提供支持
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MySecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    final private BaseSysPermsService baseSysPermsService;

    private Map<String, Collection<ConfigAttribute>> map = null;

    /**
     * 加载权限表中所有操作请求权限
     */
    public void loadResourceDefine() {
        map = new HashMap<>();
        Collection<ConfigAttribute> configAttributes;
        ConfigAttribute cfg;
        String[] perms = baseSysPermsService.getAllPerms();
        // 获取启用的权限操作请求
        for (String perm : perms) {
            configAttributes = new ArrayList<>();
            cfg = new SecurityConfig(perm);
            // 作为MyAccessDecisionManager类的decide的第三个参数
            configAttributes.add(cfg);
            // 用权限的path作为map的key，用ConfigAttribute的集合作为value
            map.put(perm.replaceAll(":", "/"), configAttributes);
        }
    }

    /**
     * 判定用户请求的url是否在权限表中 如果在权限表中，则返回给decide方法，用来判定用户是否有此权限 如果不在权限表中则放行
     *
     * @param o
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        UserTypeEnum userTypeEnum = CoolSecurityUtil.getCurrentUserType();
        if (ObjectUtil.equal(userTypeEnum, UserTypeEnum.APP)) {
            // app用户不需要权限拦截
            return null;
        }
        if (map == null) {
            loadResourceDefine();
        }
        // Object中包含用户请求request
        String url = ((FilterInvocation) o).getRequestUrl();
        return map.get(url.replace("/admin/", "").split("[?]")[0]);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return new ArrayList<>();
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
