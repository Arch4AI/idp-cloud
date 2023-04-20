package com.dataease.cloud.config;

import cn.hutool.core.util.IdUtil;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationConsentMapper;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationMapper;
import com.dataease.cloud.base.mapper.CloudOauth2RegisteredClientMapper;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.security.handler.CustomAccessDeniedHandler;
import com.dataease.cloud.security.handler.CustomAuthenticationEntryPoint;
import com.dataease.cloud.security.oauthserver.CustomOAuth2AuthorizationConsentService;
import com.dataease.cloud.security.oauthserver.CustomOAuth2AuthorizationService;
import com.dataease.cloud.security.oauthserver.CustomRegisteredClientRepository;
import com.dataease.cloud.security.oauthserver.oidc.CustomUserInfoMapper;
import com.dataease.cloud.security.oauthserver.resourceserver.ResourceServerAuthenticationEntryPoint;
import com.dataease.cloud.service.UserService;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.dataease.cloud.utils.RSAUtils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * oauth server的配置 非RX的写法 RX需要自己在webFilter里面去实现
 * 这2个暂时没接入sessionManager
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/2/24 4:08 PM
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
public class OauthServerConfig {

    /**
     * 具体的实现都在OAuth2AuthorizationServerConfiguration
     * 这里如果要实现前后端分离 需要自定义登录的方式 目前支持的是跳转到loginForm
     *
     * @param http
     * @return
     * @throws Exception
     * @see {@link SecurityConfig}
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      CustomAccessDeniedHandler customAccessDeniedHandler,
                                                                      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                                                      UserService userService) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(oidcConfigurer -> oidcConfigurer.userInfoEndpoint(oidcUserInfoEndpointConfigurer ->
                        oidcUserInfoEndpointConfigurer.userInfoMapper(new CustomUserInfoMapper(userService))));
        http
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {
                    httpSecurityOAuth2ResourceServerConfigurer.jwt();
                    httpSecurityOAuth2ResourceServerConfigurer.authenticationEntryPoint(new ResourceServerAuthenticationEntryPoint());
                });
        return http.build();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/oauth2/resource/**")
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {
                    httpSecurityOAuth2ResourceServerConfigurer.jwt();
                    httpSecurityOAuth2ResourceServerConfigurer.authenticationEntryPoint(new ResourceServerAuthenticationEntryPoint());
                })
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
                    httpSecurityExceptionHandlingConfigurer.accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        response.setContentType(MediaType.APPLICATION_JSON.toString());
                        response.getWriter().write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.error(HttpStatus.UNAUTHORIZED, "您无权限访问")));
                    });
                });
        return http.build();
    }

    /**
     * 自定义实现的RegisteredClientRepository 这里就可以通过Mybatis的方式去查询
     * 参考第三方的设计 不过这里可以匹配多个 意味着一个client理论上可以同时被多个应用使用
     * 目前使用的是默认的PasswordEncoder 后续如果指定的话 需要改动其余的都需要改动
     *
     * @param cloudOauth2RegisteredClientMapper
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(CloudOauth2RegisteredClientMapper cloudOauth2RegisteredClientMapper) {
        String clientId = "idp_cloud";
        CustomRegisteredClientRepository registeredClientRepository = new CustomRegisteredClientRepository(cloudOauth2RegisteredClientMapper);
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        String id = IdUtil.randomUUID();
        if (client != null) {
            id = client.getId();
        }

        RegisteredClient registeredClient = RegisteredClient.withId(id)
                //客户端ID
                .clientId(clientId)
                //客服端密钥 建议自己生成 生成后展示给客户端即可 需要加密
                .clientSecret("123456")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                // 授权模式 固定即可
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 这个地方必须是全路径
                .redirectUri("https://www.baidu.com")
                //这里的scope理论上也可以定义 但是需要结合你实际接口需要的权限 建议这里用枚举 因为本身这个支持OIDC v1 所以默认带了OIDC的2个profile
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.PHONE)
                //客户端配置
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder()
                        .authorizationCodeTimeToLive(Duration.ofMinutes(10))//code 10分钟有效
                        .accessTokenTimeToLive(Duration.ofMinutes(30))//token 限制30分钟
                        //默认refreshToken 是60分钟
                        .build())
                .build();
        registeredClientRepository.save(registeredClient);
        return registeredClientRepository;
    }

    /**
     * 可以自己通过数据库去实现 也可以是No-DB
     *
     * @param cloudOauth2AuthorizationMapper
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public CustomOAuth2AuthorizationService authorizationService(CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper, RegisteredClientRepository registeredClientRepository) {
        return new CustomOAuth2AuthorizationService(cloudOauth2AuthorizationMapper, registeredClientRepository);
    }

    /***
     * 可以自己通过数据库去实现 也可以是No-DB
     * @param cloudOauth2AuthorizationConsentMapper
     * @param registeredClientRepository
     * @return
     */
    @Bean
    public CustomOAuth2AuthorizationConsentService authorizationConsentService(CloudOauth2AuthorizationConsentMapper cloudOauth2AuthorizationConsentMapper, RegisteredClientRepository registeredClientRepository) {
        return new CustomOAuth2AuthorizationConsentService(cloudOauth2AuthorizationConsentMapper, registeredClientRepository);
    }

    /***
     * 这个地方有问题 不能设置成每次启动就是随机的
     * 这样分布式环境会存在非常大的问题
     * @return
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = RSAUtils.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置端点 一般直接默认即可 当然也可以做成自定义的 切记这个是固定的 在服务器启动的时候生效 因为要写入Filter的PathMatcher
     * 建议做成配置文件
     *
     * @return
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }
}
