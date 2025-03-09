package com.cool.modules.task.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import lombok.Getter;
import lombok.Setter;
import org.dromara.autotable.annotation.Index;

@Getter
@Setter
@Table(value = "task_log", comment = "任务日志")
public class TaskLogEntity extends BaseEntity<TaskLogEntity> {

    @Index
    @ColumnDefine(comment = "任务ID", notNull = true, type = "bigint")
    private Long taskId;

    @ColumnDefine(comment = "状态 0：失败 1：成功", defaultValue = "0")
    private Integer status;

    @ColumnDefine(comment = "详情", type = "text")
    private String detail;

    // 任务名称
    @Column(ignore = true)
    private String taskName;
}
