package com.dataease.cloud.annotation;

import com.dataease.cloud.constant.LogBusinessType;
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
     * 操作描述
     */
    public String title() default "";

    /**
     * 业务类型
     */
    public LogBusinessType logBusinessType() default LogBusinessType.SELECT;
}
