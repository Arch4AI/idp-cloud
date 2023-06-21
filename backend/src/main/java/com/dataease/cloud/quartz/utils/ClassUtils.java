package com.dataease.cloud.quartz.utils;

/**
 * class工具类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/17 11:49 AM
 **/
public class ClassUtils {
    /**
     * 根据请求返回class
     *
     * @param array
     * @return
     */
    public static Class<?>[] toClass(Object... array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Class[0];
        } else {
            Class<?>[] classes = new Class[array.length];

            for (int i = 0; i < array.length; ++i) {
                classes[i] = array[i] == null ? null : array[i].getClass();
            }

            return classes;
        }
    }
}
