package com.cool.modules.task.utils;

import com.cool.core.exception.CoolException;
import com.cool.modules.task.entity.TaskInfoEntity;
import com.cool.modules.task.run.ScheduleJob;
import org.quartz.*;

/**
 * 定时任务工具类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.2.0 2016-11-28
 */
public class ScheduleUtils {
    private final static String JOB_NAME = "TASK_";

    public enum ScheduleStatus {
        /**
         * 暂停
         */
        PAUSE(0),
        /**
         * 正常
         */
        NORMAL(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 获取触发器key
     */
    public static TriggerKey getTriggerKey(String jobId) {
        return TriggerKey.triggerKey(JOB_NAME + jobId);
    }

    /**
     * 获取jobKey
     */
    public static JobKey getJobKey(String jobId) {
        return JobKey.jobKey(JOB_NAME + jobId);
    }

    /**
     * 获取表达式触发器
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, String jobId) {
        try {
            return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new CoolException("获取定时任务CronTrigger出现异常", e);
        }
    }

    /**
     * 获取表达式触发器
     */
    public static SimpleTrigger getSimpleTrigger(Scheduler scheduler, String jobId) {
        try {
            return (SimpleTrigger) scheduler.getTrigger(getTriggerKey(jobId));
        } catch (SchedulerException e) {
            throw new CoolException("获取定时任务CronTrigger出现异常", e);
        }
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, TaskInfoEntity scheduleJob) {
        try {
            // 构建job信息
            JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getJobId()))
                    .build();

            if (scheduleJob.getTaskType() == 0) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
                        .withMisfireHandlingInstructionDoNothing();

                TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey(scheduleJob.getJobId())).withSchedule(scheduleBuilder);

                if (scheduleJob.getStartDate() != null) {
                    triggerBuilder.startAt(scheduleJob.getStartDate());
                }

                if (scheduleJob.getEndDate() != null) {
                    triggerBuilder.endAt(scheduleJob.getEndDate());
                }

                // 按新的cronExpression表达式构建一个新的trigger
                CronTrigger trigger = triggerBuilder.build();

                scheduler.scheduleJob(jobDetail, trigger);
                scheduleJob.setNextRunTime(trigger.getNextFireTime());
            }

            if (scheduleJob.getTaskType() == 1) {
                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(scheduleJob.getEvery() / 1000);
                if (scheduleJob.getRepeatCount() != null) {
                    scheduleBuilder.withRepeatCount(scheduleJob.getRepeatCount());
                } else {
                    scheduleBuilder.repeatForever();
                }
                TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(getTriggerKey(scheduleJob.getJobId())).withSchedule(scheduleBuilder);
                if (scheduleJob.getStartDate() != null) {
                    triggerBuilder.startAt(scheduleJob.getStartDate());
                }

                if (scheduleJob.getEndDate() != null) {
                    triggerBuilder.endAt(scheduleJob.getEndDate());
                }
                Trigger trigger = triggerBuilder.build();

                scheduler.scheduleJob(jobDetail, trigger);
                scheduleJob.setNextRunTime(trigger.getNextFireTime());
            }

            // 暂停任务
            if (scheduleJob.getStatus() != null && scheduleJob.getStatus() == ScheduleStatus.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getJobId());
            }
        } catch (SchedulerException e) {
            throw new CoolException("创建定时任务失败", e);
        }
    }

    /**
     * 更新定时任务
     */
    public static void updateScheduleJob(Scheduler scheduler, TaskInfoEntity scheduleJob) {
        try {
            TriggerKey triggerKey = getTriggerKey(scheduleJob.getJobId());

            if (scheduleJob.getTaskType() == 0) {
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCron())
                        .withMisfireHandlingInstructionDoNothing();

                CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getJobId());

                TriggerBuilder<CronTrigger> triggerBuilder = trigger.getTriggerBuilder();

                if (scheduleJob.getStartDate() != null) {
                    triggerBuilder.startAt(scheduleJob.getStartDate());
                }

                if (scheduleJob.getEndDate() != null) {
                    triggerBuilder.endAt(scheduleJob.getEndDate());
                }

                // 按新的cronExpression表达式重新构建trigger
                trigger = triggerBuilder.withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(triggerKey, trigger);
                scheduleJob.setNextRunTime(trigger.getNextFireTime());

            }

            if (scheduleJob.getTaskType() == 1) {
                SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(scheduleJob.getEvery() / 1000);

                SimpleTrigger trigger = getSimpleTrigger(scheduler, scheduleJob.getJobId());

                if (scheduleJob.getRepeatCount() != null) {
                    scheduleBuilder.withRepeatCount(scheduleJob.getRepeatCount());
                } else {
                    scheduleBuilder.repeatForever();
                }
                TriggerBuilder<SimpleTrigger> triggerBuilder = trigger.getTriggerBuilder();
                if (scheduleJob.getStartDate() != null) {
                    triggerBuilder.startAt(scheduleJob.getStartDate());
                }

                if (scheduleJob.getEndDate() != null) {
                    triggerBuilder.endAt(scheduleJob.getEndDate());
                }
                trigger = triggerBuilder.withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

                scheduler.rescheduleJob(triggerKey, trigger);
                scheduleJob.setNextRunTime(trigger.getNextFireTime());
            }

            // 暂停任务
            if (scheduleJob.getStatus() == ScheduleStatus.PAUSE.getValue()) {
                pauseJob(scheduler, scheduleJob.getJobId());
            }

        } catch (SchedulerException e) {
            throw new CoolException("更新定时任务失败", e);
        }
    }

    /**
     * 立即执行任务
     */
    public static void run(Scheduler scheduler, TaskInfoEntity scheduleJob) {
        try {
            // 参数
            JobDataMap dataMap = new JobDataMap();

            scheduler.triggerJob(getJobKey(scheduleJob.getJobId()), dataMap);
        } catch (SchedulerException e) {
            throw new CoolException("立即执行定时任务失败", e);
        }
    }

    /**
     * 暂停任务
     */
    public static void pauseJob(Scheduler scheduler, String jobId) {
        try {
            scheduler.pauseJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new CoolException("暂停定时任务失败", e);
        }
    }

    /**
     * 恢复任务
     */
    public static void resumeJob(Scheduler scheduler, String jobId) {
        try {
            scheduler.resumeJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new CoolException("恢复定时任务失败", e);
        }
    }

    /**
     * 删除定时任务
     */
    public static void deleteScheduleJob(Scheduler scheduler, String jobId) {
        try {
            scheduler.deleteJob(getJobKey(jobId));
        } catch (SchedulerException e) {
            throw new CoolException("删除定时任务失败", e);
        }
    }
}
