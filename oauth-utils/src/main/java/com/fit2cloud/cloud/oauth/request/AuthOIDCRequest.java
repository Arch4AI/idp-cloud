package com.fit2cloud.cloud.oauth.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.cloud.http.constants.Constants;
import com.fit2cloud.cloud.http.support.HttpHeader;
import com.fit2cloud.cloud.http.support.SimpleHttpResponse;
import com.fit2cloud.cloud.oauth.cache.AuthStateCache;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;
import com.fit2cloud.cloud.oauth.enums.AuthResponseStatus;
import com.fit2cloud.cloud.oauth.enums.scope.OIDCScope;
import com.fit2cloud.cloud.oauth.exception.AuthException;
import com.fit2cloud.cloud.oauth.model.AuthCallback;
import com.fit2cloud.cloud.oauth.model.AuthToken;
import com.fit2cloud.cloud.oauth.model.AuthUser;
import com.fit2cloud.cloud.oauth.utils.AuthScopeUtils;
import com.fit2cloud.cloud.oauth.utils.HttpUtils;
import com.fit2cloud.cloud.oauth.utils.UrlBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支持OIDC协议的链接
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 2:29 PM
**/
public class AuthOIDCRequest extends AuthDefaultRequest {

    public AuthOIDCRequest(AuthConfig config) {
        super(config, AuthDefaultSource.OIDC);
    }

    public AuthOIDCRequest(AuthConfig config, AuthStateCache authStateCache) {
        super(config, AuthDefaultSource.OIDC, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        Map<String, String> requetMap = new LinkedHashMap<>();
        //接口参数
        requetMap.put("code", authCallback.getCode());
        requetMap.put("client_id", config.getClientId());
        requetMap.put("client_secret", config.getClientSecret());
        requetMap.put("grant_type", "authorization_code");
        requetMap.put("redirect_uri", config.getRedirectUri());
        HttpHeader header = new HttpHeader();
        header.add(Constants.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
        SimpleHttpResponse response = new HttpUtils(config.getHttpConfig()).post(config.getTokenUri(),requetMap,header,false).getHttpResponse();
        if(response.getCode() == 200){
            JSONObject jsonObject = JSON.parseObject(response.getBody());
            return AuthToken.builder()
                    .accessToken(jsonObject.getString("access_token"))
                    .tokenType(jsonObject.getString("token_type"))
                    .expireIn(jsonObject.getInteger("expires_in"))
                    .refreshToken(jsonObject.getString("refresh_token"))
                    .refreshTokenExpireIn(jsonObject.getInteger("refresh_expires_in"))
                    .idToken(jsonObject.getString("id_token"))
                    .scope(jsonObject.getString("scope"))
                    .build();
        }else{
            throw new AuthException(AuthResponseStatus.ILLEGAL_TOKEN);
        }
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        //获取用户信息
        HttpHeader header = new HttpHeader();
        header.add("Authorization","Bearer " + authToken.getAccessToken());
        SimpleHttpResponse response = new HttpUtils(config.getHttpConfig()).get(config.getUserInfoUri(),null,header,false).getHttpResponse();
        if(response.getCode() == 200){
            JSONObject jsonObject = JSONObject.parseObject(response.getBody());
            return AuthUser.builder()
                    .username(jsonObject.getString("preferred_username"))
                    .email(jsonObject.getString("email"))
                    .nickname(jsonObject.getString("name"))
                    .token(authToken)
                    .source(this.source.toString())
                    .uuid(jsonObject.getString("sub"))
                    .rawUserInfo(jsonObject)
                    .build();
        }else {
            throw new AuthException(AuthResponseStatus.ILLEGAL_TOKEN);
        }
    }

    /**
     * 检查响应内容是否正确
     *
     * @param object 请求响应内容
     */
    private void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new AuthException(object.getString("error_description"));
        }
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     */
    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(config.getAuthUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("scope", this.getScopes("+", false, AuthScopeUtils.getDefaultScopes(
                        OIDCScope.values())))
                .queryParam("state", getRealState(state))
                .build();
    }
}
