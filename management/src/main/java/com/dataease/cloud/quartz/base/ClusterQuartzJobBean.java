package com.dataease.cloud.quartz.base;

import com.dataease.cloud.quartz.utils.ClassUtils;
import com.dataease.cloud.utils.CommonBeanFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * 集群定时任务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:18 AM
 **/
@PersistJobDataAfterExecution
@Slf4j
@Setter
public class ClusterQuartzJobBean extends QuartzJobBean {
    private String targetObject;
    private String targetMethod;
    private Object[] params;

    public ClusterQuartzJobBean() {
    }

    /**
     * 重写 根据bean与method获取执行对象
     * @param context
     */
    protected void executeInternal(JobExecutionContext context) {
        try {
            log.debug("The scheduled task starts：targetObject={}, targetMethod={}", this.targetObject, this.targetMethod);
            Object targetObject = CommonBeanFactory.getBean(this.targetObject);
            Method m = targetObject.getClass().getMethod(this.targetMethod, ClassUtils.toClass(this.params));
            m.invoke(targetObject, this.params);
            log.debug("The scheduled task ends normally：targetObject={}, targetMethod={}", this.targetObject, this.targetMethod);
        } catch (Exception e) {
            log.error("The scheduled task execution failed：targetObject=" + this.targetObject + ", targetMethod=" + this.targetMethod, e);
        }

    }
}
