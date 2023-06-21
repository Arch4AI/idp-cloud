package com.dataease.cloud.security.social.constant;

import com.dataease.cloud.security.social.base.ClientRegistrationSetting;
import com.dataease.cloud.security.social.secret.AbstractSecret;
import com.dataease.cloud.security.social.secret.impl.*;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 默认支持的客服端类型
 */
public enum ClientType implements ClientRegistrationSetting {

    /**
     * 企业微信自建应用
     */
    wechatwork_self_app() {
        @Override
        public String authorize() {
            return "https://login.work.weixin.qq.com/wwlogin/sso/login";
        }

        @Override
        public String accessToken() {
            return "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        }

        @Override
        public String userInfo() {
            return "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo";
        }

        @Override
        public Set<String> scopes() {
            return Collections.emptySet();
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "userid";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return WechatworkSelfSecret.class;
        }
    },
    /**
     * 企业微信外部应用
     */
    wechatwork_external_app() {
        @Override
        public String authorize() {
            return "https://login.work.weixin.qq.com/wwlogin/sso/login";
        }

        @Override
        public String accessToken() {
            return "https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";
        }

        @Override
        public String userInfo() {
            return "https://qyapi.weixin.qq.com/cgi-bin/service/auth/getuserinfo3rd";
        }

        @Override
        public Set<String> scopes() {
            return Collections.emptySet();
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "open_userid";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return WechatworkExternalSecret.class;
        }
    },
    /**
     * 微信开放平台
     */
    wechat() {
        @Override
        public String authorize() {
            return "https://open.weixin.qq.com/connect/qrconnect";
        }

        @Override
        public String accessToken() {
            return "https://api.weixin.qq.com/sns/oauth2/access_token";
        }

        @Override
        public String userInfo() {
            return "https://api.weixin.qq.com/sns/userinfo";
        }

        @Override
        public Set<String> scopes() {
            return new LinkedHashSet<>() {{
                add("snsapi_login");
            }};
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "openid";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return WechatSecret.class;
        }
    },
    /**
     * qq互联
     */
    qq() {
        @Override
        public String authorize() {
            return "https://graph.qq.com/oauth2.0/authorize";
        }

        @Override
        public String accessToken() {
            return "https://graph.qq.com/oauth2.0/token";
        }

        @Override
        public String userInfo() {
            return "https://graph.qq.com/oauth2.0/me";
        }

        @Override
        public Set<String> scopes() {
            return new LinkedHashSet<>() {{
                add("get_user_info");
            }};
        }

        @Override
        public String scopeSeparator() {
            return ",";
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "openid";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return QQSecret.class;
        }
    },
    /**
     * 钉钉
     */
    dingtalk() {
        @Override
        public String authorize() {
            return "https://login.dingtalk.com/oauth2/auth";
        }

        @Override
        public String accessToken() {
            return "https://api.dingtalk.com/v1.0/oauth2/userAccessToken";
        }

        @Override
        public String userInfo() {
            return "https://api.dingtalk.com/v1.0/contact/users/me";
        }

        @Override
        public Set<String> scopes() {
            return Collections.emptySet();
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.HEADER;
        }

        @Override
        public String userNameAttributeName() {
            return "openId";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return DingWeiboSecret.class;
        }
    },
    /**
     * 飞书自建应用
     */
    feishu_self_app() {
        @Override
        public String authorize() {
            return "https://open.feishu.cn/open-apis/authen/v1/index";
        }

        @Override
        public String accessToken() {
            return "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";
        }

        @Override
        public String userInfo() {
            return "https://open.feishu.cn/open-apis/authen/v1/access_token";
        }

        @Override
        public Set<String> scopes() {
            return Collections.emptySet();
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.HEADER;
        }

        @Override
        public String userNameAttributeName() {
            return "open_id";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return FeishuSecret.class;
        }
    },
    /**
     * 百度
     */
    baidu() {
        @Override
        public String authorize() {
            return "https://openapi.baidu.com/oauth/2.0/authorize";
        }

        @Override
        public String accessToken() {
            return "https://openapi.baidu.com/oauth/2.0/token";
        }

        @Override
        public String userInfo() {
            return "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
        }

        @Override
        public Set<String> scopes() {
            return new LinkedHashSet<>() {{
                add("basic");
            }};
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "openid";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return BaiduSecret.class;
        }
    },
    /**
     * 新浪微博
     */
    weibo() {
        @Override
        public String authorize() {
            return "https://api.weibo.com/oauth2/authorize";
        }

        @Override
        public String accessToken() {
            return "https://api.weibo.com/oauth2/access_token";
        }

        @Override
        public String userInfo() {
            return "https://api.weibo.com/2/users/show.json";
        }

        @Override
        public Set<String> scopes() {
            return new LinkedHashSet<>() {{
                add("all");
            }};
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return AuthenticationMethod.QUERY;
        }

        @Override
        public String userNameAttributeName() {
            return "idstr";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return DingWeiboSecret.class;
        }
    },
    /**
     * oidc协议
     */
    oidc() {
        @Override
        public String authorize() {
            return null;
        }

        @Override
        public String accessToken() {
            return null;
        }

        @Override
        public String userInfo() {
            return null;
        }

        @Override
        public Set<String> scopes() {
            return null;
        }

        @Override
        public String scopeSeparator() {
            return "+";
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return null;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return null;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return null;
        }

        @Override
        public String userNameAttributeName() {
            return "sub";
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return DefaultSecret.class;
        }
    },
    /**
     * oauth2协议
     */
    oauth2() {
        @Override
        public String authorize() {
            return null;
        }

        @Override
        public String accessToken() {
            return null;
        }

        @Override
        public String userInfo() {
            return null;
        }

        @Override
        public Set<String> scopes() {
            return null;
        }

        @Override
        public String scopeSeparator() {
            return super.scopeSeparator();
        }

        @Override
        public ClientAuthenticationMethod clientAuthenticationMethod() {
            return null;
        }

        @Override
        public AuthorizationGrantType authorizationGrantType() {
            return null;
        }

        @Override
        public AuthenticationMethod userInfoAuthenticationMethod() {
            return null;
        }

        @Override
        public String userNameAttributeName() {
            return null;
        }

        @Override
        public String getName() {
            return super.getName();
        }

        @Override
        public Class<? extends AbstractSecret> getSecretClass() {
            return DefaultSecret.class;
        }
    };
}
