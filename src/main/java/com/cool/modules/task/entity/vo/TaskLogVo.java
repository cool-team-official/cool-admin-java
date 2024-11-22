package com.cool.modules.task.entity.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskLogVo {

    private Long taskId;

    private Integer status;

    private String detail;

    private Date createTime;

    private String taskName;

}
