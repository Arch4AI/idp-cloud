package com.dataease.cloud.security.password.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 密码登录请求体
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 4:18 PM
 **/
@Data
public class PasswordLoginRequest implements Serializable {

    //用户名
    private String userName;
    //密码
    private String password;
    //社交登录配置ID
    private String registrationId;
    //社交用户ID
    private String uuid;
}
