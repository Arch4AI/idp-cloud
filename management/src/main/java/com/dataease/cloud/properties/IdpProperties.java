package com.dataease.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 属性注入类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 11:00 AM
 **/
@Component
@ConfigurationProperties(prefix = "idp")
@Data
public class IdpProperties {

    /**
     * 记住我token的一个key
     */
    private String rememberTokenKey;

    /**
     * session过期时间
     */
    private Long sessionTimeOut = 1800l;


    /**
     * vite的地址
     */
    private String viteAddr = "http://127.0.0.1:4000";
}
