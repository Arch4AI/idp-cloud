package com.dataease.cloud.quartz.annotation;

import java.lang.annotation.*;

/**
 * quartz任务注解
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzScheduled {

    /**
     * cron表达式
     * @return
     */
    String cron() default "";

    /**
     * 单位是毫秒 只执行一次
     * @return
     */
    long fixedDelay() default -1L;

    /**
     * 隔多久执行一次
     * @return
     */
    long fixedRate() default -1L;

    /**
     * 初始化时间
     * @return
     */
    long initialDelay() default 0L;
}
