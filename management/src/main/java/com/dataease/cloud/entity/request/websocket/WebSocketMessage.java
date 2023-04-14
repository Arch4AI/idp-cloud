package com.dataease.cloud.entity.request.websocket;

import lombok.Data;

/**
 * webscoket消息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 2:04 PM
 **/
@Data
public class WebSocketMessage {
    private String message;//消息
    private String toUserId;//接收人
    private String fromUserId;//发送人
}
