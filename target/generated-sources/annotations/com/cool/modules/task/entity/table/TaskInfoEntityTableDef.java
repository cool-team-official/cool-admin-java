package com.cool.modules.task.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

// Auto generate by mybatis-flex, do not modify it.
public class TaskInfoEntityTableDef extends TableDef {

    public static final TaskInfoEntityTableDef TASK_INFO_ENTITY = new TaskInfoEntityTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn CRON = new QueryColumn(this, "cron");

    public final QueryColumn DATA = new QueryColumn(this, "data");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public final QueryColumn TYPE = new QueryColumn(this, "type");

    public final QueryColumn EVERY = new QueryColumn(this, "every");

    public final QueryColumn JOB_ID = new QueryColumn(this, "job_id");

    public final QueryColumn REMARK = new QueryColumn(this, "remark");

    public final QueryColumn STATUS = new QueryColumn(this, "status");

    public final QueryColumn END_DATE = new QueryColumn(this, "end_date");

    public final QueryColumn SERVICE = new QueryColumn(this, "service");

    public final QueryColumn TASK_TYPE = new QueryColumn(this, "task_type");

    public final QueryColumn START_DATE = new QueryColumn(this, "start_date");

    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    public final QueryColumn NEXT_RUN_TIME = new QueryColumn(this, "next_run_time");

    public final QueryColumn REPEAT_COUNT = new QueryColumn(this, "repeat_count");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CRON, DATA, NAME, TYPE, EVERY, JOB_ID, REMARK, STATUS, END_DATE, SERVICE, TASK_TYPE, START_DATE, CREATE_TIME, UPDATE_TIME, NEXT_RUN_TIME, REPEAT_COUNT};

    public TaskInfoEntityTableDef() {
        super("", "task_info");
    }

    private TaskInfoEntityTableDef(String schema, String name, String alisa) {
        super(schema, name, alisa);
    }

    public TaskInfoEntityTableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new TaskInfoEntityTableDef("", "task_info", alias));
    }

}
