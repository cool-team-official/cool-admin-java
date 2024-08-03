package com.cool.modules.recycle.aop;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONObject;
import com.cool.core.annotation.IgnoreRecycleData;
import com.cool.core.base.BaseController;
import com.cool.core.base.BaseService;
import com.cool.core.util.CoolSecurityUtil;
import com.cool.modules.recycle.entity.RecycleDataEntity;
import com.cool.modules.recycle.service.RecycleDataService;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据删除前拦截
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteAspect {

    final private RecycleDataService recycleDataService;

    @Around(value = "execution(* com.cool.core.base.BaseController.delete*(..)) && args(request, params, requestParams)", argNames = "joinPoint,request,params,requestParams")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint, HttpServletRequest request,
        Map<String, Object> params,
        JSONObject requestParams) throws Throwable {
        Method currentMethod = getCurrentMethod(joinPoint);
        if (Objects.nonNull(currentMethod) && currentMethod.isAnnotationPresent(
            IgnoreRecycleData.class)) {
            // 忽略回收站记录
            return joinPoint.proceed();
        }
        List<Object> list = null;
        String className = null;
        try {
            log.info("数据删除前拦截");
            // 可以在目标方法执行前进行一些操作
            BaseController baseController = (BaseController) joinPoint.getTarget();
            BaseService service = baseController.getService();
            className = (baseController.currentEntityClass()).getName();
            QueryWrapper queryWrapper = new QueryWrapper();
            Object ids = params.get("ids");
            if (!(ids instanceof ArrayList)) {
                ids = ids.toString().split(",");
            }
            List<Long> idList = Convert.toList(Long.class, ids);
            queryWrapper.in("id", (Object) Convert.toLongArray(idList));
            list = service.list(queryWrapper);
        } catch (Exception e) {
            log.error("数据删除前拦截获取数据详情信息失败", e);
        }

        Object result = joinPoint.proceed();
        if (ObjUtil.isNotEmpty(list)) {
            RecycleDataEntity recycleDataEntity = new RecycleDataEntity();
            recycleDataEntity.setUrl(request.getRequestURI());
            recycleDataEntity.setUserName(CoolSecurityUtil.getAdminUsername());
            recycleDataEntity
                .setUserId(Long.parseLong(
                    String.valueOf(CoolSecurityUtil.getAdminUserInfo(requestParams).get("userId"))));
            recycleDataEntity.setParams(params);
            recycleDataEntity.setData(list);
            recycleDataEntity.setParams(params);
            RecycleDataEntity.EntityInfo entityInfo = new RecycleDataEntity.EntityInfo();
            entityInfo.setEntityClassName(className);
            recycleDataEntity.setEntityInfo(entityInfo);
            recycleDataEntity.setCount(recycleDataEntity.getData().size());

            log.info("数据进入回收站 {}", recycleDataService.add(recycleDataEntity));
        }
        return result;
    }

    private Method getCurrentMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        Method[] methods = joinPoint.getTarget().getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                boolean isSameMethod = true;
                Class<?>[] parameterTypes = method.getParameterTypes();
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                        isSameMethod = false;
                        break;
                    }
                }
                if (isSameMethod) {
                    return method;
                }
            }
        }
        return null;
    }

}