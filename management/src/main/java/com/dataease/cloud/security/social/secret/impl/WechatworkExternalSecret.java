package com.dataease.cloud.security.social.secret.impl;

import lombok.Data;

/**
 * 企业微信外部应用
 * clientId: 应用ID(SuiteID)
 * clientSecret: 应用密钥(Secret)
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 10:56 AM
 **/
@Data
public class WechatworkExternalSecret extends DefaultSecret {
    //企业ID
    private String corpId;
    //应用token
    private String token;
    //应用加密
    private String encodingAESKey;
}
