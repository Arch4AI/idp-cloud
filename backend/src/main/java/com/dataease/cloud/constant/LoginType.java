package com.dataease.cloud.constant;

/**
 * 登录类型 密码/手机/第三方
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 6:34 PM
 **/
public enum LoginType {
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 手机
     */
    PHONE,
    /**
     * 外部
     */
    EXTERNAL,
    /**
     * 社交
     */
    SOCIAL,
    /**
     * 记住我
     */
    REMEMBER_ME;
}
