package com.dataease.cloud.payment.properties;

import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.payment.constant.PayProvider;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 基础的支付配置属性
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/28 9:58 AM
 **/
@Data
public abstract class BasePayProperties {
    protected String notifyUrl;//支付回调地址 如果没有配置默认用开放平台设置的回调地址
    protected String appId;//应用ID
    protected PayProvider payProvider;//支付提供者
    protected String privateKey;//私钥

    /**
     * 校验信息
     */
    public void validate() {
        requiredCheck();
        if (!StringUtils.hasText(privateKey))
            CloudException.throwException(I18nService.getMessage("payment_config_private_key_required"));
        if (!StringUtils.hasText(appId))
            CloudException.throwException(I18nService.getMessage("payment_config_app_id_required"));
        if (payProvider == null)
            CloudException.throwException(I18nService.getMessage("payment_config_provider_required"));
    }

    ;

    /**
     * 检查属性是否必填
     */
    protected abstract void requiredCheck();
}
