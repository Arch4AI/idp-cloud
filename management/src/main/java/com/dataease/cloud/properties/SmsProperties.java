package com.dataease.cloud.properties;

import com.dataease.cloud.constant.SmsProvider;
import com.dataease.cloud.properties.sms.AliyunSmsProperties;
import com.dataease.cloud.properties.sms.TencentSmsProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信配置属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 2:39 PM
 **/
@ConfigurationProperties(value = "cloud.sms", ignoreUnknownFields = true)
@Data
public class SmsProperties {

    /**
     * 短信类型
     */
    private SmsProvider provider = SmsProvider.local;

    /**
     * 阿里云短信的配置
     */
    private AliyunSmsProperties aliyun;

    /**
     * 腾讯云短信配置
     */
    private TencentSmsProperties tencent;
}
