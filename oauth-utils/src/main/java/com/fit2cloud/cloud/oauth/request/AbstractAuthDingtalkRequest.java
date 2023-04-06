package com.fit2cloud.cloud.oauth.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.cloud.oauth.cache.AuthStateCache;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthSource;
import com.fit2cloud.cloud.oauth.enums.AuthUserGender;
import com.fit2cloud.cloud.oauth.exception.AuthException;
import com.fit2cloud.cloud.oauth.model.AuthCallback;
import com.fit2cloud.cloud.oauth.model.AuthToken;
import com.fit2cloud.cloud.oauth.model.AuthUser;
import com.fit2cloud.cloud.oauth.utils.GlobalAuthUtils;
import com.fit2cloud.cloud.oauth.utils.HttpUtils;
import com.fit2cloud.cloud.oauth.utils.UrlBuilder;

/**
 * <p>
 * 钉钉登录抽象类，负责处理使用钉钉账号登录第三方网站和扫码登录第三方网站两种钉钉的登录方式
 * </p>
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @since 1.16.0
 */
public abstract class AbstractAuthDingtalkRequest extends AuthDefaultRequest {

    public AbstractAuthDingtalkRequest(AuthConfig config, AuthSource source) {
        super(config, source);
    }


    public AbstractAuthDingtalkRequest(AuthConfig config, AuthSource source, AuthStateCache authStateCache) {
        super(config, source, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        return AuthToken.builder().accessCode(authCallback.getCode()).build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String code = authToken.getAccessCode();
        JSONObject param = new JSONObject();
        param.put("tmp_auth_code", code);
        String response = new HttpUtils(config.getHttpConfig()).post(userInfoUrl(authToken), param.toJSONString()).getBody();
        JSONObject object = JSON.parseObject(response);
        if (object.getIntValue("errcode") != 0) {
            throw new AuthException(object.getString("errmsg"));
        }
        object = object.getJSONObject("user_info");
        AuthToken token = AuthToken.builder()
                .openId(object.getString("openid"))
                .unionId(object.getString("unionid"))
                .build();
        return AuthUser.builder()
                .rawUserInfo(object)
                .uuid(object.getString("unionid"))
                .nickname(object.getString("nick"))
                .username(object.getString("nick"))
                .gender(AuthUserGender.UNKNOWN)
                .source(source.toString())
                .token(token)
                .build();
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     * @since 1.9.3
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .queryParam("response_type", "code")
                .queryParam("appid", config.getClientId())
                .queryParam("scope", "snsapi_login")
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("state", getRealState(state))
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param authToken 用户授权后的token
     * @return 返回获取userInfo的url
     */
    @Override
    protected String userInfoUrl(AuthToken authToken) {
        // 根据timestamp, appSecret计算签名值
        String timestamp = System.currentTimeMillis() + "";
        String urlEncodeSignature = GlobalAuthUtils.generateDingTalkSignature(config.getClientSecret(), timestamp);

        return UrlBuilder.fromBaseUrl(source.userInfo())
                .queryParam("signature", urlEncodeSignature)
                .queryParam("timestamp", timestamp)
                .queryParam("accessKey", config.getClientId())
                .build();
    }

}
