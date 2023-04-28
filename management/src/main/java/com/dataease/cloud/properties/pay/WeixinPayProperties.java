package com.dataease.cloud.properties.pay;

import lombok.Data;


/**
 * 微信支付的配置
 * 这个可能是数据库会配置的
 * 先默认按照固定实体
 */
@Data
public class WeixinPayProperties {
    private String appId;//应用ID
    private String merchantId;//商户ID
    private String notifyUrl;//回调url
    private String apiV3Key;//v3 key
    private String merchantSerialNumber;//商户序列号
    private String privateKey;//私钥内容
}
