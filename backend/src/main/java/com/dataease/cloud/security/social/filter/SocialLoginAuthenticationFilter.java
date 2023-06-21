package com.dataease.cloud.security.social.filter;

import com.dataease.cloud.constant.LoginType;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.entity.response.SocialLoginResponse;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.social.SocialAuthenticationToken;
import com.dataease.cloud.security.social.utils.SocialAuthorizationResponseUtils;
import com.dataease.cloud.security.social.utils.UserUtils;
import com.dataease.cloud.service.SocialAuthenticationService;
import com.dataease.cloud.utils.CommonBeanFactory;
import com.dataease.cloud.utils.JsonUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 社交登录认证过滤器
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/2 4:14 PM
 **/
@Slf4j
public class SocialLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_FILTER_PROCESSES_URI = "/login/oauth2/code/*";

    private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";

    private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

    private static final String USER_UNIQUE_ATTR_NOT_FOUND = "user_unique_attr_not_found";

    private static final String SOCIAL_USER_SERVICE_NOT_FOUND = "social_user_service_not_found";

    private ClientRegistrationRepository clientRegistrationRepository;

    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    private Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

    /**
     * init some one
     *
     * @param clientRegistrationRepository
     * @param authorizedClientService
     */
    public SocialLoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizedClientService authorizedClientService) {
        this(clientRegistrationRepository, authorizedClientService, DEFAULT_FILTER_PROCESSES_URI);
    }

    /**
     * init some one
     *
     * @param clientRegistrationRepository
     * @param authorizedClientService
     * @param filterProcessesUrl
     */
    public SocialLoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizedClientService authorizedClientService, String filterProcessesUrl) {
        this(clientRegistrationRepository,
                new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService),
                filterProcessesUrl);
    }

    /**
     * init some on
     *
     * @param clientRegistrationRepository
     * @param authorizedClientRepository
     * @param filterProcessesUrl
     */
    public SocialLoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
                                           OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl) {
        super(filterProcessesUrl);
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        MultiValueMap<String, String> params = SocialAuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!SocialAuthorizationResponseUtils.isAuthorizationResponse(params)) {
            OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .removeAuthorizationRequest(request, response);
        if (authorizationRequest == null) {
            OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE,
                    "Client Registration not found with Id: " + registrationId, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        // @formatter:off
        String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();
        // @formatter:on
        OAuth2AuthorizationResponse authorizationResponse = SocialAuthorizationResponseUtils.convert(params,
                redirectUri);
        Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
                new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));
        authenticationRequest.setDetails(authenticationDetails);
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) this
                .getAuthenticationManager().authenticate(authenticationRequest);

        //catch OAuthUser
        OAuth2User oAuth2User = authenticationResult.getPrincipal();
        //cat userAttributeName
        String userNameAttributeName = authenticationRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        String userUId = oAuth2User.getAttribute(userNameAttributeName);
        SocialAuthenticationService socialAuthenticationService = CommonBeanFactory.getBean(SocialAuthenticationService.class);
        if (socialAuthenticationService == null) {
            OAuth2Error oauth2Error = new OAuth2Error(SOCIAL_USER_SERVICE_NOT_FOUND,
                    "Social Login with user service has not found!", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        log.info("get oauth user info:{}", JsonUtils.toJsonString(oAuth2User));
        // 说明这个
        if (!StringUtils.hasText(userUId)) {
            OAuth2Error oauth2Error = new OAuth2Error(USER_UNIQUE_ATTR_NOT_FOUND,
                    "User unique attribute not found with: " + userNameAttributeName, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        } else {
            // 说明有这个用户属性的 需要去获取到用户属性并获取是否已经有用户 有用户的话 就直接返回对应的Authentication
            // 判断是不是可以获取到用户
            LocalUser loginUser = UserUtils.getCurrentUser();
            if (loginUser == null) {
                //去获取是否已经绑定了用户 绑定了直接就登录
                try {
                    LocalUser localUser = socialAuthenticationService.queryUserBySocailUser(userUId, registrationId);
                    localUser.setLoginType(LoginType.SOCIAL);
                    return new SocialAuthenticationToken(localUser, oAuth2User, registrationId);
                } catch (Exception e) {
                    log.error("not found local user info:", e);
                    socialAuthenticationService.saveSocialUser(userUId, registrationId, JsonUtils.toJsonString(oAuth2User));
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    PrintWriter out = response.getWriter();
                    out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.error(HttpStatus.FORBIDDEN, e,
                            SocialLoginResponse.builder()
                                    .registrationId(registrationId)
                                    .clientName(clientRegistration.getClientName())
                                    .uuid(userUId)
                                    .build())));
                    out.flush();
                    out.close();
                    return null;
                }
            } else {// 处理登录绑定社交登录的情况
                try {
                    //判断是当前的用户直接返回
                    if (socialAuthenticationService.isCurrentUser(loginUser.getUserId(), userUId, registrationId))
                        return SecurityContextHolder.getContext().getAuthentication();
                    socialAuthenticationService.bindNewUser(loginUser.getUserId(), userUId, registrationId, JsonUtils.toJsonString(oAuth2User));
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    PrintWriter out = response.getWriter();
                    out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.success(I18nService.getMessage("bind_user_success"))));
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    log.error("user bind social error:", e);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON.toString());
                    PrintWriter out = response.getWriter();
                    out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.error(e, I18nService.getMessage("bind_user_error"))));
                    out.flush();
                    out.close();
                }
                return null;
            }
        }
    }

    /**
     * set authorizationRequestRepository
     *
     * @param authorizationRequestRepository
     */
    public final void setAuthorizationRequestRepository(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
        Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    /**
     * authentication Result converter
     *
     * @param authenticationResultConverter
     */
    public final void setAuthenticationResultConverter(
            Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter) {
        Assert.notNull(authenticationResultConverter, "authenticationResultConverter cannot be null");
        this.authenticationResultConverter = authenticationResultConverter;
    }

    /**
     * create a token
     *
     * @param authenticationResult
     * @return
     */
    private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
        return new OAuth2AuthenticationToken(authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
                authenticationResult.getClientRegistration().getRegistrationId());
    }
}
