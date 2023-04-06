package com.fit2cloud.cloud.oauth.request;

import com.fit2cloud.cloud.oauth.cache.AuthStateCache;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;

/**
 * 微软登录
 *
 * @author yangkai.shen (https://xkcoding.com)
 * @update:2021-08-24 mroldx (xzfqq5201314@gmail.com)
 * @since 1.5.0
 */
public class AuthMicrosoftRequest extends AbstractAuthMicrosoftRequest {

    public AuthMicrosoftRequest(AuthConfig config) {
        super(config, AuthDefaultSource.MICROSOFT);
    }

    public AuthMicrosoftRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.MICROSOFT, authStateCache);
    }

}
