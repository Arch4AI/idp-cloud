package com.fit2cloud.cloud.oauth.request;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.cloud.oauth.cache.AuthStateCache;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;
import com.fit2cloud.cloud.oauth.enums.scope.AuthWeChatEnterpriseWebScope;
import com.fit2cloud.cloud.oauth.exception.AuthException;
import com.fit2cloud.cloud.oauth.model.AuthCallback;
import com.fit2cloud.cloud.oauth.model.AuthToken;
import com.fit2cloud.cloud.oauth.model.AuthUser;
import com.fit2cloud.cloud.oauth.utils.AuthScopeUtils;
import com.fit2cloud.cloud.oauth.utils.HttpUtils;
import com.fit2cloud.cloud.oauth.utils.UrlBuilder;

/**
 * <p>
 * https://developer.work.weixin.qq.com/document/path/98151
 * 企业微信最新外部网页登录
 * 他的获取信息需要调用异步的内容不太好处理
 * https://developer.work.weixin.qq.com/document/path/98179
 * </p>
 *
 * @author shrchang (347826496(a)qq.com)
 * @since 1.15.9
 */
public class AuthWeChatEnterprisePageRequest extends AuthDefaultRequest {
    public AuthWeChatEnterprisePageRequest(AuthConfig config) {
        super(config, AuthDefaultSource.WECHAT_ENTERPRISE_PAGE);
    }

    public AuthWeChatEnterprisePageRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.WECHAT_ENTERPRISE_PAGE, authStateCache);
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .queryParam("login_type", "ServiceApp")
                .queryParam("appid", config.getClientId())//第三方登录时填登录授权 SuiteID
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("state", getRealState(state))
                .build();
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        return AuthToken.builder()
                .accessToken(authCallback.getOauth_token())//这个token需要持久化
                // 需要从回调的地址那边传递过来 否则会存在问题 获取的方式在下面可以查询到 需要监听定时推送的去进行获取校验
                // https://developer.work.weixin.qq.com/document/10975#%E8%8E%B7%E5%8F%96%E7%AC%AC%E4%B8%89%E6%96%B9%E5%BA%94%E7%94%A8%E5%87%AD%E8%AF%81
                .code(authCallback.getCode())
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String userInfo = doGetUserInfo(authToken);
        checkResponse(userInfo);
        JSONObject userObj = JSONObject.parseObject(userInfo);
        return AuthUser.builder()
                .rawUserInfo(userObj)
                .source(AuthDefaultSource.WECHAT_ENTERPRISE_PAGE.name())
                .uuid(userObj.getString("open_userid"))
                .build();
    }

    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return UrlBuilder.fromBaseUrl(source.userInfo())
                .queryParam("suite_access_token", authToken.getAccessToken())
                .queryParam("code",authToken.getCode())
                .build();
    }

    /**
     * 检查返回信息
     * @param response
     * @return
     */
    private JSONObject checkResponse(String response) {
        JSONObject object = JSONObject.parseObject(response);
        if (object.containsKey("errcode") && object.getIntValue("errcode") != 0) {
            throw new AuthException(object.getString("errmsg"), source);
        }
        return object;
    }
}
