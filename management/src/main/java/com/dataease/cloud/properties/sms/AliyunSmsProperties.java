package com.dataease.cloud.properties.sms;

import com.dataease.cloud.constant.SmsSendType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云短信配置
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 3:45 PM
 **/
@Data
public class AliyunSmsProperties {
    /**
     * regionId
     */
    private String regionId;

    /**
     * ak
     */
    private String accessKey;

    /**
     * sk
     */
    private String secretKey;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 默认模版
     */
    private String defalutTemplate;

    /**
     * 模版
     * 对应这个里面的属性
     * {@link com.dataease.cloud.constant.SmsSendType}
     */
    private Map<SmsSendType, String> template = new HashMap<>();
}
