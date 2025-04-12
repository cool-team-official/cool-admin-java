package com.cool.core.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.cache.CoolCache;
import com.cool.core.enums.UserTypeEnum;
import com.cool.core.exception.CoolPreconditions;
import com.cool.core.security.jwt.JwtUser;
import com.cool.modules.base.entity.sys.BaseSysUserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Security 工具类
 */
public class CoolSecurityUtil {

    private static final CoolCache coolCache = SpringUtil.getBean(CoolCache.class);

    /***************后台********************/
    /**
     * 获取后台登录的用户名
     */
    public static String getAdminUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 获得jwt中的信息
     *
     * @param requestParams 请求参数
     * @return jwt
     */
    public static JSONObject getAdminUserInfo(JSONObject requestParams) {
        JSONObject tokenInfo = requestParams.getJSONObject("tokenInfo");
        if (tokenInfo != null) {
            tokenInfo.set("department",
                coolCache.get("admin:department:" + tokenInfo.get("userId")));
            tokenInfo.set("roleIds", coolCache.get("admin:roleIds:" + tokenInfo.get("userId")));
        }
        return tokenInfo;
    }

    public static Long getTenantId(JSONObject requestParams) {
        JSONObject tokenInfo = requestParams.getJSONObject("tokenInfo");
        if (tokenInfo != null) {
            return tokenInfo.getLong("tenantId");
        }
        return null;
    }

    /**
     * 后台账号退出登录
     *
     * @param adminUserId 用户ID
     * @param username    用户名
     */
    public static void adminLogout(Long adminUserId, String username) {
        coolCache.del("admin:department:" + adminUserId, "admin:passwordVersion:" + adminUserId,
            "admin:userInfo:" + adminUserId, "admin:userDetails:" + username);
    }

    /**
     * 后台账号退出登录
     *
     * @param userEntity 用户
     */
    public static void adminLogout(BaseSysUserEntity userEntity) {
        adminLogout(userEntity.getId(), userEntity.getUsername());
    }


    /**
     * 获取当前用户id
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((JwtUser) principal).getUserId();
            }
        }
        CoolPreconditions.check(true, 401, "未登录");
        return null;
    }

    /**
     * 获取当前用户类型
     */
    public static UserTypeEnum getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((JwtUser) principal).getUserTypeEnum();
            }
        }
        // 还未登录,未知类型
        return UserTypeEnum.UNKNOWN;
    }

    /**
     * app退出登录,移除缓存信息
     */
    public static void appLogout() {
        coolCache.del("app:userDetails"+ getCurrentUserId());
    }
}
