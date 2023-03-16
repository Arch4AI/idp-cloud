package com.dataease.cloud.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 自定义安全异常类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 3:56 PM
 **/
public class SecurityException extends AuthenticationException {

    public SecurityException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SecurityException(String msg) {
        super(msg);
    }

    public static void throwException(String msg){
        throw new SecurityException(msg);
    }

    public static void throwException(Throwable t) {
        throw new SecurityException(t.getLocalizedMessage());
    }
}
