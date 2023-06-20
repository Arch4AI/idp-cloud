package com.dataease.cloud.security.social.resolver;

import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.base.domain.CloudSocialSetting;
import com.dataease.cloud.base.mapper.CloudSocialSettingMapper;
import com.dataease.cloud.security.social.constant.ClientType;
import com.dataease.cloud.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Consumer;

/**
 * custom defind request resolver
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/1 6:11 PM
 * //todo 所有的认证信息都是从cloud_oauth2_client_setting auth_secret获取 这里会存在一个问题 就是只能转Map去获取 无法获取到具体的信息
 **/
@Slf4j
public class SocialAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

    private static final char PATH_DELIMITER = '/';

    private static final StringKeyGenerator DEFAULT_SECURE_KEY_GENERATOR = new Base64StringKeyGenerator(
            Base64.getUrlEncoder().withoutPadding(), 96);

    private static final Consumer<OAuth2AuthorizationRequest.Builder> DEFAULT_PKCE_APPLIER = OAuth2AuthorizationRequestCustomizers
            .withPkce();

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final CloudSocialSettingMapper settingMapper;

    private final AntPathRequestMatcher authorizationRequestMatcher;

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer = (customizer) -> {
    };

    /**
     * Constructs a {@code DefaultOAuth2AuthorizationRequestResolver} using the provided
     * parameters.
     * @param clientRegistrationRepository the repository of client registrations
     * @param authorizationRequestBaseUri the base {@code URI} used for resolving
     * authorization requests
     */
    public SocialAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                              CloudSocialSettingMapper settingMapper,
                                              String authorizationRequestBaseUri) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.settingMapper = settingMapper;
        this.authorizationRequestMatcher = new AntPathRequestMatcher(
                authorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        String registrationId = resolveRegistrationId(request);
        if (registrationId == null) {
            return null;
        }
        String redirectUriAction = getAction(request, "login");
        return resolve(request, registrationId, redirectUriAction);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
        if (registrationId == null) {
            return null;
        }
        String redirectUriAction = getAction(request, "authorize");
        return resolve(request, registrationId, redirectUriAction);
    }

    /**
     * Sets the {@code Consumer} to be provided the
     * {@link OAuth2AuthorizationRequest.Builder} allowing for further customizations.
     * @param authorizationRequestCustomizer the {@code Consumer} to be provided the
     * {@link OAuth2AuthorizationRequest.Builder}
     * @since 5.3
     * @see OAuth2AuthorizationRequestCustomizers
     */
    public void setAuthorizationRequestCustomizer(
            Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer) {
        Assert.notNull(authorizationRequestCustomizer, "authorizationRequestCustomizer cannot be null");
        this.authorizationRequestCustomizer = authorizationRequestCustomizer;
    }

    private String getAction(HttpServletRequest request, String defaultAction) {
        String action = request.getParameter("action");
        if (action == null) {
            return defaultAction;
        }
        return action;
    }

    private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId,
                                               String redirectUriAction) {
        if (registrationId == null) {
            return null;
        }
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
        }
        OAuth2AuthorizationRequest.Builder builder = getBuilder(clientRegistration);

        String redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction);
        // @formatter:off
        var state = IdUtil.randomUUID();
        builder.clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(redirectUriStr);
        // @formatter:on

        this.authorizationRequestCustomizer.accept(builder);

        customizer(registrationId, builder);
        return builder.build();
    }

    /**
     * 自定义处理了一下需要修改的重定向信息
     * @param registrationId
     * @param builder
     */
    private void customizer(String registrationId, OAuth2AuthorizationRequest.Builder builder) {
        //出来一下不同的类型获取到的信息
        CloudSocialSetting setting = settingMapper.selectByPrimaryKey(registrationId);
        String settingClientType = setting.getClientType();
        ClientType clientType = ClientType.valueOf(settingClientType.toLowerCase());
        if(clientType == null){
            throw new IllegalArgumentException("Not support client type:" + settingClientType);
        }
        String authorizationRequestUri = "";
        //自定义状态码
        var state = IdUtil.randomUUID();
        builder.state(state);
        OAuth2AuthorizationRequest authorizationRequest = builder.build();
        String separator = StringUtils.hasText(setting.getScopeSeparator()) ? setting.getScopeSeparator() : " ";
        switch (clientType){
            case dingtalk -> {
                // 或者直接加这个
                builder.additionalParameters(new HashMap<>() {{
                    put("prompt", "consent");
                    put(OAuth2ParameterNames.SCOPE, "openid");
                }});
                break;
            }
            case qq -> {
                Set<String> scopes = new LinkedHashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(setting.getScopes(),",")));
                authorizationRequestUri = UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationUri())
                        .queryParam(OAuth2ParameterNames.RESPONSE_TYPE, authorizationRequest.getResponseType().getValue())
                        .queryParam(OAuth2ParameterNames.CLIENT_ID, encodeQueryParam(authorizationRequest.getClientId()))
                        .queryParam(OAuth2ParameterNames.REDIRECT_URI, encodeQueryParam(authorizationRequest.getRedirectUri()))
                        .queryParam(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(scopes, separator))
                        .queryParam(OAuth2ParameterNames.STATE, encodeQueryParam(state))
                        .build()
                        .toUriString();
                break;
            }
            case wechat -> {
                Set<String> scopes = new LinkedHashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(setting.getScopes(),",")));
                authorizationRequestUri = UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationUri())
                        .queryParam("appid", encodeQueryParam(authorizationRequest.getClientId()))
                        .queryParam(OAuth2ParameterNames.REDIRECT_URI, encodeQueryParam(authorizationRequest.getRedirectUri()))
                        .queryParam(OAuth2ParameterNames.RESPONSE_TYPE, authorizationRequest.getResponseType().getValue())
                        .queryParam(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(scopes, separator))//应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
                        .queryParam(OAuth2ParameterNames.STATE, encodeQueryParam(state))
                        .fragment("wechat_redirect")
                        .build()
                        .toUriString();
                break;
            }
            case feishu_self_app -> {
                authorizationRequestUri = UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationUri())
                        .queryParam(OAuth2ParameterNames.REDIRECT_URI, encodeQueryParam(authorizationRequest.getRedirectUri()))
                        .queryParam("app_id", encodeQueryParam(authorizationRequest.getClientId()))
                        .queryParam(OAuth2ParameterNames.STATE, encodeQueryParam(state))
                        .build()
                        .toUriString();
                break;
            }
            case wechatwork_self_app -> {
                // 这里有问题 不知道如何弄比较好 因为这里是在一个json字符串中
                Map<String,String> settingMap = JsonUtils.jsonToObject(setting.getAuthSecret(),Map.class);
                authorizationRequestUri = UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationUri())
                        .queryParam("login_type", encodeQueryParam("CorpApp"))
                        .queryParam("appid", encodeQueryParam(settingMap.get("corpId")))
                        .queryParam("agentid", encodeQueryParam(authorizationRequest.getClientId()))
                        .queryParam(OAuth2ParameterNames.REDIRECT_URI, encodeQueryParam(authorizationRequest.getRedirectUri()))
                        .queryParam(OAuth2ParameterNames.STATE, encodeQueryParam(state))
                        .build()
                        .toUriString();
                break;
            }
            case oidc, oauth2, baidu, weibo -> {
                // 添加作用域 这里是作用域的效果
                builder.parameters(stringObjectMap -> {
                    //如果没有设置则用默认的空格 否则用自己的
                    Set<String> scopes = new LinkedHashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(setting.getScopes(),",")));
                    stringObjectMap.put(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(scopes, separator));
                });
                break;
            }
            case wechatwork_external_app -> {
                authorizationRequestUri = UriComponentsBuilder.fromUriString(authorizationRequest.getAuthorizationUri())
                        .queryParam("login_type", encodeQueryParam("ServiceApp"))
                        // 这个是应用的登录授权 SuiteID 默认放到clientId
                        .queryParam("appid", encodeQueryParam(authorizationRequest.getClientId()))
                        .queryParam(OAuth2ParameterNames.REDIRECT_URI, encodeQueryParam(authorizationRequest.getRedirectUri()))
                        .queryParam(OAuth2ParameterNames.STATE, encodeQueryParam(state))
                        .build()
                        .toUriString();
                break;
            }
            default -> {
                log.info("no thing to do");
            }
        }
        //获取这个
        builder.authorizationRequestUri(authorizationRequestUri);
    }

    private static String encodeQueryParam(String value) {
        return UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8);
    }

    private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
            // @formatter:off
            OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
                    .attributes((attrs) ->
                            attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
            // @formatter:on
            if (!CollectionUtils.isEmpty(clientRegistration.getScopes())
                    && clientRegistration.getScopes().contains(OidcScopes.OPENID)) {
                applyNonce(builder);
            }
            if (ClientAuthenticationMethod.NONE.equals(clientRegistration.getClientAuthenticationMethod())) {
                DEFAULT_PKCE_APPLIER.accept(builder);
            }
            return builder;
        }
        throw new IllegalArgumentException(
                "Invalid Authorization Grant Type (" + clientRegistration.getAuthorizationGrantType().getValue()
                        + ") for Client Registration with Id: " + clientRegistration.getRegistrationId());
    }

    private String resolveRegistrationId(HttpServletRequest request) {
        if (this.authorizationRequestMatcher.matches(request)) {
            return this.authorizationRequestMatcher.matcher(request).getVariables()
                    .get(REGISTRATION_ID_URI_VARIABLE_NAME);
        }
        return null;
    }

    /**
     * Expands the {@link ClientRegistration#getRedirectUri()} with following provided
     * variables:<br/>
     * - baseUrl (e.g. https://localhost/app) <br/>
     * - baseScheme (e.g. https) <br/>
     * - baseHost (e.g. localhost) <br/>
     * - basePort (e.g. :8080) <br/>
     * - basePath (e.g. /app) <br/>
     * - registrationId (e.g. google) <br/>
     * - action (e.g. login) <br/>
     * <p/>
     * Null variables are provided as empty strings.
     * <p/>
     * Default redirectUri is:
     * {@code org.springframework.security.config.oauth2.client.CommonOAuth2Provider#DEFAULT_REDIRECT_URL}
     * @return expanded URI
     */
    private static String expandRedirectUri(HttpServletRequest request, ClientRegistration clientRegistration,
                                            String action) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("registrationId", clientRegistration.getRegistrationId());
        // @formatter:off
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();
        // @formatter:on
        String scheme = uriComponents.getScheme();
        uriVariables.put("baseScheme", (scheme != null) ? scheme : "");
        String host = uriComponents.getHost();
        uriVariables.put("baseHost", (host != null) ? host : "");
        // following logic is based on HierarchicalUriComponents#toUriString()
        int port = uriComponents.getPort();
        uriVariables.put("basePort", (port == -1) ? "" : ":" + port);
        String path = uriComponents.getPath();
        if (StringUtils.hasLength(path)) {
            if (path.charAt(0) != PATH_DELIMITER) {
                path = PATH_DELIMITER + path;
            }
        }
        uriVariables.put("basePath", (path != null) ? path : "");
        uriVariables.put("baseUrl", uriComponents.toUriString());
        uriVariables.put("action", (action != null) ? action : "");
        return UriComponentsBuilder.fromUriString(clientRegistration.getRedirectUri()).buildAndExpand(uriVariables)
                .toUriString();
    }

    /**
     * Creates nonce and its hash for use in OpenID Connect 1.0 Authentication Requests.
     * @param builder where the {@link OidcParameterNames#NONCE} and hash is stored for
     * the authentication request
     *
     * @since 5.2
     * @see <a target="_blank" href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest">3.1.2.1.
     * Authentication Request</a>
     */
    private static void applyNonce(OAuth2AuthorizationRequest.Builder builder) {
        try {
            String nonce = DEFAULT_SECURE_KEY_GENERATOR.generateKey();
            String nonceHash = createHash(nonce);
            builder.attributes((attrs) -> attrs.put(OidcParameterNames.NONCE, nonce));
            builder.additionalParameters((params) -> params.put(OidcParameterNames.NONCE, nonceHash));
        }
        catch (NoSuchAlgorithmException ex) {
        }
    }

    private static String createHash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
