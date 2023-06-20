package com.dataease.cloud.security.social.endpoint;

import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.security.social.constant.ClientType;
import com.dataease.cloud.security.social.constant.SocialConstant;
import com.dataease.cloud.security.social.secret.impl.WechatworkExternalSecret;
import com.dataease.cloud.security.social.secret.impl.WechatworkSelfSecret;
import com.dataease.cloud.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.dataease.cloud.security.social.constant.SocialConstant.AUTHORIZATION_RESPONSE_KEY;
import static com.dataease.cloud.security.social.constant.SocialConstant.SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR;

/**
 * custom return token uri response
 * //TODO 企业微信外部应用的逻辑并不完整 需要补充通过回调获取信息
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/5 9:50 AM
 **/
@Slf4j
public final class SocialAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private static final String INVALID_TOKEN_RESPONSE_ERROR_CODE = "invalid_token_response";

    private final CloudSocialSettingMapper settingMapper;

    private Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> requestEntityConverter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

    private RestOperations restOperations;

    private final RestTemplate restTemplate = new RestTemplate();

    // set rest operations for spring rest template
    public SocialAuthorizationCodeTokenResponseClient(CloudSocialSettingMapper settingMapper) {
        RestTemplate restTemplate = new RestTemplate(
                Arrays.asList(new FormHttpMessageConverter(), new OAuth2AccessTokenResponseHttpMessageConverter()));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
        this.settingMapper = settingMapper;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        Assert.notNull(authorizationCodeGrantRequest, "authorizationCodeGrantRequest cannot be null");
        // 获取到注册的客户端ID
        String clientRegistrationId = authorizationCodeGrantRequest.getClientRegistration().getRegistrationId();
        CloudSocialSetting cloudOauth2ClientSetting = settingMapper.selectByPrimaryKey(clientRegistrationId);
        ClientType clientType = ClientType.valueOf(cloudOauth2ClientSetting.getClientType());
        switch (clientType) {
            case wechatwork_self_app -> {
                return getAccessTokenByWechatworkSelfApp(authorizationCodeGrantRequest, cloudOauth2ClientSetting);
            }
            case dingtalk -> {
                return getAccessTokenResponseByDingtalk(authorizationCodeGrantRequest);
            }
            case feishu_self_app -> {
                return getAccessTokenResponseByFeishu(authorizationCodeGrantRequest);
            }
            case wechatwork_external_app -> {
                return getAccessTokenByWechatworkExternalApp(authorizationCodeGrantRequest, cloudOauth2ClientSetting);
            }
            case wechat -> {
                return getAccessTokenByWechat(authorizationCodeGrantRequest);
            }
            case qq -> {
                return getAccessTokenByQQ(authorizationCodeGrantRequest);
            }
            case baidu -> {
                return getAccessTokenByBaidu(authorizationCodeGrantRequest);
            }
            case weibo -> {
                return getAccessTokenByWeibo(authorizationCodeGrantRequest);
            }
            default -> {
                return defaultGetToken(authorizationCodeGrantRequest);
            }
        }
    }

    /**
     * get weibo access token
     * //TODO 注：weibo返回的也时openid
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenByWeibo(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        RequestEntity<?> request = this.requestEntityConverter.convert(grantRequest);
        ResponseEntity<String> response = restOperations.exchange(request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> resMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            String[] scopes = StringUtils.delimitedListToStringArray(String.valueOf(resMap.get(OAuth2ParameterNames.SCOPE)), " ");
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(String.valueOf(resMap.get(OAuth2ParameterNames.ACCESS_TOKEN)))
                    .expiresIn(Integer.valueOf(resMap.get(OAuth2ParameterNames.EXPIRES_IN) + ""))
                    .scopes(new LinkedHashSet<>(Arrays.asList(scopes)))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .additionalParameters(new HashMap<>() {{
                        put(SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR, String.valueOf(resMap.get("uid")));
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * get baidu access token
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenByBaidu(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        ClientRegistration clientRegistration = grantRequest.getClientRegistration();
        String tokenUri = UriComponentsBuilder.fromUriString(grantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                .queryParam(OAuth2ParameterNames.GRANT_TYPE, grantRequest.getGrantType().getValue())
                .queryParam(OAuth2ParameterNames.CODE, grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode())
                .queryParam(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId())
                .queryParam(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret())
                .queryParam(OAuth2ParameterNames.REDIRECT_URI, grantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri())
                .build()
                .toUriString();
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.GET, new HttpEntity<>(null), String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> resMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            if (resMap.containsKey("error_description")) {
                OAuth2Error oAuth2Error = new OAuth2Error(resMap.get("error_description") + "");
                throw new OAuth2AuthorizationException(oAuth2Error);
            }
            String[] scopes = StringUtils.delimitedListToStringArray(String.valueOf(resMap.get(OAuth2ParameterNames.SCOPE)), " ");
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(String.valueOf(resMap.get(OAuth2ParameterNames.ACCESS_TOKEN)))
                    .expiresIn(Integer.valueOf(resMap.get(OAuth2ParameterNames.EXPIRES_IN) + ""))
                    .refreshToken(String.valueOf(resMap.get(OAuth2ParameterNames.REFRESH_TOKEN)))
                    .scopes(new LinkedHashSet<>(Arrays.asList(scopes)))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * get qq access token
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenByQQ(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        ClientRegistration clientRegistration = grantRequest.getClientRegistration();
        String tokenUri = UriComponentsBuilder.fromUriString(grantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                .queryParam(OAuth2ParameterNames.GRANT_TYPE, grantRequest.getGrantType().getValue())
                .queryParam(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId())
                .queryParam(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret())
                .queryParam(OAuth2ParameterNames.REDIRECT_URI, grantRequest.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri())
                .queryParam(OAuth2ParameterNames.CODE, grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode())
                .queryParam("fmt", "json")
                .build()
                .toUriString();
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.GET, new HttpEntity<>(null), String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, String> resMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(String.valueOf(resMap.get(OAuth2ParameterNames.ACCESS_TOKEN)))
                    .expiresIn(Integer.valueOf(resMap.get(OAuth2ParameterNames.EXPIRES_IN)))
                    .refreshToken(String.valueOf(resMap.get(OAuth2ParameterNames.REFRESH_TOKEN)))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * Get wechat open access token
     * //TODO do not test pelase test in your wechat setting in network
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenByWechat(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        ClientRegistration clientRegistration = grantRequest.getClientRegistration();
        String tokenUri = UriComponentsBuilder.fromUriString(grantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                .queryParam("appid", clientRegistration.getClientId())
                .queryParam("secret", clientRegistration.getClientSecret())
                .queryParam(OAuth2ParameterNames.CODE, grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode())
                .queryParam(OAuth2ParameterNames.GRANT_TYPE, grantRequest.getGrantType().getValue())
                .build()
                .toUriString();
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.GET, new HttpEntity<>(null), String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> resMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            if (resMap.containsKey("errcode")) {
                OAuth2Error oAuth2Error = new OAuth2Error(String.valueOf(resMap.get("errmsg")));
                throw new OAuth2AuthorizationException(oAuth2Error);
            }
            Integer expiresIn = (Integer) resMap.get(OAuth2ParameterNames.EXPIRES_IN);
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(String.valueOf(resMap.get(OAuth2ParameterNames.ACCESS_TOKEN)))
                    .expiresIn(expiresIn)
                    .refreshToken(String.valueOf(resMap.get(OAuth2ParameterNames.REFRESH_TOKEN)))
                    .scopes(StringUtils.commaDelimitedListToSet(String.valueOf(resMap.get(OAuth2ParameterNames.SCOPE))))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .additionalParameters(new HashMap<>() {{
                        put(SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR, String.valueOf(resMap.get("openid")));
                        if (resMap.containsKey("unionid")) {
                            put(SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR, String.valueOf(resMap.get("unionid")));
                        }
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * get wechat work third App Access Token
     * //TODO 需要建立一个回调的机制去获取accessToken 企业微信规定不能在一段时间内多次获取accessToken 以免浪费系统开销
     *
     * @param grantRequest
     * @param setting
     * @return
     * @see <a href="https://developer.work.weixin.qq.com/document/path/98170">help page</a>
     */
    private OAuth2AccessTokenResponse getAccessTokenByWechatworkExternalApp(OAuth2AuthorizationCodeGrantRequest grantRequest, CloudSocialSetting setting) {
        String ticket = "";// 企业微信的票据
        WechatworkExternalSecret secret = JsonUtils.jsonToObject(setting.getAuthSecret(), WechatworkExternalSecret.class);
        Map<String, String> bodyRaw = new HashMap<>() {{
            put("suite_id", grantRequest.getClientRegistration().getClientId());
            put("suite_secret", grantRequest.getClientRegistration().getClientSecret());
            put("suite_ticket", ticket);
        }};
        HttpEntity httpEntity = new HttpEntity(JsonUtils.toJsonString(bodyRaw));
        ResponseEntity<String> responseEntity = restTemplate.exchange(grantRequest.getClientRegistration().getProviderDetails().getTokenUri(), HttpMethod.POST, httpEntity, String.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> stringObjectMap = JsonUtils.jsonToObject(responseEntity.getBody(), Map.class);
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(String.valueOf(stringObjectMap.get("suite_access_token")))
                    .expiresIn(Integer.valueOf(stringObjectMap.get("expires_in") + ""))
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    //将返回的信息传递到下一步去
                    .additionalParameters(new HashMap<>() {{
                        put(AUTHORIZATION_RESPONSE_KEY, grantRequest.getAuthorizationExchange().getAuthorizationResponse());
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(responseEntity);
        }
        return null;
    }

    /**
     * get feishu self App access Token
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenResponseByFeishu(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        Map<String, String> bodyRaw = new HashMap<>() {{
            put("app_id", grantRequest.getClientRegistration().getClientId());
            put("app_secret", grantRequest.getClientRegistration().getClientSecret());
        }};
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity httpEntity = new HttpEntity(JsonUtils.toJsonString(bodyRaw), headers);
        ResponseEntity<String> response = restTemplate.exchange(grantRequest.getClientRegistration().getProviderDetails().getTokenUri(), HttpMethod.POST, httpEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> appAccessTokenMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            Integer code = Integer.valueOf(appAccessTokenMap.get("code") + "");
            if (code != 0) {
                OAuth2Error oAuth2Error = new OAuth2Error(appAccessTokenMap.get("msg") + "");
                throw new OAuth2AuthorizationException(oAuth2Error);
            }
            String app_access_token = String.valueOf(appAccessTokenMap.get("app_access_token"));
            Integer expireIn = Integer.valueOf(appAccessTokenMap.get("expire") + "");
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(app_access_token)
                    .expiresIn(expireIn)
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    //将返回的信息传递到下一步去
                    .additionalParameters(new HashMap<>() {{
                        put(SocialConstant.AUTHORIZATION_RESPONSE_KEY, grantRequest.getAuthorizationExchange().getAuthorizationResponse());
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * Get dingtalk accessToken
     *
     * @param grantRequest
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenResponseByDingtalk(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        Map<String, String> bodyRaw = new HashMap<>() {{
            put("clientId", grantRequest.getClientRegistration().getClientId());
            put("clientSecret", grantRequest.getClientRegistration().getClientSecret());
            put(OAuth2ParameterNames.CODE, grantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode());
            put("grantType", grantRequest.getClientRegistration().getAuthorizationGrantType().getValue());
        }};
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity httpEntity = new HttpEntity(JsonUtils.toJsonString(bodyRaw), headers);
        ResponseEntity<String> response = restTemplate.exchange(grantRequest.getClientRegistration().getProviderDetails().getTokenUri(), HttpMethod.POST, httpEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> tokenMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            String accessToken = String.valueOf(tokenMap.get("accessToken"));
            String refreshToken = String.valueOf(tokenMap.get("refreshToken"));
            Integer expireIn = Integer.valueOf(tokenMap.get("expireIn") + "");
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expireIn)
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    //将返回的信息传递到下一步去
                    .additionalParameters(new HashMap<>() {{
                        put(SocialConstant.AUTHORIZATION_RESPONSE_KEY, grantRequest.getAuthorizationExchange().getAuthorizationResponse());
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * Get wetchat Self App AccessToken
     *
     * @param grantRequest
     * @param setting
     * @return
     */
    private OAuth2AccessTokenResponse getAccessTokenByWechatworkSelfApp(OAuth2AuthorizationCodeGrantRequest grantRequest, CloudSocialSetting setting) {
        WechatworkSelfSecret selfSecret = JsonUtils.jsonToObject(setting.getAuthSecret(), WechatworkSelfSecret.class);
        String tokenUri = UriComponentsBuilder.fromUriString(grantRequest.getClientRegistration().getProviderDetails().getTokenUri())
                .queryParam("corpid", selfSecret.getCorpId())
                .queryParam("corpsecret", selfSecret.getClientSecret())
                .build()
                .toUriString();
        //暂时没有设置任何东西
        HttpEntity httpEntity = new HttpEntity(null);
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.GET, httpEntity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> tokenMap = JsonUtils.jsonToObject(response.getBody(), Map.class);
            Integer errcode = Integer.valueOf(tokenMap.get("errcode") + "");
            if (errcode != 0) {
                String errorMsg = String.valueOf(tokenMap.get("errmsg"));
                OAuth2Error oauth2Error = new OAuth2Error(errorMsg);
                throw new OAuth2AuthorizationException(oauth2Error);
            }
            String tokenVal = String.valueOf(tokenMap.get(OAuth2ParameterNames.ACCESS_TOKEN));
            Integer expires_in = Integer.valueOf(tokenMap.get(OAuth2ParameterNames.EXPIRES_IN) + "");
            OAuth2AccessTokenResponse oAuth2AccessTokenResponse = OAuth2AccessTokenResponse
                    .withToken(tokenVal)
                    .expiresIn(expires_in)
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    //将返回的信息传递到下一步去
                    .additionalParameters(new HashMap<>() {{
                        put(SocialConstant.AUTHORIZATION_RESPONSE_KEY, grantRequest.getAuthorizationExchange().getAuthorizationResponse());
                    }})
                    .build();
            return oAuth2AccessTokenResponse;
        } else {
            errorHandle(response);
        }
        return null;
    }

    /**
     * 错误处理
     *
     * @param response
     */
    private void errorHandle(ResponseEntity<String> response) {
        OAuth2Error oAuth2Error = new OAuth2Error(response.getBody());
        throw new OAuth2AuthorizationException(oAuth2Error);
    }

    /**
     * 默认获取AccessTokenResponse的信息
     *
     * @param authorizationCodeGrantRequest
     * @return
     */
    private OAuth2AccessTokenResponse defaultGetToken(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        RequestEntity<?> request = this.requestEntityConverter.convert(authorizationCodeGrantRequest);
        ResponseEntity<OAuth2AccessTokenResponse> response = getResponse(request);
        return response.getBody();
    }

    private ResponseEntity<OAuth2AccessTokenResponse> getResponse(RequestEntity<?> request) {
        try {
            return this.restOperations.exchange(request, OAuth2AccessTokenResponse.class);
        } catch (RestClientException ex) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_TOKEN_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the OAuth 2.0 Access Token Response: "
                            + ex.getMessage(),
                    null);
            throw new OAuth2AuthorizationException(oauth2Error, ex);
        }
    }

    /**
     * set token request boby converter
     *
     * @param requestEntityConverter
     */
    public void setRequestEntityConverter(
            Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> requestEntityConverter) {
        Assert.notNull(requestEntityConverter, "requestEntityConverter cannot be null");
        this.requestEntityConverter = requestEntityConverter;
    }

    public void setRestOperations(RestOperations restOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.restOperations = restOperations;
    }
}
