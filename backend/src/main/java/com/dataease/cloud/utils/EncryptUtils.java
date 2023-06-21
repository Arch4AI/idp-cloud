package com.dataease.cloud.utils;

public class EncryptUtils extends CodingUtil {

    private static final String secretKey = "www.cloud.bu";
    private static final String iv = "1234567890123456";


    public static Object aesEncrypt(Object o) {
        if (o == null) {
            return null;
        }
        return aesEncrypt(o.toString(), secretKey, iv);
    }

    public static Object aesDecrypt(Object o) {
        if (o == null) {
            return null;
        }
        return aesDecrypt(o.toString(), secretKey, iv);
    }
}
