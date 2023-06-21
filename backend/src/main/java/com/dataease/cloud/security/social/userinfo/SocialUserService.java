package com.dataease.cloud.security.social.userinfo;

import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.security.social.constant.ClientType;
import com.dataease.cloud.security.social.constant.SocialConstant;
import com.dataease.cloud.utils.JsonUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Custom OAuth2 User Service
 * <p>Need support ALL client Type</p>
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/9 6:23 PM
 **/
public class SocialUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

    private static final String SOCIAL_PROVIDER_NOT_FOUND_CODE = "social_provider_not_found_code";

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();

    private final CloudSocialSettingMapper settingMapper;

    private RestOperations restOperations;

    public SocialUserService(CloudSocialSettingMapper settingMapper) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
        this.settingMapper = settingMapper;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Assert.notNull(userRequest, "userRequest cannot be null");
        if (!StringUtils
                .hasText(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                    "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (!StringUtils.hasText(userNameAttributeName)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        CloudSocialSetting setting = settingMapper.selectByPrimaryKey(userRequest.getClientRegistration().getRegistrationId());
        if (setting == null || !setting.getEnable()) {
            OAuth2Error oauth2Error = new OAuth2Error(SOCIAL_PROVIDER_NOT_FOUND_CODE,
                    "Social Provider has not found or setting disabled: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // 根据类型来处理 这里最好也包含OIDC的哦
        ClientType clientType = ClientType.valueOf(setting.getClientType());
        Map<String, Object> userAttributes = new HashMap<>();
        switch (clientType) {
            //定义不同的类型获取不同的信息
            case wechatwork_self_app -> {
                userAttributes = wechatworkSelfAppUserAttributes(userRequest);
                break;
            }
            case dingtalk -> {
                userAttributes = dingtalkUserAttributes(userRequest);
                break;
            }
            case feishu_self_app -> {
                userAttributes = feishuUserAttributes(userRequest);
                break;
            }
            case wechatwork_external_app -> {
                userAttributes = wechatworkExternalAppUserAttributes(userRequest);
                break;
            }
            case wechat -> {
                userAttributes = wechatUserAttributes(userRequest);
                break;
            }
            case qq -> {
                userAttributes = qqUserAttributes(userRequest);
                break;
            }
            case baidu -> {
                userAttributes = baiduUserAttributes(userRequest);
                break;
            }
            case weibo -> {
                userAttributes = weiboUserAttributes(userRequest);
                break;
            }
            default -> {
                RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
                ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);
                userAttributes = response.getBody();
            }
        }
        if (userAttributes.isEmpty()) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "Social provider get user response for client type:" + setting.getClientType() + " has some error!",
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new OAuth2UserAuthority(userAttributes));
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String authority : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
        }
        return new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
    }

    /**
     * 获取微博的用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> weiboUserAttributes(OAuth2UserRequest userRequest) {
        String uid = (String) userRequest.getAdditionalParameters().get(SocialConstant.SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR);
        String uri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                .queryParam("uid", uid)
                .toUriString();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> userMap = responseEntity.getBody();
            return userMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 获取百度的用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> baiduUserAttributes(OAuth2UserRequest userRequest) {
        String uri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                .build()
                .toUriString();
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, null, Map.class);
            Map<String, Object> userMap = responseEntity.getBody();
            if (userMap.containsKey("error_msg")) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                        "An error occurred while attempting to retrieve the UserInfo Resource: " + String.valueOf(userMap.get("error_msg")), null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
            return userMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 获取QQ的用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> qqUserAttributes(OAuth2UserRequest userRequest) {
        String uri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                .queryParam("fmt", "json")
                .build()
                .toUriString();
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, null, Map.class);
            return responseEntity.getBody();
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * //TODO 具体需要测试 这个地方返回可能有问题
     * 微信开发平台获取用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> wechatUserAttributes(OAuth2UserRequest userRequest) {
        String openid = (String) userRequest.getAdditionalParameters().get(SocialConstant.SOCIAL_ACCESSTOKEN_USER_UNIONID_ATTR);
        String uri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                .queryParam("openid", openid)
                .queryParam("lang", "zh_CN")
                .build()
                .toUriString();
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, new HttpEntity<>(null), Map.class);
            Map<String, Object> userMap = responseEntity.getBody();
            if (userMap.containsKey("errcode")) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                        "An error occurred while attempting to retrieve the UserInfo Resource: " + String.valueOf(userMap.get("errmsg")), null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
            return userMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 获取企业微信外部应用的用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> wechatworkExternalAppUserAttributes(OAuth2UserRequest userRequest) {
        OAuth2AuthorizationResponse oAuth2AuthorizationResponse = (OAuth2AuthorizationResponse) userRequest.getAdditionalParameters().get(SocialConstant.AUTHORIZATION_RESPONSE_KEY);
        //企业微信获取用户信息
        String uri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam("suite_access_token", userRequest.getAccessToken().getTokenValue())
                .queryParam(OAuth2ParameterNames.CODE, oAuth2AuthorizationResponse.getCode())
                .build()
                .toUriString();
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, new HttpEntity<>(null), Map.class);
            Map<String, Object> userMap = responseEntity.getBody();
            if (userMap.containsKey("errcode") && Integer.valueOf(userMap.get("errcode") + "") != 0) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                        "An error occurred while attempting to retrieve the UserInfo Resource: " + String.valueOf(userMap.get("errmsg")), null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
            return userMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 获取飞书的用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> feishuUserAttributes(OAuth2UserRequest userRequest) {
        OAuth2AuthorizationResponse oAuth2AuthorizationResponse = (OAuth2AuthorizationResponse) userRequest.getAdditionalParameters().get(SocialConstant.AUTHORIZATION_RESPONSE_KEY);
        String uri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        Map<String, String> rawBody = new HashMap<>() {{
            put(OAuth2ParameterNames.GRANT_TYPE, userRequest.getClientRegistration().getAuthorizationGrantType().getValue());
            put(OAuth2ParameterNames.CODE, oAuth2AuthorizationResponse.getCode());
        }};
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        HttpEntity httpEntity = new HttpEntity(JsonUtils.toJsonString(rawBody), headers);
        try {
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.POST, httpEntity, Map.class);
            Map<String, Object> userMap = responseEntity.getBody();
            if (userMap.containsKey("code") && Integer.valueOf(userMap.get("code") + "") != 0) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                        "An error occurred while attempting to retrieve the UserInfo Resource: " + String.valueOf(userMap.get("msg")), null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
            return userMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 获取钉钉的用户
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> dingtalkUserAttributes(OAuth2UserRequest userRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set("x-acs-dingtalk-access-token", userRequest.getAccessToken().getTokenValue());
        HttpEntity httpEntity = new HttpEntity(headers);
        try {
            String uri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
            ResponseEntity<Map> responseEntity = this.restOperations.exchange(uri, HttpMethod.GET, httpEntity, Map.class);
            return responseEntity.getBody();
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    /**
     * 企业微信自建应用获取用户信息
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> wechatworkSelfAppUserAttributes(OAuth2UserRequest userRequest) {
        OAuth2AuthorizationResponse oAuth2AuthorizationResponse = (OAuth2AuthorizationResponse) userRequest.getAdditionalParameters().get(SocialConstant.AUTHORIZATION_RESPONSE_KEY);
        String userUri = UriComponentsBuilder.fromUriString(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())
                .queryParam(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue())
                .queryParam(OAuth2ParameterNames.CODE, oAuth2AuthorizationResponse.getCode())
                .build()
                .toUriString();
        HttpEntity httpEntity = new HttpEntity(null);
        try {
            ResponseEntity<Map> response = this.restOperations.exchange(userUri, HttpMethod.GET, httpEntity, Map.class);
            Map<String, Object> resMap = response.getBody();
            if (resMap.containsKey("errcode") && !String.valueOf(resMap.get("errcode")).equals("0")) {
                OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                        "An error occurred while attempting to retrieve the UserInfo Resource: " + String.valueOf(resMap.get("errmsg")), null);
                throw new OAuth2AuthenticationException(oauth2Error);
            }
            return resMap;
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    private ResponseEntity<Map<String, Object>> getResponse(OAuth2UserRequest userRequest, RequestEntity<?> request) {
        try {
            return this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
        } catch (OAuth2AuthorizationException ex) {
            throw throwOAuthError(userRequest, ex);
        } catch (UnknownContentTypeException ex) {
            throw unknowError(userRequest, ex);
        } catch (RestClientException ex) {
            throw restError(ex);
        }
    }

    private OAuth2AuthenticationException restError(RestClientException ex) {
        OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
        return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
    }


    private OAuth2AuthenticationException unknowError(OAuth2UserRequest userRequest, UnknownContentTypeException ex) {
        String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
                + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()
                + "': response contains invalid content type '" + ex.getContentType().toString() + "'. "
                + "The UserInfo Response should return a JSON object (content type 'application/json') "
                + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                + userRequest.getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, "
                + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
        OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
        return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
    }

    private OAuth2AuthenticationException throwOAuthError(OAuth2UserRequest userRequest, OAuth2AuthorizationException ex) {
        OAuth2Error oauth2Error = ex.getError();
        StringBuilder errorDetails = new StringBuilder();
        errorDetails.append("Error details: [");
        errorDetails.append("UserInfo Uri: ")
                .append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
        errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
        if (oauth2Error.getDescription() != null) {
            errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
        }
        errorDetails.append("]");
        oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails.toString(),
                null);
        return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
    }

    /**
     * Sets the {@link Converter} used for converting the {@link OAuth2UserRequest} to a
     * {@link RequestEntity} representation of the UserInfo Request.
     *
     * @param requestEntityConverter the {@link Converter} used for converting to a
     *                               {@link RequestEntity} representation of the UserInfo Request
     * @since 5.1
     */
    public final void setRequestEntityConverter(Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter) {
        Assert.notNull(requestEntityConverter, "requestEntityConverter cannot be null");
        this.requestEntityConverter = requestEntityConverter;
    }

    /**
     * Sets the {@link RestOperations} used when requesting the UserInfo resource.
     *
     * <p>
     * <b>NOTE:</b> At a minimum, the supplied {@code restOperations} must be configured
     * with the following:
     * <ol>
     * <li>{@link ResponseErrorHandler} - {@link OAuth2ErrorResponseErrorHandler}</li>
     * </ol>
     *
     * @param restOperations the {@link RestOperations} used when requesting the UserInfo
     *                       resource
     * @since 5.1
     */
    public final void setRestOperations(RestOperations restOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.restOperations = restOperations;
    }
}
