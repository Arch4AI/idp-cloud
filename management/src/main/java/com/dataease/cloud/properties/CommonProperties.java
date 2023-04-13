package com.dataease.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
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
    private String rememberTokenKey = "cloud_idp";

    /**
     * session到期时间<这里指的只是登录后的Session>
     */
    private Long sessionTimeOut = 1800l;


    /**
     * 前端VITE的地址查询
     */
    private String viteAddr = "http://127.0.0.1:4000";

    /**
     * 记住我的cookieName
     */
    private String rememberCookieName = "CLOUD_REMEMBER_ME";

    /**
     * 单用户最大在线数量
     */
    private int maximumSessions = -1;

    /**
     * 是否开启记住我 默认true
     */
    private boolean autoRememberMe = true;

    /**
     * 记住我的token时间 秒 默认两个星期
     */
    private int rememberMeTokenTime = AbstractRememberMeServices.TWO_WEEKS_S;

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
