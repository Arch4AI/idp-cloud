package com.dataease.cloud.security.social.secret.impl;

import com.dataease.cloud.security.social.secret.AbstractSecret;
import lombok.Data;

/**
 * 默认的认证
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 10:52 AM
 **/
@Data
public class DefaultSecret extends AbstractSecret {
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端密钥
     */
    private String clientSecret;
}
