package com.dataease.cloud.security.exclude;

import com.dataease.cloud.constant.Constants;

/**
 * 不需要校验的URI
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 11:57 AM
 **/
public final class DefaultSecurityExcludes {

    // 默认不需要认证的URI都在这里
    public static final String[] excludes = new String[]{
            Constants.PASSWORD_LOGIN_URI,
            Constants.SMS_SEND_URI,
            Constants.PHONE_LOGIN_URI,
            Constants.ANONYMOUS_URI,
            Constants.REGISTER_URI,
            Constants.USER_PROFILE,
            Constants.EXTERNAL_LOGIN_URI,
            "/sms/check",
            "/actuator/**",
            "/login",
            "/auth/providers",
            "/auth/providers/**",
            "/user/me"
    };
}
