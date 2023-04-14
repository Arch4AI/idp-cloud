package com.dataease.cloud.controller.websocket;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis发送信息 需要一个订阅者
 * @author ShrChang.Liu
 * @date 2022-05-27
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发布
     * @param key
     */
    public void publish(String key, String value) {
        stringRedisTemplate.convertAndSend(key, value);
    }
}
