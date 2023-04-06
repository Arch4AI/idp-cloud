package com.dataease.cloud.security.phone.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 手机号码登录
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 4:46 PM
 **/
@Data
public class PhoneLoginRequest implements Serializable {
    //手机号码
    private String phone;
    //验证码
    private String code;
    //来源ID
    private String sourceId;
    //用户的uuid
    private String uuid;
}
