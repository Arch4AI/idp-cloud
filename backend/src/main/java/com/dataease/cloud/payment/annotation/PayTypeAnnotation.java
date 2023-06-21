package com.dataease.cloud.payment.annotation;


import com.dataease.cloud.payment.constant.PayProvider;

import java.lang.annotation.*;

/**
 * 支付类型的注解
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PayTypeAnnotation {

    /**
     * 支付提供者
     *
     * @return
     */
    PayProvider value();
}
