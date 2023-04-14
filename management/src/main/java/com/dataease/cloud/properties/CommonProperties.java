package com.dataease.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 通用属性
 * 静态的变量返回只需要控制set是非Static即可注入
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
     * 启用requestBody加解密 默认不开启
     */
    private static boolean enableRequestBodyEncrypt = false;
    /**
     * 启用接口限流 默认不开启
     */
    private static boolean enableRateLimit = false;
    /**
     * 限流默认的QPS 默认QPS 50
     */
    private static double rateLimitQps=50;
    /**
     * 限流默认时间微秒 默认限流时间
     */
    private static long rateLimitTime=100;

    /**
     * 限流时间默认单位
     */
    private static TimeUnit rateLimitTimeUnit = TimeUnit.MICROSECONDS;


    public static boolean isEnableRequestBodyEncrypt() {
        return enableRequestBodyEncrypt;
    }

    public void setEnableRequestBodyEncrypt(boolean enableRequestBodyEncrypt) {
        this.enableRequestBodyEncrypt = enableRequestBodyEncrypt;
    }

    public static boolean isEnableRateLimit() {
        return enableRateLimit;
    }

    public void setEnableRateLimit(boolean enableRateLimit) {
        this.enableRateLimit = enableRateLimit;
    }

    public static double getRateLimitQps() {
        return rateLimitQps;
    }

    public void setRateLimitQps(double rateLimitQps) {
        this.rateLimitQps = rateLimitQps;
    }

    public static long getRateLimitTime() {
        return rateLimitTime;
    }

    public void setRateLimitTime(long rateLimitTime) {
        this.rateLimitTime = rateLimitTime;
    }

    public static TimeUnit getRateLimitTimeUnit() {
        return rateLimitTimeUnit;
    }

    public void setRateLimitTimeUnit(TimeUnit rateLimitTimeUnit) {
        this.rateLimitTimeUnit = rateLimitTimeUnit;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        this.addressEnabled = addressEnabled;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }
}
