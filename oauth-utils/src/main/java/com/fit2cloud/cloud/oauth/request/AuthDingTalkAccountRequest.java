package com.fit2cloud.cloud.oauth.request;

import com.fit2cloud.cloud.oauth.cache.AuthStateCache;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;

/**
 * 钉钉账号登录
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.0.0
 */
public class AuthDingTalkAccountRequest extends AbstractAuthDingtalkRequest {

    public AuthDingTalkAccountRequest(AuthConfig config) {
        super(config, AuthDefaultSource.DINGTALK_ACCOUNT);
    }

    public AuthDingTalkAccountRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.DINGTALK_ACCOUNT, authStateCache);
    }
}
