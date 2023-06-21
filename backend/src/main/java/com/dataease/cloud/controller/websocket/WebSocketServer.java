package com.dataease.cloud.controller.websocket;

import com.dataease.cloud.entity.request.websocket.WebSocketMessage;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * websocke链接
 */
@ServerEndpoint("/websocket/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("connect user:{},online user num:{}", userId, getOnlineCount());

        try {
            sendMessage("connect success");
        } catch (IOException e) {
            log.error("user:{},network error!", userId);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("user logout:{},当前在线人数为:{}", userId, getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("user [{}] message:,message body:{}", userId, message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.hasText(message)) {
            try {
                //解析发送的报文
                WebSocketMessage webSocketMessage = ObjectMapperUtils.simpleObjectMapper.readValue(message, WebSocketMessage.class);
                //追加发送人(防止串改)
                webSocketMessage.setFromUserId(this.userId);
                String toUserId = webSocketMessage.getToUserId();
                //传送给对应toUserId用户的websocket
                if (StringUtils.hasText(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(webSocketMessage));
                } else {
                    log.error("request userId:{} do not in this server", toUserId);
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                log.error("on message error:", e);
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("user:[{}] error:", this.userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("send message to user:{}，body:{}", userId, message);
        if (StringUtils.hasText(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    /**
     * 发送信息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    public static void sendMessage(String userId, String message) {
        log.info("send message to user:{},body:{}", userId, message);
        if (StringUtils.hasText(userId) && webSocketMap.containsKey(userId)) {
            try {
                webSocketMap.get(userId).sendMessage(message);
            } catch (Exception e) {
                log.error("send webosocket message error:", e);
            }
        } else {
            log.error("user:{} do not online", userId);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
