package com.dataease.cloud.quartz.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义quart属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:02 AM
 **/
@Component
@ConfigurationProperties(
        prefix = "cloud.quartz",
        ignoreUnknownFields = true
)
@Data
public class CloudQuartzProperties {
    /**
     * 是否开启quartz
     */
    private boolean enabled = false;
    /**
     * 定时器名称
     */
    private String schedulerName = "quartzScheduler";
    /**
     * 时区
     */
    private String timeZone = "Asia/Shanghai";
    /**
     * 默认线程数
     */
    private Integer threadCount = 10;
    /**
     * 启动延时
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration startupDelay = Duration.ofSeconds(60L);
    /**
     * 默认分组名称
     */
    private String groupName = "DEFAULT";
    /**
     * 自定义属性
     */
    private final Map<String, String> properties = new HashMap();
}
