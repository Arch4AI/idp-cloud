package com.dataease.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 支持websocket配置
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 11:46 AM
**/
@Configuration(proxyBeanMethods = false)
public class WebsockConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
