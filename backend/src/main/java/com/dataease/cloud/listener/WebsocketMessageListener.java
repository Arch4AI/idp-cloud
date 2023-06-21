package com.dataease.cloud.listener;

import com.dataease.cloud.controller.websocket.WebSocketServer;
import com.dataease.cloud.entity.request.websocket.WebSocketMessage;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * wesocket消息订阅器
 * 用于解决分布式情况下websocket出现的问题
 *
 * @author ShrChang.Liu
 * @date 2022-05-27
 */
@Slf4j
@Component
public class WebsocketMessageListener implements MessageListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> valueSerializer = stringRedisTemplate.getStringSerializer();
        String value = valueSerializer.deserialize(message.getBody());
        if (StringUtils.hasText(value)) {
            try {
                WebSocketMessage webSocketMessage = ObjectMapperUtils.simpleObjectMapper.readValue(value, WebSocketMessage.class);
                WebSocketServer.sendMessage(webSocketMessage.getToUserId(), webSocketMessage.getMessage());
                log.info("websocket message send success");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("消息格式转换异常：{}", e.toString());
            }
            log.info("监听集群websocket消息--- {}", value);
        }
    }
}
