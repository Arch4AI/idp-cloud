package com.dataease.cloud.config;

import com.dataease.cloud.base.domain.CloudRedisSession;
import com.dataease.cloud.base.domain.CloudRedisSessionExample;
import com.dataease.cloud.base.mapper.CloudRedisSessionMapper;
import com.dataease.cloud.constant.Constants;
import com.dataease.cloud.security.session.redis.RedisSessionRegistryImpl;
import com.dataease.cloud.utils.CommonBeanFactory;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.List;


/**
 * 开启redis http session
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/6 11:25 AM
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "cloud.session", value = "session-type", havingValue = "redis")
@EnableRedisHttpSession(redisNamespace = "${cloud.session.redis-namespace:spring:session}")
@Slf4j
public class RedisSessionConfig {

    @Resource
    private RedisProperties redisProperties;

    @Resource
    private CloudRedisSessionMapper cloudRedisSessionMapper;

    private ThreadPoolTaskScheduler taskScheduler;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(ObjectMapperUtils.objectMapper);
    }

    @Bean
    public SessionRegistry sessionRegistry(RedisSessionRepository sessionRepository) {
        RedisSessionRegistryImpl sessionRegistry = new RedisSessionRegistryImpl(sessionRepository);
        return sessionRegistry;
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

    /**
     * 初始化的时候初始化一个任务
     */
    @PostConstruct
    public void init() {
        if (!Scheduled.CRON_DISABLED.equals(Constants.SESSION_CLEANUP_CRON)) {
            this.taskScheduler = createTaskScheduler();
            this.taskScheduler.initialize();
            this.taskScheduler.schedule(this::cleanUpExpiredSessions, new CronTrigger(Constants.SESSION_CLEANUP_CRON));
        }
    }

    /**
     * 清除超时的session
     */
    public void cleanUpExpiredSessions() {
        log.info("start clean up redis session");
        RedisSessionRepository sessionRepository = CommonBeanFactory.getBean(RedisSessionRepository.class);
        if (sessionRepository != null) {
            //开始判断是不是session还存在 不存在就删除记录
            List<CloudRedisSession> cloudRedisSessions = cloudRedisSessionMapper.selectByExample(new CloudRedisSessionExample());
            cloudRedisSessions.forEach(session -> {
                Session s = sessionRepository.findById(session.getSessionId());
                if (s == null) {
                    cloudRedisSessionMapper.deleteByPrimaryKey(session.getSessionId());
                }
            });
        }
    }

    /**
     * 创建任务
     *
     * @return
     */
    private static ThreadPoolTaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("spring-redis-session-");
        return taskScheduler;
    }
}
