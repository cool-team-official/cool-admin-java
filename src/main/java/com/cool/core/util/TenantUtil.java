package com.cool.core.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class TenantUtil {
    public static Long getTenantId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return (Long) attributes.getAttribute("tenantId", RequestAttributes.SCOPE_REQUEST);
    }
}
