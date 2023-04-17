package com.dataease.cloud.quartz.base;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 任务的监听器
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:23 AM
 **/
@Slf4j
public final class FixedDelayJobListener extends JobListenerSupport {
    private static final String FIXED_JOB_LISTENER_NAME = "FixedDelayJobListener";
    public static final String FIXED_DELAY_JOB_DATA = "FIXED_DELAY_JOB_DATA";

    public String getName() {
        return FIXED_JOB_LISTENER_NAME;
    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        JobDetail jobdetail = context.getJobDetail();
        JobDataMap data = jobdetail.getJobDataMap();
        if (!data.containsKey(FIXED_DELAY_JOB_DATA)) {
            log.debug("Not a fixed delay job : {}", context.getJobDetail().getKey());
        } else if (this.shouldNotSchedule(context)) {
            log.debug("Not scheduling {} again as there is still an unfired trigger.", context.getJobDetail().getKey());
        } else {
            log.debug("Rescheduling {} as there is no unfired trigger.", context.getJobDetail().getKey());
            TriggerKey oldTriggerKey = context.getTrigger().getKey();
            FixedDelayJobData jobData = (FixedDelayJobData)data.getWrappedMap().get(FIXED_DELAY_JOB_DATA);
            Trigger newTrigger = this.buildNewTrigger(jobData, oldTriggerKey);
            this.rescheduleJob(context.getScheduler(), oldTriggerKey, newTrigger);
        }
    }

    private boolean shouldNotSchedule(JobExecutionContext context) {
        List<? extends Trigger> triggersOfJob = this.getTriggersOfJob(context);

        Iterator iterator = triggersOfJob.iterator();

        Trigger trigger;
        do {
            if (!iterator.hasNext()) {
                return false;
            }
            trigger = (Trigger)iterator.next();
        } while(!(trigger instanceof SimpleTrigger) || ((SimpleTrigger)trigger).getTimesTriggered() != 0);

        return true;
    }

    private List<? extends Trigger> getTriggersOfJob(JobExecutionContext context) {
        Object triggersOfJob = new ArrayList();

        try {
            triggersOfJob = context.getScheduler().getTriggersOfJob(context.getJobDetail().getKey());
        } catch (SchedulerException e) {
            this.getLog().error(e.getMessage());
        }

        return (List)triggersOfJob;
    }

    private Trigger buildNewTrigger(FixedDelayJobData jobData, TriggerKey oldTriggerKey) {
        Date runTime = jobData.getNextScheduleDate();
        Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(oldTriggerKey).startAt(runTime).build();
        return newTrigger;
    }

    public void rescheduleJob(Scheduler scheduler, TriggerKey oldTriggerKey, Trigger newTrigger) {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.rescheduleJob(oldTriggerKey, newTrigger);
            }
        } catch (SchedulerException e) {
            log.error("failed to reschedule the job with triger : {}", oldTriggerKey, e);
        }

    }
}
