package com.dataease.cloud.annotation;

import java.lang.annotation.*;

/**
 * 不加密请求体
 * @author ShrChang.Liu
 * @date 2022-05-25
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDecryptBody {
}
