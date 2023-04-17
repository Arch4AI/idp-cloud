package com.dataease.cloud.quartz.service;

import com.dataease.cloud.quartz.base.ClusterQuartzJobBean;
import com.dataease.cloud.quartz.base.FixedDelayJobListener;
import com.dataease.cloud.quartz.entity.JobDetailTrigger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * quartz管理服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 10:51 AM
 **/
@ConditionalOnProperty(prefix = "cloud.quartz", value = "enabled", havingValue = "true")
@Component
public class QuartzManageService {

    @Autowired
    private Scheduler scheduler;

    public QuartzManageService() {
    }

    public List<JobExecutionContext> getCurrentlyExecutingJobs() throws Exception {
        return this.scheduler.getCurrentlyExecutingJobs();
    }

    public void deleteJob(JobKey jobKey) throws Exception {
        this.scheduler.deleteJob(jobKey);
    }

    public void pauseJob(JobKey jobKey) throws Exception {
        this.scheduler.pauseJob(jobKey);
    }

    public void resumeJob(JobKey jobKey) throws Exception {
        this.scheduler.resumeJob(jobKey);
    }

    public void rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws Exception {
        this.scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    public boolean checkExists(TriggerKey triggerKey) throws Exception {
        return this.scheduler.checkExists(triggerKey);
    }

    public boolean checkExists(JobKey jobKey) throws Exception {
        return this.scheduler.checkExists(jobKey);
    }

    public JobKey getJobKey(TriggerKey triggerKey) throws Exception {
        return this.scheduler.getTrigger(triggerKey).getJobKey();
    }

    public void addJob(JobDetail jobDetail, Trigger trigger) throws Exception {
        this.scheduler.scheduleJob(jobDetail, trigger);
    }

    public void addJob(JobDataMap jobDataMap, Trigger trigger) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class).storeDurably(true).usingJobData(jobDataMap).build();
        this.addJob(jobDetail, trigger);
    }

    public void addJob(String beanName, String methodName, Trigger trigger, Object... params) throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("targetObject", beanName);
        jobDataMap.put("targetMethod", methodName);
        jobDataMap.put("params", params);
        this.addJob(jobDataMap, trigger);
    }

    public void addJob(String beanName, String methodName, String cron, Object... params) throws Exception {
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(beanName + "." + methodName).withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        this.addJob(beanName, methodName, trigger, params);
    }

    public List<? extends Trigger> getTriggersOfJob(JobKey jobKey) throws Exception {
        return this.scheduler.getTriggersOfJob(jobKey);
    }

    public Trigger getTrigger(TriggerKey triggerKey) throws Exception {
        return this.scheduler.getTrigger(triggerKey);
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    public void rescheduleJobs(List<JobKey> jobs, List<TriggerKey> triggers, Map<String, JobDetailTrigger> jobDetailTriggerMap) throws Exception {
        this.scheduler.deleteJobs(jobs);
        this.scheduler.unscheduleJobs(triggers);
        this.scheduler.getListenerManager().addJobListener(new FixedDelayJobListener());
        //执行操作
        for (String key : jobDetailTriggerMap.keySet()) {
            JobDetailTrigger jobDetailTrigger = jobDetailTriggerMap.get(key);
            this.scheduler.scheduleJob(jobDetailTrigger.getJobDetail(), jobDetailTrigger.getTrigger());
        }

    }
}
