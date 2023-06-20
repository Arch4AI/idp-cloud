package com.dataease.cloud.security.social.configurer;

import com.dataease.cloud.security.social.authentication.SocialLoginAuthenticationProvider;
import com.dataease.cloud.security.social.filter.SocialAuthorizationRequestRedirectFilter;
import com.dataease.cloud.security.social.filter.SocialLoginAuthenticationFilter;
import com.dataease.cloud.security.social.utils.SocialClientConfigurerUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 定义一个社交登录的configurer 去实现属于基于OAuth2的客户端认证
 * 只适用于前后端分离 默认返回的都是JSON格式 默认不带重定向 慎用
 * <p>参考{@link OAuth2LoginConfigurer}</p>
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/13 1:54 PM
 **/
public final class SocialLoginConfigurer extends AbstractHttpConfigurer<SocialLoginConfigurer, HttpSecurity> {

    // 认证的相关配置
    private final AuthorizationEndpointConfig authorizationEndpointConfig = new AuthorizationEndpointConfig();

    // 获取token的认证
    private final TokenEndpointConfig tokenEndpointConfig = new TokenEndpointConfig();

    // 重定向到社交登录网址
    private final RedirectionEndpointConfig redirectionEndpointConfig = new RedirectionEndpointConfig();

    // 获取用户信息的处理
    private final UserInfoEndpointConfig userInfoEndpointConfig = new UserInfoEndpointConfig();

    // 认证后回到的地址 一般情况下都是/login/code???
    private String loginProcessingUrl = SocialLoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;

    private SocialLoginAuthenticationFilter authFilter;

    private AuthenticationSuccessHandler successHandler;

    private AuthenticationFailureHandler failureHandler;

    public SocialLoginConfigurer(){

    }

    public SocialLoginConfigurer(AuthenticationSuccessHandler successHandler,AuthenticationFailureHandler failureHandler){
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    /**
     * 设置客户端信息来源
     *
     * @param clientRegistrationRepository
     * @return
     */
    public SocialLoginConfigurer clientRegistrationRepository(
            ClientRegistrationRepository clientRegistrationRepository) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        this.getBuilder().setSharedObject(ClientRegistrationRepository.class, clientRegistrationRepository);
        return this;
    }

    /**
     * 设置认证客户端token保存
     *
     * @param authorizedClientRepository
     * @return
     */
    public SocialLoginConfigurer authorizedClientRepository(
            OAuth2AuthorizedClientRepository authorizedClientRepository) {
        Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
        this.getBuilder().setSharedObject(OAuth2AuthorizedClientRepository.class, authorizedClientRepository);
        return this;
    }

    /**
     * 设置认证客户端token保存处理的服务类
     *
     * @param authorizedClientService
     * @return
     */
    public SocialLoginConfigurer authorizedClientService(OAuth2AuthorizedClientService authorizedClientService) {
        Assert.notNull(authorizedClientService, "authorizedClientService cannot be null");
        this.authorizedClientRepository(
                new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService));
        return this;
    }

    /**
     * 设置回调的时候处理的URI
     *
     * @param loginProcessingUrl
     * @return
     */
    public SocialLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        Assert.hasText(loginProcessingUrl, "loginProcessingUrl cannot be empty");
        this.loginProcessingUrl = loginProcessingUrl;
        return this;
    }

    /**
     * 获取authorizationEndpoint
     *
     * @return
     */
    public SocialLoginConfigurer.AuthorizationEndpointConfig authorizationEndpoint() {
        return this.authorizationEndpointConfig;
    }

    /**
     * 自定义authorizationEndpoint
     *
     * @param authorizationEndpointCustomizer
     * @return
     */
    public SocialLoginConfigurer authorizationEndpoint(
            Customizer<SocialLoginConfigurer.AuthorizationEndpointConfig> authorizationEndpointCustomizer) {
        authorizationEndpointCustomizer.customize(this.authorizationEndpointConfig);
        return this;
    }

    /**
     * 设置失败的处理器
     *
     * @param authenticationFailureHandler
     * @return
     */
    public SocialLoginConfigurer failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }

    /**
     * 设置成功的处理器
     *
     * @param successHandler
     * @return
     */
    public SocialLoginConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    /**
     * 获取tokenEndpoint
     *
     * @return
     */
    public SocialLoginConfigurer.TokenEndpointConfig tokenEndpoint() {
        return this.tokenEndpointConfig;
    }

    /**
     * 自定义tokenEndpoint
     *
     * @param tokenEndpointCustomizer
     * @return
     */
    public SocialLoginConfigurer tokenEndpoint(Customizer<SocialLoginConfigurer.TokenEndpointConfig> tokenEndpointCustomizer) {
        tokenEndpointCustomizer.customize(this.tokenEndpointConfig);
        return this;
    }

    /**
     * 获取redirectionEndpoint
     *
     * @return
     */
    public SocialLoginConfigurer.RedirectionEndpointConfig redirectionEndpoint() {
        return this.redirectionEndpointConfig;
    }

    /**
     * 自定义redirectionEndpoint
     *
     * @param redirectionEndpointCustomizer
     * @return
     */
    public SocialLoginConfigurer redirectionEndpoint(
            Customizer<SocialLoginConfigurer.RedirectionEndpointConfig> redirectionEndpointCustomizer) {
        redirectionEndpointCustomizer.customize(this.redirectionEndpointConfig);
        return this;
    }

    /**
     * 获取用户userInfoEndpoint
     *
     * @return
     */
    public SocialLoginConfigurer.UserInfoEndpointConfig userInfoEndpoint() {
        return this.userInfoEndpointConfig;
    }

    /**
     * 自定义userInfoEndpoint
     *
     * @param userInfoEndpointCustomizer
     * @return
     */
    public SocialLoginConfigurer userInfoEndpoint(Customizer<SocialLoginConfigurer.UserInfoEndpointConfig> userInfoEndpointCustomizer) {
        userInfoEndpointCustomizer.customize(this.userInfoEndpointConfig);
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        // 配置认证的Filter filter的匹配路径是loginProcessingUrl
        // 如果是在Bean中已经定义的话 不需要再去set到当前configurer 默认是查询不到就会去spring bean里面加载已经加载的Bean
        SocialLoginAuthenticationFilter authenticationFilter = new SocialLoginAuthenticationFilter(
                SocialClientConfigurerUtils.getClientRegistrationRepository(this.getBuilder()),
                SocialClientConfigurerUtils.getAuthorizedClientRepository(this.getBuilder()), this.loginProcessingUrl);
        authenticationFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        // 这个地方会去触发一个AntMather
        authenticationFilter.setFilterProcessesUrl(this.loginProcessingUrl);
        this.authFilter = authenticationFilter;

        //配置获取信息的一些内容
        // 有限配置获取token服务
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = this.tokenEndpointConfig.accessTokenResponseClient;
        if (accessTokenResponseClient == null) {
            // 获取Bean
            accessTokenResponseClient = SocialClientConfigurerUtils.getOAuth2AccessTokenResponseClient(this.getBuilder());
        }
        // 获取OAuth2 User Service 不区分OAuth2与OIDC？ 因为有些社交登录明明是OAuth2但是他的作用域可能包含了openid 但是他返回的并不定支持OIDC
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = getOAuth2UserService();
        SocialLoginAuthenticationProvider socialLoginAuthenticationProvider = new SocialLoginAuthenticationProvider(
                accessTokenResponseClient, oauth2UserService);
        //获取授权角色映射
        GrantedAuthoritiesMapper userAuthoritiesMapper = this.getGrantedAuthoritiesMapper();
        if (userAuthoritiesMapper != null) {
            socialLoginAuthenticationProvider.setAuthoritiesMapper(userAuthoritiesMapper);
        }
        // 这里认证成功后会返回一个OAuth2LoginAuthenticationToken
        http.authenticationProvider(this.postProcess(socialLoginAuthenticationProvider));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        SocialAuthorizationRequestRedirectFilter authorizationRequestFilter;
        // 判断如果是自定义的解析的话 就直接创建默认的添加自定义解析器
        if (this.authorizationEndpointConfig.authorizationRequestResolver != null) {
            authorizationRequestFilter = new SocialAuthorizationRequestRedirectFilter(
                    this.authorizationEndpointConfig.authorizationRequestResolver);
        } else {
            //判断是不是有那个Bean
            OAuth2AuthorizationRequestResolver requestResolver = http.getSharedObject(ApplicationContext.class).getBean(OAuth2AuthorizationRequestResolver.class);
            if(requestResolver != null){
                authorizationRequestFilter = new SocialAuthorizationRequestRedirectFilter(
                        requestResolver);
            }else{
                // 判断是否指定了回调的地址 有的话 就会加入到对应的里面去
                String authorizationRequestBaseUri = this.authorizationEndpointConfig.authorizationRequestBaseUri;
                if (authorizationRequestBaseUri == null) {
                    authorizationRequestBaseUri = SocialAuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
                }
                authorizationRequestFilter = new SocialAuthorizationRequestRedirectFilter(
                        SocialClientConfigurerUtils.getClientRegistrationRepository(this.getBuilder()),
                        authorizationRequestBaseUri);
            }
        }
        // 配置自定义的AuthorizationRequestRepository
        if (this.authorizationEndpointConfig.authorizationRequestRepository != null) {
            authorizationRequestFilter
                    .setAuthorizationRequestRepository(this.authorizationEndpointConfig.authorizationRequestRepository);
        }
        // 配置自定义的authorizationRedirectStrategy？默认应该不需要这个 在这里应该就没有重定向了
        if (this.authorizationEndpointConfig.authorizationRedirectStrategy != null) {
            authorizationRequestFilter
                    .setAuthorizationRedirectStrategy(this.authorizationEndpointConfig.authorizationRedirectStrategy);
        }
        // 请求缓存 默认就是写入到httpSession 如果session用的redis则会写入redis中
        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            authorizationRequestFilter.setRequestCache(requestCache);
        }
        // 请求写入到filter链里面
        http.addFilterBefore(this.postProcess(authorizationRequestFilter), UsernamePasswordAuthenticationFilter.class);
        SocialLoginAuthenticationFilter authenticationFilter = this.authFilter;
        if (this.redirectionEndpointConfig.authorizationResponseBaseUri != null) {
            authenticationFilter.setFilterProcessesUrl(this.redirectionEndpointConfig.authorizationResponseBaseUri);
        }
        if (this.authorizationEndpointConfig.authorizationRequestRepository != null) {
            authenticationFilter
                    .setAuthorizationRequestRepository(this.authorizationEndpointConfig.authorizationRequestRepository);
        }
        //认证中心
        authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        // session认证的
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            authenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        // 记住我
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            authenticationFilter.setRememberMeServices(rememberMeServices);
        }
        // 安全相关
        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository != null) {
            authenticationFilter.setSecurityContextRepository(securityContextRepository);
        }
        //设置成功与失败的处理器
        if (this.successHandler != null) {
            authenticationFilter.setAuthenticationSuccessHandler(this.successHandler);
        }
        if (this.failureHandler != null) {
            authenticationFilter.setAuthenticationFailureHandler(this.failureHandler);
        }
        authenticationFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        // 放到用户密码的过滤器后面
        http.addFilterAfter(this.postProcess(authenticationFilter), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * 对应授权映射的实现
     *
     * @return
     */
    private GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
        GrantedAuthoritiesMapper grantedAuthoritiesMapper = this.getBuilder()
                .getSharedObject(GrantedAuthoritiesMapper.class);
        if (grantedAuthoritiesMapper == null) {
            grantedAuthoritiesMapper = this.getGrantedAuthoritiesMapperBean();
            if (grantedAuthoritiesMapper != null) {
                this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class, grantedAuthoritiesMapper);
            }
        }
        return grantedAuthoritiesMapper;
    }

    /**
     * 获取已经注册的Bean
     *
     * @return
     */
    private GrantedAuthoritiesMapper getGrantedAuthoritiesMapperBean() {
        Map<String, GrantedAuthoritiesMapper> grantedAuthoritiesMapperMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(this.getBuilder().getSharedObject(ApplicationContext.class),
                        GrantedAuthoritiesMapper.class);
        return (!grantedAuthoritiesMapperMap.isEmpty() ? grantedAuthoritiesMapperMap.values().iterator().next() : null);
    }

    /**
     * 获取OAuth2UserService的实现类 没有则返回DefaultOAuth2UserService
     *
     * @return
     */
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> getOAuth2UserService() {
        if (this.userInfoEndpointConfig.userService != null) {
            return this.userInfoEndpointConfig.userService;
        }
        ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2UserService.class, OAuth2UserRequest.class,
                OAuth2User.class);
        OAuth2UserService<OAuth2UserRequest, OAuth2User> bean = getBeanOrNull(type);
        return (bean != null) ? bean : new DefaultOAuth2UserService();
    }

    /**
     * 获取Bean
     *
     * @param type
     * @param <T>
     * @return
     */
    private <T> T getBeanOrNull(ResolvableType type) {
        ApplicationContext context = getBuilder().getSharedObject(ApplicationContext.class);
        if (context != null) {
            String[] names = context.getBeanNamesForType(type);
            if (names.length == 1) {
                return (T) context.getBean(names[0]);
            }
        }
        return null;
    }

    public final class AuthorizationEndpointConfig {

        private String authorizationRequestBaseUri;

        private OAuth2AuthorizationRequestResolver authorizationRequestResolver;

        private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

        private RedirectStrategy authorizationRedirectStrategy;

        private AuthorizationEndpointConfig() {
        }

        public SocialLoginConfigurer.AuthorizationEndpointConfig baseUri(String authorizationRequestBaseUri) {
            Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
            this.authorizationRequestBaseUri = authorizationRequestBaseUri;
            return this;
        }

        public SocialLoginConfigurer.AuthorizationEndpointConfig authorizationRequestResolver(
                OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
            Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
            this.authorizationRequestResolver = authorizationRequestResolver;
            return this;
        }


        public SocialLoginConfigurer.AuthorizationEndpointConfig authorizationRequestRepository(
                AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
            Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
            this.authorizationRequestRepository = authorizationRequestRepository;
            return this;
        }


        public SocialLoginConfigurer.AuthorizationEndpointConfig authorizationRedirectStrategy(
                RedirectStrategy authorizationRedirectStrategy) {
            this.authorizationRedirectStrategy = authorizationRedirectStrategy;
            return this;
        }

        public SocialLoginConfigurer and() {
            return SocialLoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Authorization Server's Token Endpoint.
     */
    public final class TokenEndpointConfig {

        private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

        private TokenEndpointConfig() {
        }

        public SocialLoginConfigurer.TokenEndpointConfig accessTokenResponseClient(
                OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
            Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
            this.accessTokenResponseClient = accessTokenResponseClient;
            return this;
        }

        public SocialLoginConfigurer and() {
            return SocialLoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Client's Redirection Endpoint.
     */
    public final class RedirectionEndpointConfig {

        private String authorizationResponseBaseUri;

        private RedirectionEndpointConfig() {
        }

        public SocialLoginConfigurer.RedirectionEndpointConfig baseUri(String authorizationResponseBaseUri) {
            Assert.hasText(authorizationResponseBaseUri, "authorizationResponseBaseUri cannot be empty");
            this.authorizationResponseBaseUri = authorizationResponseBaseUri;
            return this;
        }

        public SocialLoginConfigurer and() {
            return SocialLoginConfigurer.this;
        }

    }

    /**
     * Configuration options for the Authorization Server's UserInfo Endpoint.
     */
    public final class UserInfoEndpointConfig {

        private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

        private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

        private UserInfoEndpointConfig() {
        }

        public SocialLoginConfigurer.UserInfoEndpointConfig userService(OAuth2UserService<OAuth2UserRequest, OAuth2User> userService) {
            Assert.notNull(userService, "userService cannot be null");
            this.userService = userService;
            return this;
        }


        public SocialLoginConfigurer.UserInfoEndpointConfig oidcUserService(OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) {
            Assert.notNull(oidcUserService, "oidcUserService cannot be null");
            this.oidcUserService = oidcUserService;
            return this;
        }


        public SocialLoginConfigurer.UserInfoEndpointConfig userAuthoritiesMapper(GrantedAuthoritiesMapper userAuthoritiesMapper) {
            Assert.notNull(userAuthoritiesMapper, "userAuthoritiesMapper cannot be null");
            SocialLoginConfigurer.this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class,
                    userAuthoritiesMapper);
            return this;
        }


        public SocialLoginConfigurer and() {
            return SocialLoginConfigurer.this;
        }

    }

    private static class OidcAuthenticationRequestChecker implements AuthenticationProvider {

        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken) authentication;
            OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange()
                    .getAuthorizationRequest();
            if (authorizationRequest.getScopes().contains(OidcScopes.OPENID)) {
                OAuth2Error oauth2Error = new OAuth2Error("oidc_provider_not_configured",
                        "An OpenID Connect Authentication Provider has not been configured. "
                                + "Check to ensure you include the dependency 'spring-security-oauth2-jose'.",
                        null);
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            }
            return null;
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
        }

    }
}
