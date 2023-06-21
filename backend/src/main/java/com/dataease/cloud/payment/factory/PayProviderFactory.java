package com.dataease.cloud.payment.factory;

import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.payment.annotation.PayTypeAnnotation;
import com.dataease.cloud.payment.constant.PayProvider;
import com.dataease.cloud.payment.properties.BasePayProperties;
import com.dataease.cloud.payment.service.AbstractPayService;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 支付类工厂
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/28 9:53 AM
 **/
public class PayProviderFactory {


    /**
     * 根据传入的属性获取对应的payService实现类
     *
     * @param properties 继承
     * @param <T>
     * @return
     */
    public static <T extends AbstractPayService, S extends BasePayProperties> T getPayService(S properties) {
        if (properties == null)
            CloudException.throwException(I18nService.getMessage("payment_config_required"));
        PayProvider payProvider = properties.getPayProvider();
        if (payProvider == null)
            CloudException.throwException(I18nService.getMessage("payment_config_provider_required"));
        Reflections reflections = new Reflections("com.dataease.cloud.payment.service");
        Set<Class<? extends AbstractPayService>> payServiceSet = reflections.getSubTypesOf(AbstractPayService.class);
        for (Class<? extends AbstractPayService> clz : payServiceSet) {
            PayTypeAnnotation payTypeAnnotation = clz.getAnnotation(PayTypeAnnotation.class);
            if (payTypeAnnotation != null && payTypeAnnotation.value().equals(payProvider)) {
                try {
                    return (T) clz.getDeclaredConstructor(properties.getClass()).newInstance(properties);
                } catch (InvocationTargetException e) {
                    CloudException.throwException(e.getTargetException());
                } catch (Exception e) {
                    CloudException.throwException(e);
                }
            }
        }
        return null;
    }
}
