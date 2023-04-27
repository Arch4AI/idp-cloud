package com.dataease.cloud.properties.pay;

import lombok.Data;

/**
 * 支付宝支付需要的参数
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 8:12 AM
 **/
@Data
public class AliPayProperties {
    private String privateKey;//私钥
    private String alipayPublicKey;//公钥
    private String serverUrl;//服务请求地址
    private String appId;//应用ID
}
