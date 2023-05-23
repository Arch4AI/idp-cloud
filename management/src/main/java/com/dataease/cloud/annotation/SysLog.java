package com.dataease.cloud.annotation;

import com.dataease.cloud.constant.LogOperationType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义操作日志记录注解
 *
 * @author junhong
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 标题
     */
    public String title() default "无标题";

    /**
     * 业务类型
     */
    public LogOperationType type() default LogOperationType.NONE;

    /**
     * 是否保存请求的参数 默认false
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数 默认false
     */
    public boolean isSaveResponseData() default true;
}
