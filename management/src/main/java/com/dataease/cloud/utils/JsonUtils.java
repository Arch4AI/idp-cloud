package com.dataease.cloud.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * JSON转换类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 11:48 AM
 **/
@Slf4j
public class JsonUtils {


    /**
     * 转JSON字符串
     * @param obj
     * @return
     */
    public static String toJsonString(Object obj){
        if(obj != null){
            try {
                return ObjectMapperUtils.simpleObjectMapper.writeValueAsString(obj);
            }catch (Exception e){
                log.error("object transform json string error:",e);
            }
        }
        return null;
    }

    /**
     * 转对象
     * @param json
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T jsonToObject(String json,Class<T> clz){
        try {
            return ObjectMapperUtils.simpleObjectMapper.readValue(json,clz);
        }catch (Exception e){
            log.error("string transform object error:",e);
        }
        return null;
    }
}
