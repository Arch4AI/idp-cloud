package com.dataease.cloud.payment.properties;

import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import lombok.Data;
import org.springframework.util.StringUtils;


/**
 * 微信支付的配置
 * 这个可能是数据库会配置的
 * 先默认按照固定实体
 */
@Data
public class WeixinPayProperties extends BasePayProperties {
    private String merchantId;//商户ID
    private String apiV3Key;//v3 key
    private String merchantSerialNumber;//商户序列号

    @Override
    protected void requiredCheck() {
        if (!StringUtils.hasText(merchantId))
            CloudException.throwException(I18nService.getMessage("payment_config_merchant_id_required"));
        if (!StringUtils.hasText(apiV3Key))
            CloudException.throwException(I18nService.getMessage("payment_config_api_v3_key_required"));
        if (!StringUtils.hasText(merchantSerialNumber))
            CloudException.throwException(I18nService.getMessage("payment_config_merchant_sn_required"));
        if (!StringUtils.hasText(notifyUrl))
            CloudException.throwException(I18nService.getMessage("payment_config_notify_url_required"));
    }
}
