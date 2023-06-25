package com.dataease.cloud.payment.properties;

import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 支付宝支付需要的参数
 * 暂时只支持非证书模式
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/27 8:12 AM
 **/
@Data
public class AliPayProperties extends BasePayProperties {
    private String publicKey;//公钥
    private String serverDomain;//服务域名

    @Override
    protected void requiredCheck() {
        if (!StringUtils.hasText(publicKey))
            CloudException.throwException(I18nService.getMessage("payment_config_public_key_required"));
        if (!StringUtils.hasText(serverDomain))
            CloudException.throwException(I18nService.getMessage("payment_config_server_domain_required"));
    }
}
