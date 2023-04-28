package com.dataease.cloud.constant;

/**
 * 短信服务提供商
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 2:28 PM
 **/
public enum SmsProvider {
    /**
     * 本地实现 只是打印了
     */
    local,
    /**
     * 阿里云短信
     */
    aliyun,
    /**
     * 腾讯云短信
     */
    tencent;
}
