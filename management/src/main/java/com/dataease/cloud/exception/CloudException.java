package com.dataease.cloud.exception;

/**
 * 自定义异常
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 11:38 AM
 **/
public class CloudException extends RuntimeException{
    private CloudException(String message) {
        super(message);
    }

    private CloudException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new CloudException(message);
    }

    public static void throwException(Throwable t) {
        throw new CloudException(t);
    }
}
