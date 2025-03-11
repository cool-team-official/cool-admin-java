package com.cool.modules.plugin.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class PluginInfoEntityTableDef extends TableDef {

    public static final PluginInfoEntityTableDef PLUGIN_INFO_ENTITY = new PluginInfoEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn KEY = new QueryColumn(this, "key");

    public final QueryColumn HOOK = new QueryColumn(this, "hook");

    public final QueryColumn LOGO = new QueryColumn(this, "logo");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn AUTHOR = new QueryColumn(this, "author");

    public final QueryColumn CONFIG = new QueryColumn(this, "config");

    public final QueryColumn README = new QueryColumn(this, "readme");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn VERSION = new QueryColumn(this, "version");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn PLUGIN_JSON = new QueryColumn(this, "plugin_json");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn DESCRIPTION = new QueryColumn(this, "description");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, KEY, HOOK, LOGO, NAME, AUTHOR, CONFIG, README, STATUS, VERSION, CREATE_TIME, PLUGIN_JSON, UPDATE_TIME, DESCRIPTION};

    public PluginInfoEntityTableDef() {
        super("", "plugin_info");
    }

    private PluginInfoEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public PluginInfoEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new PluginInfoEntityTableDef("", "plugin_info", alias));
    }

}
