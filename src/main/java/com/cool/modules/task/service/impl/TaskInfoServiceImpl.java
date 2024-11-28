package com.cool.modules.task.service.impl;

import static com.cool.modules.task.entity.table.TaskInfoEntityTableDef.TASK_INFO_ENTITY;
import static com.cool.modules.task.entity.table.TaskLogEntityTableDef.TASK_LOG_ENTITY;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.task.entity.TaskInfoEntity;
import com.cool.modules.task.entity.TaskLogEntity;
import com.cool.modules.task.mapper.TaskInfoMapper;
import com.cool.modules.task.service.TaskInfoService;
import com.cool.modules.task.utils.ScheduleUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInfoServiceImpl extends BaseServiceImpl<TaskInfoMapper, TaskInfoEntity> implements
    TaskInfoService {

    final private Scheduler scheduler;

    @Override
    public void init() {
        try {
            List<TaskInfoEntity> list = list();
            list.forEach(scheduleJob -> {
                CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler,
                    scheduleJob.getJobId());
                if (cronTrigger == null) {
                    ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
                } else {
                    ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
                }
                updateById(scheduleJob);
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void once(Long taskId) {
        ScheduleUtils.run(scheduler, getById(taskId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stop(Long taskId) {
        ScheduleUtils.pauseJob(scheduler, taskId + "");
        TaskInfoEntity taskInfoEntity = getById(taskId);
        taskInfoEntity.setStatus(0);
        updateById(taskInfoEntity);
        modifyAfter(JSONUtil.parseObj(taskInfoEntity), taskInfoEntity);
    }

    @Override
    public Object log(Page page, Long taskId, Integer status) {

        QueryWrapper queryWrapper = QueryWrapper.create().select(TASK_LOG_ENTITY.DETAIL,
                TASK_LOG_ENTITY.STATUS, TASK_LOG_ENTITY.CREATE_TIME,
                TASK_INFO_ENTITY.NAME).from(TASK_LOG_ENTITY)
            .leftJoin(TASK_INFO_ENTITY).on(TASK_LOG_ENTITY.TASK_ID.eq(TASK_INFO_ENTITY.ID))
            .eq(TaskLogEntity::getTaskId, taskId, taskId != null)
            .eq(TaskLogEntity::getStatus, status, status != null)
            .orderBy(TaskLogEntity::getCreateTime, false);
        return mapper.paginateAs(page, queryWrapper, TaskLogEntity.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long taskId, Integer type) {
        TaskInfoEntity taskInfoEntity = getById(taskId);
        taskInfoEntity.setStatus(1);
        if (type != null) {
            taskInfoEntity.setType(type);
        }
        boolean isExists = false;
        try {
            isExists = scheduler.checkExists(ScheduleUtils.getJobKey(taskId + ""));
        } catch (SchedulerException e) {
            log.error("err", e);
        }
        if (isExists) {
            ScheduleUtils.updateScheduleJob(scheduler, taskInfoEntity);
            ScheduleUtils.resumeJob(scheduler, taskId + "");
        } else {
            ScheduleUtils.createScheduleJob(scheduler, taskInfoEntity);
        }
        updateById(taskInfoEntity);
        modifyAfter(JSONUtil.parseObj(taskInfoEntity), taskInfoEntity);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(JSONObject requestParams, TaskInfoEntity scheduleJob) {
        scheduleJob.setStatus(1);
        super.add(scheduleJob);
        scheduleJob.setJobId(scheduleJob.getId() + "");

        ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
        updateById(scheduleJob);
        super.modifyAfter(requestParams, scheduleJob);
        return scheduleJob.getId();
    }

    @Override
    public boolean update(JSONObject requestParams, TaskInfoEntity entity) {
        updateById(entity);
        ScheduleUtils.deleteScheduleJob(scheduler, entity.getId().toString());
        if (entity.getStatus() == 1) {
            start(entity.getId(), entity.getType());
        } else {
            stop(entity.getId());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(JSONObject requestParams, Long... ids) {
        Convert.toList(String.class, ids).forEach(jobId -> {
            ScheduleUtils.deleteScheduleJob(scheduler, jobId);
        });
        return super.delete(requestParams, ids);
    }
}
