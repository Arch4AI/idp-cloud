package com.dataease.cloud.utils;

import com.dataease.cloud.security.jackson2.CustomJackson2Module;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

/**
 * 获取ObjectMapper的一个实例
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 3:37 PM
 **/
public final class ObjectMapperUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final ObjectMapper simpleObjectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModules(SecurityJackson2Modules.getModules(ObjectMapperUtils.class.getClassLoader()));
        objectMapper.registerModules(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new CustomJackson2Module());

        simpleObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        simpleObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

}
