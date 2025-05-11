package com.cool.modules.task.run;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cool.core.util.AutoTypeConverter;
import com.cool.modules.task.entity.TaskInfoEntity;
import com.cool.modules.task.entity.TaskLogEntity;
import com.cool.modules.task.service.TaskInfoLogService;
import com.cool.modules.task.service.TaskInfoService;
import com.mybatisflex.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 定时任务
 */
@Slf4j
public class ScheduleJob extends QuartzJobBean {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void executeInternal(JobExecutionContext context) {
        // 获取spring bean
        TaskInfoLogService taskInfoLogService = SpringUtil.getBean(TaskInfoLogService.class);
        // 获取spring bean
        TaskInfoService taskInfoService = SpringUtil.getBean(TaskInfoService.class);

        Scheduler scheduler = SpringUtil.getBean(Scheduler.class);

        TaskInfoEntity taskInfoEntity = taskInfoService
                .getById(AutoTypeConverter.autoConvert(context.getJobDetail().getKey().getName().split("_")[1]));
        if (ObjUtil.isEmpty(taskInfoEntity)) {
            log.warn("taskInfoEntity is null");
            return;
        }

        // 数据库保存执行记录
        TaskLogEntity taskLogEntity = new TaskLogEntity();

        // 任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 执行任务
            log.info("任务准备执行，任务ID：" + taskInfoEntity.getJobId());
            taskLogEntity.setTaskId(taskInfoEntity.getId());
            // 解析执行
            String service = taskInfoEntity.getService();
            if (StrUtil.isNotEmpty(service)) {
                String[] arr = service.split("\\.");
                String methodName = arr[1].substring(0, arr[1].indexOf("("));
                String params = service.substring(service.indexOf("(") + 1, service.indexOf(")"));

                ScheduleRunnable task = new ScheduleRunnable(StringUtil.firstCharToLowerCase(arr[0]).replaceAll(" ", ""), methodName, params);
                Future<?> future = executorService.submit(task);

                future.get();
            }
            // 任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            // 状态 0：失败 1：成功
            taskLogEntity.setStatus(1);
            taskLogEntity.setDetail("任务执行完毕，任务ID：" + taskInfoEntity.getJobId() + "  总共耗时：" + times + "毫秒");
            log.info(taskLogEntity.getDetail());
        } catch (Exception e) {
            // 任务执行总时长
            long times = System.currentTimeMillis() - startTime;

            taskLogEntity.setDetail(
                    "任务执行失败，任务ID：" + taskInfoEntity.getJobId() + "  总共耗时：" + times + "毫秒" + "失败原因：" + e.getMessage());
            log.error("任务执行失败，任务ID：" + taskInfoEntity.getJobId(), e);

            // 状态 0：失败 1：成功
            taskLogEntity.setStatus(0);
        } finally {
            taskInfoLogService.add(taskLogEntity);
        }
        ThreadUtil.execAsync(() -> {
            ThreadUtil.sleep(2000);
            TaskInfoEntity next = new TaskInfoEntity();
            next.setId(taskInfoEntity.getId());
            try {
                if (!scheduler.checkExists(context.getTrigger().getJobKey())) {
                    if (context.getTrigger().getNextFireTime() == null) {
                        next.setStatus(0);
                    }
                } else {
                    if (context.getTrigger().getNextFireTime() == null) {
                        next.setNextRunTime(context.getTrigger().getNextFireTime());
                    }
                }
            } catch (SchedulerException e) {
                log.error("err", e);
            }
            taskInfoService.updateById(next);
        });
    }
}
