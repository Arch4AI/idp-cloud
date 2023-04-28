package com.dataease.cloud.security.oauthserver;

import com.dataease.cloud.base.domain.CloudOauth2AuthorizationExample;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationWithBLOBs;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationMapper;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 自定义服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 10:24 AM
 **/
@Slf4j
public class CustomOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private RegisteredClientRepository registeredClientRepository;
    private CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper;
    private ObjectMapper objectMapper = ObjectMapperUtils.objectMapper;
    private static final String AUTHORITIES_SCOPE_PREFIX = "SCOPE_";

    public CustomOAuth2AuthorizationService(CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper,
                                            RegisteredClientRepository registeredClientRepository) {
        this.cloudOauth2AuthorizationMapper = cloudOauth2AuthorizationMapper;
        this.registeredClientRepository = registeredClientRepository;
    }


    @Override
    public void save(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");

        String authorizedScopes = null;
        if (!CollectionUtils.isEmpty(authorization.getAuthorizedScopes())) {
            authorizedScopes = StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ",");
        }
        CloudOauth2AuthorizationWithBLOBs cloudOauth2AuthorizationWithBLOBs = new CloudOauth2AuthorizationWithBLOBs();
        cloudOauth2AuthorizationWithBLOBs.setId(authorization.getId());
        cloudOauth2AuthorizationWithBLOBs.setRegisteredClientId(authorization.getRegisteredClientId());
        cloudOauth2AuthorizationWithBLOBs.setUserId(authorization.getPrincipalName());
        cloudOauth2AuthorizationWithBLOBs.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());
        cloudOauth2AuthorizationWithBLOBs.setAuthorizedScopes(authorizedScopes);
        String attributes = writeMap(authorization.getAttributes());
        cloudOauth2AuthorizationWithBLOBs.setAttributes(attributes);
        String state = null;
        String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(authorizationState)) {
            state = authorizationState;
        }
        cloudOauth2AuthorizationWithBLOBs.setState(state);
        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        if (authorizationCode != null) {
            cloudOauth2AuthorizationWithBLOBs.setAuthorizationCodeValue(authorizationCode.getToken().getTokenValue());
            cloudOauth2AuthorizationWithBLOBs.setAuthorizationCodeIssuedAt(authorizationCode.getToken().getIssuedAt() != null ? Date.from(authorizationCode.getToken().getIssuedAt()) : null);
            cloudOauth2AuthorizationWithBLOBs.setAuthorizationCodeExpiresAt(authorizationCode.getToken().getExpiresAt() != null ? Date.from(authorizationCode.getToken().getExpiresAt()) : null);
            String authorizationCodeMetadata = authorizationCode.getMetadata() != null ? writeMap(authorizationCode.getMetadata()) : null;
            cloudOauth2AuthorizationWithBLOBs.setAuthorizationCodeMetadata(authorizationCodeMetadata);
        }
        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        if (accessToken != null) {
            String accessTokenType = accessToken.getToken().getTokenType().getValue();
            String accessTokenScopes = null;
            if (!CollectionUtils.isEmpty(accessToken.getToken().getScopes())) {
                accessTokenScopes = StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ",");
            }
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenValue(accessToken.getToken().getTokenValue());
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenIssuedAt(accessToken.getToken().getIssuedAt() != null ? Date.from(accessToken.getToken().getIssuedAt()) : null);
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenExpiresAt(accessToken.getToken().getExpiresAt() != null ? Date.from(accessToken.getToken().getExpiresAt()) : null);
            String metadata = accessToken.getMetadata() != null ? writeMap(accessToken.getMetadata()) : null;
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenMetadata(metadata);
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenType(accessTokenType);
            cloudOauth2AuthorizationWithBLOBs.setAccessTokenScopes(accessTokenScopes);
        }

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken = authorization.getToken(OidcIdToken.class);
        if (oidcIdToken != null) {
            cloudOauth2AuthorizationWithBLOBs.setOidcIdTokenValue(oidcIdToken.getToken().getTokenValue());
            cloudOauth2AuthorizationWithBLOBs.setOidcIdTokenIssuedAt(oidcIdToken.getToken().getIssuedAt() != null ? Date.from(oidcIdToken.getToken().getIssuedAt()) : null);
            cloudOauth2AuthorizationWithBLOBs.setOidcIdTokenExpiresAt(oidcIdToken.getToken().getExpiresAt() != null ? Date.from(oidcIdToken.getToken().getExpiresAt()) : null);
            String metadata = oidcIdToken.getMetadata() != null ? writeMap(oidcIdToken.getMetadata()) : null;
            cloudOauth2AuthorizationWithBLOBs.setOidcIdTokenMetadata(metadata);
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
        if (refreshToken != null) {
            cloudOauth2AuthorizationWithBLOBs.setRefreshTokenValue(refreshToken.getToken().getTokenValue());
            cloudOauth2AuthorizationWithBLOBs.setRefreshTokenIssuedAt(refreshToken.getToken().getIssuedAt() != null ? Date.from(refreshToken.getToken().getIssuedAt()) : null);
            cloudOauth2AuthorizationWithBLOBs.setRefreshTokenExpiresAt(refreshToken.getToken().getExpiresAt() != null ? Date.from(refreshToken.getToken().getExpiresAt()) : null);
            String metadata = refreshToken.getMetadata() != null ? writeMap(refreshToken.getMetadata()) : null;
            cloudOauth2AuthorizationWithBLOBs.setRefreshTokenMetadata(metadata);
        }
        //转化的整个内容
        CloudOauth2AuthorizationWithBLOBs primaryKey = cloudOauth2AuthorizationMapper.selectByPrimaryKey(cloudOauth2AuthorizationWithBLOBs.getId());
        if (primaryKey == null) {
            // 只在新增的時候加入
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                log.info("request session id:{},session id:{}", request.getRequestedSessionId(), request.getSession().getId());
                cloudOauth2AuthorizationWithBLOBs.setSessionId(request.getRequestedSessionId());
            }
            cloudOauth2AuthorizationMapper.insertSelective(cloudOauth2AuthorizationWithBLOBs);
        } else {
            //這個不會更新所有 那樣的話sessionId可以保存下來
            cloudOauth2AuthorizationMapper.updateByPrimaryKeySelective(cloudOauth2AuthorizationWithBLOBs);
        }
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        Assert.notNull(authorization, "authorization cannot be null");
        cloudOauth2AuthorizationMapper.deleteByPrimaryKey(authorization.getId());
    }

    @Override
    public OAuth2Authorization findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        CloudOauth2AuthorizationWithBLOBs cloudOauth2AuthorizationWithBLOBs = cloudOauth2AuthorizationMapper.selectByPrimaryKey(id);
        return entityRow(cloudOauth2AuthorizationWithBLOBs);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        Assert.hasText(token, "token cannot be empty");
        CloudOauth2AuthorizationExample example = new CloudOauth2AuthorizationExample();
        if (tokenType == null) {
            example.or().andStateEqualTo(token);
            example.or().andAuthorizationCodeValueEqualTo(token);
            example.or().andAccessTokenValueEqualTo(token);
            example.or().andRefreshTokenValueEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            example.createCriteria().andStateEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            example.createCriteria().andAuthorizationCodeValueEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
            example.createCriteria().andAccessTokenValueEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            example.createCriteria().andRefreshTokenValueEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
            example.createCriteria().andOidcIdTokenValueEqualTo(token);
            CloudOauth2AuthorizationWithBLOBs bloBs = cloudOauth2AuthorizationMapper.selectByExampleWithBLOBs(example).stream().findFirst().orElse(null);
            if (bloBs != null) {
                return entityRow(bloBs);
            }
        }
        return null;
    }

    /**
     * 转换一下
     *
     * @param bloBs
     * @return
     */
    private OAuth2Authorization entityRow(CloudOauth2AuthorizationWithBLOBs bloBs) {
        RegisteredClient registeredClient = this.registeredClientRepository.findById(bloBs.getRegisteredClientId());
        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        Set<String> authorizedScopes = Collections.emptySet();
        String authorizedScopesString = bloBs.getAuthorizedScopes();
        if (authorizedScopesString != null) {
            authorizedScopes = StringUtils.commaDelimitedListToSet(authorizedScopesString);
        }
        Map<String, Object> attributes = parseMap(bloBs.getAttributes());
        builder.id(bloBs.getId())
                .principalName(bloBs.getUserId())
                .authorizationGrantType(new AuthorizationGrantType(bloBs.getAuthorizationGrantType()))
                .authorizedScopes(authorizedScopes)
                .attributes((attrs) -> attrs.putAll(attributes));
        String state = bloBs.getState();
        if (StringUtils.hasText(state)) {
            builder.attribute(OAuth2ParameterNames.STATE, state);
        }

        Instant tokenIssuedAt;
        Instant tokenExpiresAt;
        String authorizationCodeValue = bloBs.getAuthorizationCodeValue();

        if (StringUtils.hasText(authorizationCodeValue)) {
            tokenIssuedAt = bloBs.getAuthorizationCodeIssuedAt().toInstant();
            tokenExpiresAt = bloBs.getAuthorizationCodeExpiresAt().toInstant();
            Map<String, Object> authorizationCodeMetadata = parseMap(bloBs.getAuthorizationCodeMetadata());

            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    authorizationCodeValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));
        }

        String accessTokenValue = bloBs.getAccessTokenValue();
        if (StringUtils.hasText(accessTokenValue)) {
            tokenIssuedAt = bloBs.getAccessTokenIssuedAt().toInstant();
            tokenExpiresAt = bloBs.getAccessTokenExpiresAt().toInstant();
            Map<String, Object> accessTokenMetadata = parseMap(bloBs.getAccessTokenMetadata());
            OAuth2AccessToken.TokenType tokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(bloBs.getAccessTokenType())) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            Set<String> scopes = Collections.emptySet();
            String accessTokenScopes = bloBs.getAccessTokenScopes();
            if (accessTokenScopes != null) {
                scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, accessTokenValue, tokenIssuedAt, tokenExpiresAt, scopes);
            builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));
        }

        String oidcIdTokenValue = bloBs.getOidcIdTokenValue();
        if (StringUtils.hasText(oidcIdTokenValue)) {
            tokenIssuedAt = bloBs.getOidcIdTokenIssuedAt().toInstant();
            tokenExpiresAt = bloBs.getOidcIdTokenExpiresAt().toInstant();
            Map<String, Object> oidcTokenMetadata = parseMap(bloBs.getOidcIdTokenMetadata());

            OidcIdToken oidcToken = new OidcIdToken(
                    oidcIdTokenValue, tokenIssuedAt, tokenExpiresAt, (Map<String, Object>) oidcTokenMetadata.get(OAuth2Authorization.Token.CLAIMS_METADATA_NAME));
            builder.token(oidcToken, (metadata) -> metadata.putAll(oidcTokenMetadata));
        }

        String refreshTokenValue = bloBs.getRefreshTokenValue();
        if (StringUtils.hasText(refreshTokenValue)) {
            tokenIssuedAt = bloBs.getRefreshTokenIssuedAt().toInstant();
            tokenExpiresAt = bloBs.getRefreshTokenExpiresAt() != null ? bloBs.getRefreshTokenExpiresAt().toInstant() : null;
            Map<String, Object> refreshTokenMetadata = parseMap(bloBs.getRefreshTokenMetadata());

            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    refreshTokenValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));
        }
        return builder.build();
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
