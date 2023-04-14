package com.dataease.cloud.annotation;

import java.lang.annotation.*;

/**
 * 设置接口不限流
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 2:43 PM
**/
@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRateLimit {
}
