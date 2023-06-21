package com.dataease.cloud.security.social.secret.impl;

import lombok.Data;

/**
 * 企业微信自建应用
 * clientId: 应用ID(App Id)
 * clientSecret: 应用密钥(AppSecret)
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 10:56 AM
 **/
@Data
public class WechatworkSelfSecret extends DefaultSecret {
    //企业ID
    private String corpId;
}
