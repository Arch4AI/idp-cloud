package com.dataease.cloud.controller.handler;

import cn.hutool.core.io.IoUtil;
import com.dataease.cloud.annotation.NoDecryptBody;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.utils.EncryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * 针对@requestBody请求体进行加密的方案
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 2:55 PM
**/
@ControllerAdvice
@Slf4j
@ConditionalOnProperty(prefix = "cloud.common",name = "enable-request-body-encrypt",havingValue = "true")
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

    // 如果是包含了不需要解密的就不进来
    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getMethod().isAnnotationPresent(NoDecryptBody.class) ? false : true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        log.info("**************Decrypt request body*************");
        if (inputMessage.getBody().available()<=0) {
            return inputMessage;
        }
        // 解密
        String body = IoUtil.readUtf8(inputMessage.getBody());
        String decryptBody = String.valueOf(EncryptUtils.aesDecrypt(body));
        if(!StringUtils.hasText(decryptBody) || body.equals(decryptBody))
            CloudException.throwException("Decrypt request body error");
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return IoUtil.toUtf8Stream(decryptBody);
            }
        };
    }

    /**
     * 读取之后的处理
     * @return
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
