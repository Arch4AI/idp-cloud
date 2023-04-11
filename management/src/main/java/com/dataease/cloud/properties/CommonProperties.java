package com.dataease.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 通用属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/17 11:00 AM
 **/
@Component
@ConfigurationProperties(prefix = "cloud.common")
@Data
public class CommonProperties {

    /**
     * 记住我cookie加密Key
     */
    private String rememberTokenKey;

    /**
     * session到期时间<这里指的只是登录后的Session>
     */
    private Long sessionTimeOut = 1800l;


    /**
     * 前端VITE的地址查询
     */
    private String viteAddr = "http://127.0.0.1:4000";

    /**
     * 开启IP归属地查询
     */
    private static boolean addressEnabled = false;

    /**
     * 确定这个方法可以注入
     *
     * @param addressEnabled
     */
    public void setAddressEnabled(boolean addressEnabled) {
        this.addressEnabled = addressEnabled;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }
}
