package com.cool.modules.task.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.task.entity.TaskLogEntity;
import com.cool.modules.task.mapper.TaskLogMapper;
import com.cool.modules.task.service.TaskInfoLogService;
import org.springframework.stereotype.Service;

@Service
public class TaskInfoLogServiceImpl extends BaseServiceImpl<TaskLogMapper, TaskLogEntity>
        implements TaskInfoLogService {
}
