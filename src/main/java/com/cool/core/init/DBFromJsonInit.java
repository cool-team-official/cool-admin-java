package com.cool.core.init;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.base.service.MapperProviderService;
import com.cool.core.mybatis.pg.PostgresSequenceSyncService;
import com.cool.core.util.DatabaseDialectUtils;
import com.cool.core.util.EntityUtils;
import com.cool.modules.base.entity.sys.BaseSysConfEntity;
import com.cool.modules.base.entity.sys.BaseSysMenuEntity;
import com.cool.modules.base.service.sys.BaseSysConfService;
import com.cool.modules.base.service.sys.BaseSysMenuService;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * 数据库初始数据初始化 在 classpath:cool/data/db 目录下创建.json文件 并定义表数据， 由该类统一执行初始化
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class DBFromJsonInit {

    final private BaseSysConfService baseSysConfService;

    final private BaseSysMenuService baseSysMenuService;

    final private MapperProviderService mapperProviderService;

    final private ApplicationEventPublisher eventPublisher;

    final private PostgresSequenceSyncService postgresSequenceSyncService;

    @Value("${cool.initData}")
    private boolean initData;

    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        if (!initData) {
            return;
        }
        // 初始化自定义的数据
        boolean initFlag = extractedDb();
        // 初始化菜单数据
        initFlag = extractedMenu() || initFlag;
        // 发送数据库初始化完成事件
        eventPublisher.publishEvent(new DbInitCompleteEvent(this));
        if (initFlag) {
            // 如果是postgresql，同步序列
            syncIdentitySequences();
        }
        log.info("数据初始化完成！");
    }

    private void syncIdentitySequences() {
        if (DatabaseDialectUtils.isPostgresql()) {
            postgresSequenceSyncService.syncIdentitySequences();
        }
    }

    @Getter
    public static class DbInitCompleteEvent {
        private final Object source;

        public DbInitCompleteEvent(Object source) {
            this.source = source;
        }

    }

    /**
     * 解析插入业务数据
     */
    private boolean extractedDb() {
        try {
            // 加载 JSON 文件
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:cool/data/db/*.json");
            // 遍历所有.json文件
            return analysisResources(resources);
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
        }
        return false;
    }

    private boolean analysisResources(Resource[] resources)
        throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String prefix = "db_";
        boolean isInit = false;
        for (Resource resource : resources) {
            File resourceFile = new File(resource.getURL().getFile());
            String fileName = prefix + resourceFile.getName();
            String value = baseSysConfService.getValue(fileName);
            if (StrUtil.isNotEmpty(value)) {
                log.info("{} 业务数据已初始化过...", fileName);
                continue;
            }
            String jsonStr = IoUtil.read(resource.getInputStream(), StandardCharsets.UTF_8);
            JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
            // 遍历 JSON 文件中的数据
            analysisJson(jsonObject);

            BaseSysConfEntity baseSysUserEntity = new BaseSysConfEntity();
            baseSysUserEntity.setCKey(fileName);
            baseSysUserEntity.setCValue("success");
            // 当前文件已加载
            baseSysConfService.add(baseSysUserEntity);
            isInit = true;
            log.info("{} 业务数据初始化成功...", fileName);
        }
        return isInit;
    }

    private void analysisJson(JSONObject jsonObject)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, Class<?>> tableMap = EntityUtils.findTableMap();
        for (String tableName : jsonObject.keySet()) {
            JSONArray records = jsonObject.getJSONArray(tableName);
            // 根据表名生成实体类名和 Mapper 接口名
            Class<?> entityClass = tableMap.get(tableName);
            BaseMapper<?> baseMapper = mapperProviderService.getMapperByEntityClass(entityClass);
            // 插入
            insertList(baseMapper, entityClass, records);
        }
    }

    /**
     * 插入列表数据
     */
    private void insertList(BaseMapper baseMapper, Class<?> entityClass,
        JSONArray records)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 插入数据
        for (int i = 0; i < records.size(); i++) {
            JSONObject record = records.getJSONObject(i);
            Object entity = JSONUtil.toBean(record, entityClass);
            Method getIdMethod = entityClass.getMethod("getId");
            Object id = getIdMethod.invoke(entity);
            if (ObjUtil.isNotEmpty(id) && ObjUtil.isNotEmpty(
                baseMapper.selectOneById((Long) id))) {
                // 数据库已经有值了
                continue;
            }
            if (ObjUtil.isNotEmpty(id)) {
                // 带id插入
                baseMapper.insertSelectiveWithPk(entity);
            } else {
                baseMapper.insert(entity);
            }
        }
    }

    /**
     * 解析插入菜单数据
     */
    public boolean extractedMenu() {
        boolean initFlag = false;
        try {
            String prefix = "menu_";
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:cool/data/menu/*.json");
            // 遍历所有.json文件
            for (Resource resource : resources) {
                File resourceFile = new File(resource.getURL().getFile());
                String fileName = prefix + resourceFile.getName();
                String value = baseSysConfService.getValue(fileName);
                if (StrUtil.isNotEmpty(value)) {
                    log.info("{} 菜单数据已初始化过...", fileName);
                    continue;
                }
                analysisResources(resource, fileName);
                initFlag = true;
            }
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
        }
        return initFlag;
    }

    private void analysisResources(Resource resource, String fileName) throws IOException {
        String jsonStr = IoUtil.read(resource.getInputStream(), StandardCharsets.UTF_8);

        // 使用 解析 JSON 字符串
        JSONArray jsonArray = JSONUtil.parseArray(jsonStr);

        // 遍历 JSON 数组
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            // 将 JSON 对象转换为 Menu 对象
            parseMenu(jsonObj, null);
        }
        BaseSysConfEntity baseSysUserEntity = new BaseSysConfEntity();
        baseSysUserEntity.setCKey(fileName);
        baseSysUserEntity.setCValue("success");
        // 当前文件已加载
        baseSysConfService.add(baseSysUserEntity);
        log.info("{} 菜单数据初始化成功...", fileName);
    }

    // 递归解析 JSON 对象为 Menu 对象
    private void parseMenu(JSONObject jsonObj, BaseSysMenuEntity parentMenuEntity) {
        BaseSysMenuEntity menuEntity = BeanUtil.copyProperties(jsonObj, BaseSysMenuEntity.class);
        if (ObjUtil.isNotEmpty(parentMenuEntity)) {
            menuEntity.setParentName(parentMenuEntity.getName());
            menuEntity.setParentId(parentMenuEntity.getId());
        }
        QueryWrapper queryWrapper = QueryWrapper.create()
            .eq(BaseSysMenuEntity::getName, menuEntity.getName());
        if (ObjUtil.isNull(menuEntity.getParentId())) {
            queryWrapper.isNull(BaseSysMenuEntity::getParentId);
        } else {
            queryWrapper.eq(BaseSysMenuEntity::getParentId, menuEntity.getParentId());
        }
        BaseSysMenuEntity dbBaseSysMenuEntity = baseSysMenuService.getOne(queryWrapper);
        if (ObjUtil.isNull(dbBaseSysMenuEntity)) {
            baseSysMenuService.add(menuEntity);
        } else {
            menuEntity = dbBaseSysMenuEntity;
        }
        // 递归处理子菜单
        JSONArray childMenus = jsonObj.getJSONArray("childMenus");
        if (childMenus != null) {
            for (Object obj : childMenus) {
                JSONObject childObj = (JSONObject) obj;
                parseMenu(childObj, menuEntity);
            }
        }
    }
}
