package com.dataease.cloud.security.social.filter;

import com.dataease.cloud.entity.response.ExternalLoginUri;
import com.dataease.cloud.entity.response.Result;
import com.dataease.cloud.i18n.I18nService;
import com.dataease.cloud.utils.ObjectMapperUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 自定义的回调Filter
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/13 3:19 PM
 **/
public class SocialAuthorizationRequestRedirectFilter extends OncePerRequestFilter {

    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";

    private final ThrowableAnalyzer throwableAnalyzer = new SocialAuthorizationRequestRedirectFilter.DefaultThrowableAnalyzer();

    private RedirectStrategy authorizationRedirectStrategy = new DefaultRedirectStrategy();

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

    private RequestCache requestCache = new HttpSessionRequestCache();

    public SocialAuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository) {
        this(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }


    public SocialAuthorizationRequestRedirectFilter(ClientRegistrationRepository clientRegistrationRepository,
                                                    String authorizationRequestBaseUri) {
        Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
        Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository,
                authorizationRequestBaseUri);
    }

    public SocialAuthorizationRequestRedirectFilter(OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
        Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
        this.authorizationRequestResolver = authorizationRequestResolver;
    }

    public void setAuthorizationRedirectStrategy(RedirectStrategy authorizationRedirectStrategy) {
        Assert.notNull(authorizationRedirectStrategy, "authorizationRedirectStrategy cannot be null");
        this.authorizationRedirectStrategy = authorizationRedirectStrategy;
    }

    public final void setAuthorizationRequestRepository(
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
        Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    public final void setRequestCache(RequestCache requestCache) {
        Assert.notNull(requestCache, "requestCache cannot be null");
        this.requestCache = requestCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request);
            if (authorizationRequest != null) {
                //获取到的authorizationRequest不为空的时候触发重定向
                this.sendRedirectForAuthorization(request, response, authorizationRequest);
                return;
            }
        } catch (Exception ex) {
            this.unsuccessfulRedirectForAuthorization(request, response, ex);
            return;
        }
        try {
            filterChain.doFilter(request, response);
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Catch Social Authorization Request error:",ex);
            Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(ex);
            ClientAuthorizationRequiredException authzEx = (ClientAuthorizationRequiredException) this.throwableAnalyzer
                    .getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);
            if (authzEx != null) {
                try {
                    OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request,
                            authzEx.getClientRegistrationId());
                    if (authorizationRequest == null) {
                        throw authzEx;
                    }
                    this.requestCache.saveRequest(request, response);
                    this.sendRedirectForAuthorization(request, response, authorizationRequest);
                } catch (Exception failed) {
                    this.unsuccessfulRedirectForAuthorization(request, response, failed);
                }
                return;
            }
            if (ex instanceof ServletException) {
                throw (ServletException) ex;
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new RuntimeException(ex);
        }
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                              OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
            this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        }
        //这里触发重定向
        //理论上我们应该直接返回一个JSON格式到前端去处理 Like:
        ExternalLoginUri externalLoginUri = ExternalLoginUri
                .builder()
                .uri(authorizationRequest.getAuthorizationRequestUri())
                .build();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        PrintWriter out = response.getWriter();
        out.write(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(Result.success(externalLoginUri)));
        out.flush();
        out.close();

//        this.authorizationRedirectStrategy.sendRedirect(request, response,
//                authorizationRequest.getAuthorizationRequestUri());
    }

    private void unsuccessfulRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
                                                      Exception ex) throws IOException {
        LogMessage message = LogMessage.format("Authorization Request failed: %s", ex);
        if(this.logger.isWarnEnabled()){
            this.logger.warn(message, ex);
        }else{
            this.logger.error(message, ex);
        }
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private static final class DefaultThrowableAnalyzer extends ThrowableAnalyzer {

        @Override
        protected void initExtractorMap() {
            super.initExtractorMap();
            registerExtractor(ServletException.class, (throwable) -> {
                ThrowableAnalyzer.verifyThrowableHierarchy(throwable, ServletException.class);
                return ((ServletException) throwable).getRootCause();
            });
        }

    }
}
