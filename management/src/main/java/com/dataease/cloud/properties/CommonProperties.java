package com.dataease.cloud.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 通用属性
 * 静态的变量返回只需要控制set是非Static即可注入
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
     * 前端VITE的地址查询
     */
    private String viteAddr = "http://127.0.0.1:4000";

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
    private static double rateLimitQps = 50;
    /**
     * 限流默认时间微秒 默认限流时间
     */
    private static long rateLimitTime = 100;

    /**
     * 限流时间默认单位 默认是微秒
     */
    private static TimeUnit rateLimitTimeUnit = TimeUnit.MICROSECONDS;

    /**
     * 用于oidc,oauthserver的发行地址 默认为空取当前应用的基础地址
     */
    private URL issuerUrl;


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
