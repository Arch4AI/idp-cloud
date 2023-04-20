package com.dataease.cloud.config;

import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.properties.SessionProperties;
import com.dataease.cloud.security.rememberme.RememberMeDetailsService;
import com.dataease.cloud.security.rememberme.service.PersistentTokenBasedRememberMeService;
import com.dataease.cloud.service.RememberMeTokenService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.web.authentication.RememberMeServices;

import java.time.Instant;
import java.util.Date;

/**
 * rememberMe初始化
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/18 2:49 PM
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
public class RememberMeConfig {

    @Resource
    private RememberMeDetailsService rememberMeDetailsService;

    @Resource
    private SessionProperties sessionProperties;

    @Resource
    private RememberMeTokenService rememberMeTokenService;

    private ThreadPoolTaskScheduler taskScheduler;


    /**
     * 构建RememberMeService
     *
     * @return
     */
    @Bean
    public RememberMeServices rememberMeService() {
        PersistentTokenBasedRememberMeService rememberMeService = new PersistentTokenBasedRememberMeService(sessionProperties.getRememberTokenKey(),
                rememberMeDetailsService, rememberMeTokenService,
                sessionProperties.getSessionTimeOut());
        //总是记住我 不需要额外参数
        rememberMeService.setAlwaysRemember(sessionProperties.isAutoRememberMe());
        //记住我的cookie名称
        rememberMeService.setCookieName(sessionProperties.getRememberCookieName());
        //默认是两个星期
        rememberMeService.setTokenValiditySeconds(sessionProperties.getRememberMeTokenTime());
        return rememberMeService;
    }

    /**
     * 销毁的时候就移除任务定时器
     */
    @PreDestroy
    public void destory() {
        if (this.taskScheduler != null) {
            this.taskScheduler.destroy();
        }
    }

    @PostConstruct
    public void init() {
        if (!Scheduled.CRON_DISABLED.equals(Constants.SESSION_CLEANUP_CRON)) {
            this.taskScheduler = createTaskScheduler();
            this.taskScheduler.initialize();
            this.taskScheduler.schedule(this::cleanUpExpiredToken, new CronTrigger(Constants.REMEMBER_ME_TOKEN_CLEANUP_CRON));
        }
    }

    /**
     * 清除到期的token
     */
    private void cleanUpExpiredToken() {
        try {
            log.info("************start clean rememberme expire token's task*************");
            //获取最大的maxTokenDate
            Instant instant = Instant.now().minusSeconds(sessionProperties.getRememberMeTokenTime());
            //移除掉这个时间
            Date maxTokenDate = Date.from(instant);
            //移除信息
            rememberMeTokenService.deleteByMaxDate(maxTokenDate);
            log.info("************clean rememberme expire token's task done*************");
        } catch (Exception e) {
            log.error("clean remember me token error task:", e);
        }
    }

    /**
     * 创建任务
     *
     * @return
     */
    private static ThreadPoolTaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("spring-remeber-token-");
        return taskScheduler;
    }

}
