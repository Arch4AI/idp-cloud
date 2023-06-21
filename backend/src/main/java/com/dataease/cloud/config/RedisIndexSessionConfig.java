package com.dataease.cloud.config;

import com.dataease.cloud.security.session.redisIndex.RedisIndexSessionRegistryImpl;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

/**
 * redis索引型的
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/18 3:23 PM
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "cloud.session", value = "session-type", havingValue = "redis_index")
@EnableRedisIndexedHttpSession(redisNamespace = "${cloud.session.redis-namespace:spring:session}")
public class RedisIndexSessionConfig {

    @Resource
    private RedisProperties redisProperties;

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
    public SessionRegistry sessionRegistry(RedisIndexedSessionRepository sessionRepository) {
        return new RedisIndexSessionRegistryImpl<>(sessionRepository);
    }
}
