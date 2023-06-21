package com.dataease.cloud.quartz.base;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * 集群任务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:20 AM
 **/
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ClusterQuartzFixedDelayJobBean extends ClusterQuartzJobBean {
    public ClusterQuartzFixedDelayJobBean() {
    }
}
