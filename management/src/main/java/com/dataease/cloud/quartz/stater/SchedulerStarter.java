package com.dataease.cloud.quartz.stater;

import com.dataease.cloud.quartz.annotation.QuartzScheduled;
import com.dataease.cloud.quartz.base.ClusterQuartzFixedDelayJobBean;
import com.dataease.cloud.quartz.base.ClusterQuartzJobBean;
import com.dataease.cloud.quartz.base.FixedDelayJobData;
import com.dataease.cloud.quartz.entity.JobDetailTrigger;
import com.dataease.cloud.quartz.properties.CloudQuartzProperties;
import com.dataease.cloud.quartz.service.QuartzManageService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.SchedulingException;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 启动器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 10:58 AM
 **/
@Slf4j
@Component
@ConditionalOnProperty(prefix = "cloud.quartz", value = "enabled", havingValue = "true")
public class SchedulerStarter implements BeanPostProcessor, ApplicationContextAware, ApplicationRunner {
    //当前时间
    private Instant now;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private TimeZone quartzTimeZone;

    @Autowired
    private CloudQuartzProperties cloudQuartzProperties;

    private Map<String, JobDetailTrigger> jobDetailTriggerMap = new HashMap();

    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private QuartzManageService quartzManageService;

    /**
     * 加载bean之前
     * 这里默认会在所有的Bean加载的时候都会进来
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * bean加载之后
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            QuartzScheduled annotation = (QuartzScheduled) AnnotationUtils.findAnnotation(method, QuartzScheduled.class);
            if (annotation != null) {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("targetObject", beanName);
                jobDataMap.put("targetMethod", method.getName());
                String cron = annotation.cron();
                long fixedDelay = annotation.fixedDelay();
                long fixedRate = annotation.fixedRate();
                int initialDelay = (int)annotation.initialDelay();
                String jobDetailIdentity = beanName + "." + method.getName();
                JobDetail jobDetail;
                Trigger trigger;
                if (cron != null && !"".equals(cron)) {
                    cron = this.getCronExpression(cron);
                    jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class).storeDurably(true).usingJobData(jobDataMap).build();
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(TriggerKey.triggerKey(jobDetailIdentity, this.cloudQuartzProperties.getGroupName()))
                            .startAt(new Date(this.now.plusMillis((long)initialDelay).toEpochMilli()))
                            .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(this.quartzTimeZone)).build();
                } else if (fixedDelay > 0L) {
                    jobDataMap.put("FIXED_DELAY_JOB_DATA", new FixedDelayJobData(fixedDelay));
                    jobDetail = JobBuilder.newJob(ClusterQuartzFixedDelayJobBean.class).storeDurably(true).usingJobData(jobDataMap).build();
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(TriggerKey.triggerKey(jobDetailIdentity, this.cloudQuartzProperties.getGroupName()))
                            .startAt(new Date(this.now.plusMillis((long)initialDelay).toEpochMilli()))
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(fixedDelay).repeatForever()).build();
                } else {
                    jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class).storeDurably(true).usingJobData(jobDataMap).build();
                    trigger = TriggerBuilder.newTrigger()
                            .withIdentity(TriggerKey.triggerKey(jobDetailIdentity, this.cloudQuartzProperties.getGroupName()))
                            .startAt(new Date(this.now.plusMillis((long)initialDelay).toEpochMilli()))
                            .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(fixedRate).repeatForever()).build();
                }
                this.jobDetailTriggerMap.put(jobDetailIdentity, new JobDetailTrigger(jobDetail, trigger));
            }
        }

        return bean;
    }

    /**
     * 获取cron表达式
     * @param cron
     * @return
     */
    private String getCronExpression(String cron) {
        cron = cron.trim();
        return cron.startsWith("${") && cron.endsWith("}") ? this.applicationContext.getBeanFactory().resolveEmbeddedValue(cron) : cron;
    }

    /**
     * 获取所有的任务key
     * @return
     * @throws SchedulerException
     */
    private List<JobKey> getJobKeys() throws SchedulerException {
        return new ArrayList(this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(this.cloudQuartzProperties.getGroupName())));
    }

    /**
     * 获取所有的定时器key
     * @return
     * @throws SchedulerException
     */
    private List<TriggerKey> getTriggerKeys() throws SchedulerException {
        return new ArrayList(this.scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(this.cloudQuartzProperties.getGroupName())));
    }

    /**
     * 获取上下文
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.now = Instant.now();
        this.applicationContext = (ConfigurableApplicationContext)applicationContext;
    }

    /**
     * 项目启动后执行
     * @param args
     * @throws Exception
     */
    public void run(ApplicationArguments args) throws Exception {
        try {
            if (!this.scheduler.isShutdown()) {
                Thread schedulerThread = new Thread(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(cloudQuartzProperties.getStartupDelay().getSeconds());
                    } catch (InterruptedException var3) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        log.info("reschedule all jobs...");
                        this.quartzManageService.rescheduleJobs(this.getJobKeys(), this.getTriggerKeys(), this.jobDetailTriggerMap);
                        this.scheduler.start();
                    } catch (Exception e) {
                        log.error("quartz start error:",e);
                        throw new SchedulingException("Could not start Quartz Scheduler after delay", e);
                    }
                });
                schedulerThread.setName("Quartz Scheduler [" + this.scheduler.getSchedulerName() + "]");
                schedulerThread.setDaemon(true);
                schedulerThread.start();
            }
        } catch (SchedulerException e) {
            log.error("quartz start error:",e);
        }

    }
}
