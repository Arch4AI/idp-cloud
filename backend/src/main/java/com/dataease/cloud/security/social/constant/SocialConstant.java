package com.dataease.cloud.security.social.constant;

/**
 * 常量
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/5/10 3:11 PM
 **/
public class SocialConstant {

    /**
     * 用于传递OAuth2AuthorizationResponse的key
     * 涉及原因是因为有些社交登录在url返回的信息在获取token的时候无用 只有在获取用户的是才有效
     * 如企业微信调用
     */
    public static final String AUTHORIZATION_RESPONSE_KEY = "authorizationResponse";

    /**
     * 用于保存获取AccessToken时返回的用户唯一属性
     */
    public static final String SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR = "openid";
}
