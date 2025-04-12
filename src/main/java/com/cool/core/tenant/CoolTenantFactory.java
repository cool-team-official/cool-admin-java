package com.cool.core.tenant;

import com.cool.core.util.TenantUtil;
import com.mybatisflex.core.tenant.TenantFactory;

public class CoolTenantFactory implements TenantFactory {
    public Object[] getTenantIds(){
        Long tenantId = TenantUtil.getTenantId();
        if (tenantId == null) {
            return null;
        }
        return new Object[]{tenantId};
    }
}
