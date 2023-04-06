package com.dataease.cloud.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * SpringBean获取方式
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 4:28 PM
 **/
public class CommonBeanFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * 根据名称获取Bean
     *
     * @param beanName bean名称
     * @return Bean实例
     */
    public static Object getBean(String beanName) {
        if (context == null || !StringUtils.hasText(beanName)) {
            return null;
        }
        return context.getBean(beanName);
    }

    /**
     * 根据类型获取Bean
     *
     * @param className bean类型
     * @param <T>       bean类型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> className) {
        if (context == null || className == null) {
            return null;
        }
        return context.getBean(className);
    }

    /**
     * 根据参数获取Bean
     *
     * @param className
     * @param var
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> className, Object... var) {
        if (context == null || className == null) {
            return null;
        }
        return context.getBean(className, var);
    }
}
