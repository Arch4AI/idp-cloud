package com.dataease.cloud.controller.handler;

import com.dataease.cloud.annotation.NoResult;
import com.dataease.cloud.entity.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一处理返回结果集
 */
@RestControllerAdvice
@Slf4j
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType) || StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        // 处理空值
        if (o == null && StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return null;
        }

        if (methodParameter.hasMethodAnnotation(NoResult.class)) {
            return o;
        }

        //如果判断不是ResultHolder直接组装返回即可
        if (!(o instanceof Result)) {
            return Result.success(o);
        }
        return o;
    }

}
