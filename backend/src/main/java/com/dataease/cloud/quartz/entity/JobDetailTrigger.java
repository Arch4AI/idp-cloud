package com.dataease.cloud.quartz.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * 保存信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:16 AM
 **/
@Getter
@AllArgsConstructor
public class JobDetailTrigger {

    JobDetail jobDetail;
    Trigger trigger;


}
