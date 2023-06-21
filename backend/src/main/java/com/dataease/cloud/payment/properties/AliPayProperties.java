package com.dataease.cloud.payment.properties;

import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 支付宝支付需要的参数
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 8:12 AM
 **/
@Data
public class AliPayProperties extends BasePayProperties {
    private String publicKey;//公钥
    private String serverUrl;//服务请求地址

    @Override
    protected void requiredCheck() {
        if (!StringUtils.hasText(publicKey))
            CloudException.throwException(I18nService.getMessage("payment_config_public_key_required"));
        if (!StringUtils.hasText(serverUrl))
            CloudException.throwException(I18nService.getMessage("payment_config_server_url_required"));
    }
}
