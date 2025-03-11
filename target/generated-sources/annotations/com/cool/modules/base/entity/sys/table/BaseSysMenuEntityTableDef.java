package com.cool.modules.base.entity.sys.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class BaseSysMenuEntityTableDef extends TableDef {

    public static final BaseSysMenuEntityTableDef BASE_SYS_MENU_ENTITY = new BaseSysMenuEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn ICON = new QueryColumn(this, "icon");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn TYPE = new QueryColumn(this, "type");

    public final QueryColumn PERMS = new QueryColumn(this, "perms");

    public final QueryColumn IS_SHOW = new QueryColumn(this, "is_show");

    public final QueryColumn ROUTER = new QueryColumn(this, "router");

    public final QueryColumn ORDER_NUM = new QueryColumn(this, "order_num");

    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    public final QueryColumn VIEW_PATH = new QueryColumn(this, "view_path");

    public final QueryColumn KEEP_ALIVE = new QueryColumn(this, "keep_alive");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ICON, NAME, TYPE, PERMS, IS_SHOW, ROUTER, ORDER_NUM, PARENT_ID, VIEW_PATH, KEEP_ALIVE, CREATE_TIME, UPDATE_TIME};

    public BaseSysMenuEntityTableDef() {
        super("", "base_sys_menu");
    }

    private BaseSysMenuEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public BaseSysMenuEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new BaseSysMenuEntityTableDef("", "base_sys_menu", alias));
    }

}
