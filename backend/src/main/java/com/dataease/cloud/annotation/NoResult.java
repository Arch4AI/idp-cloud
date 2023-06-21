package com.dataease.cloud.annotation;

import java.lang.annotation.*;

/**
 * 不返回Result格式
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResult {
}
