package com.cool.modules.task.entity;

import com.cool.core.base.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.tangzc.mybatisflex.autotable.annotation.ColumnDefine;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(value = "task_info", comment = "任务信息")
public class TaskInfoEntity extends BaseEntity<TaskInfoEntity> {
    /**
     * 任务调度参数key
     */
    @Column(ignore = true)
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    @ColumnDefine(comment = "名称", notNull = true)
    private String name;

    @ColumnDefine(comment = "任务ID")
    private String jobId;

    @ColumnDefine(comment = "最大执行次数 不传为无限次")
    private Integer repeatCount;

    @ColumnDefine(comment = "每间隔多少毫秒执行一次 如果cron设置了 这项设置就无效")
    private Integer every;

    @ColumnDefine(comment = "状态 0:停止 1：运行", defaultValue = "1", notNull = true)
    private Integer status;

    @ColumnDefine(comment = "服务实例名称")
    private String service;

    @ColumnDefine(comment = "状态 0:cron 1：时间间隔", defaultValue = "0")
    private Integer taskType;

    @ColumnDefine(comment = "状态 0:系统 1：用户", defaultValue = "0")
    private Integer type;

    @ColumnDefine(comment = "任务数据")
    private String data;

    @ColumnDefine(comment = "备注")
    private String remark;

    @ColumnDefine(comment = "cron")
    private String cron;

    @ColumnDefine(comment = "下一次执行时间")
    private Date nextRunTime;

    @ColumnDefine(comment = "开始时间")
    private Date startDate;

    @ColumnDefine(comment = "结束时间")
    private Date endDate;
}
