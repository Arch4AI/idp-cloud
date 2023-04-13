package com.dataease.cloud.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClient;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClientExample;
import com.dataease.cloud.base.mapper.CloudOauth2RegisteredClientMapper;
import com.dataease.cloud.constant.RegxContants;
import com.dataease.cloud.exception.CloudException;
import jakarta.annotation.Resource;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * oauth2.0的客户端服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/13 2:37 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class Oauth2RegisteredClientSerive {

    @Resource
    private CloudOauth2RegisteredClientMapper cloudOauth2RegisteredClientMapper;

    //默认的授权类型
    private static List<AuthorizationGrantType> defaultGrantTypes = new ArrayList<>() {{
        add(AuthorizationGrantType.AUTHORIZATION_CODE);
        add(AuthorizationGrantType.REFRESH_TOKEN);
        add(AuthorizationGrantType.CLIENT_CREDENTIALS);
    }};

    //默认给的作用域
    private static List<String> defaultScopes = new ArrayList<>() {{
        add(OidcScopes.OPENID);
        add(OidcScopes.PROFILE);
    }};

    /**
     * 创建一个oauth客户端
     *
     * @param client
     */
    public void createRegisteredClient(CloudOauth2RegisteredClient client) {
        client.setId(IdUtil.randomUUID());
        client.setClientIdIssuedAt(Calendar.getInstance().getTime());
        if (!StringUtils.hasText(client.getClientId()))
            client.setClientId(IdUtil.nanoId(8));
        if (!StringUtils.hasText(client.getClientSecret()))
            client.setClientSecret(IdUtil.nanoId(32));
        if (!StringUtils.hasText(client.getClientAuthenticationMethods()))
            client.setClientAuthenticationMethods(ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue());
        if (!StringUtils.hasText(client.getAuthorizationGrantTypes()))
            client.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(defaultGrantTypes));
        if (!StringUtils.hasText(client.getScopes()))
            client.setScopes(StringUtils.collectionToCommaDelimitedString(defaultScopes));
        validateClient(client);
        //判断是不是已经存在
        CloudOauth2RegisteredClientExample example = new CloudOauth2RegisteredClientExample();
        example.createCriteria().andClientSecretEqualTo(client.getClientId());
        example.or().andClientSecretEqualTo(client.getClientSecret());
        if (cloudOauth2RegisteredClientMapper.countByExample(example) > 0)
            CloudException.throwException("oauth client exist");
        cloudOauth2RegisteredClientMapper.insertSelective(client);
    }

    /**
     * 校验客户端
     *
     * @param client
     */
    public void validateClient(CloudOauth2RegisteredClient client) {
        if (!StringUtils.hasText(client.getClientName()))
            CloudException.throwException("Oauth client name required");
        if (!StringUtils.hasText(client.getScopes()))
            CloudException.throwException("Oauth client scope required");
        //判断回调地址也不能为空
        if (!StringUtils.hasText(client.getRedirectUris()))
            CloudException.throwException("Oauth redirect uris required");
        //判断回调地址是否符合规则
        if (stringtoSet(client.getRedirectUris()).stream().filter(uri -> !ReUtil.isMatch(RegxContants.URL_REGX, uri)).count() > 0)
            CloudException.throwException("Oauth redirect uri not conforming to the rules");
        //判断认证方法
        if (notContains(stringtoSet(client.getClientAuthenticationMethods()), supportAuthenticaitionMethods()))
            CloudException.throwException("Oauth authentication method not support");
        //判断授权类型
        if (notContains(stringtoSet(client.getAuthorizationGrantTypes()), supportGrantTypes()))
            CloudException.throwException("Oauth authentication grant type not support");
        //判断作用域
        if (notContains(stringtoSet(client.getScopes()), supportSocops()))
            CloudException.throwException("Oauth scope not support");
        //判断accesstoken的格式化
        if (StringUtils.hasText(client.getAccessTokenFormat()) && !supportTokenFormats().contains(client.getAccessTokenFormat()))
            CloudException.throwException("Oauth access token format not support");
        //判断id token的加密方式
        if (StringUtils.hasText(client.getIdTokenSignatureAlgorithm()) && !supportIdTokenSignatureAlgorithms().contains(client.getIdTokenSignatureAlgorithm()))
            CloudException.throwException("Oauth id token  signature algorithm not support");
    }

    /**
     * 获取验证支持的方法列表
     *
     * @return
     */
    public List<String> supportAuthenticaitionMethods() {
        return new ArrayList<ClientAuthenticationMethod>() {{
            add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
            add(ClientAuthenticationMethod.NONE);
            add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            add(ClientAuthenticationMethod.CLIENT_SECRET_JWT);
            add(ClientAuthenticationMethod.PRIVATE_KEY_JWT);
        }}.stream().map(ClientAuthenticationMethod::getValue).collect(Collectors.toList());
    }

    /**
     * 获取支持授权的类型列表
     *
     * @return
     */
    public List<String> supportGrantTypes() {
        return new ArrayList<AuthorizationGrantType>() {{
            add(AuthorizationGrantType.AUTHORIZATION_CODE);
            add(AuthorizationGrantType.CLIENT_CREDENTIALS);
            add(AuthorizationGrantType.REFRESH_TOKEN);
            add(AuthorizationGrantType.JWT_BEARER);
            add(AuthorizationGrantType.PASSWORD);
        }}.stream().map(AuthorizationGrantType::getValue).collect(Collectors.toList());
    }

    /**
     * 获取支持的作用域
     *
     * @return
     */
    public List<String> supportSocops() {
        return new ArrayList<>() {{
            add(OidcScopes.OPENID);
            add(OidcScopes.PHONE);
            add(OidcScopes.PROFILE);
            add(OidcScopes.EMAIL);
        }};
    }

    /**
     * 获取支持的token格式列表
     *
     * @return
     */
    public List<String> supportTokenFormats() {
        return new ArrayList<OAuth2TokenFormat>() {{
            add(OAuth2TokenFormat.SELF_CONTAINED);
            add(OAuth2TokenFormat.REFERENCE);
        }}.stream().map(OAuth2TokenFormat::getValue).collect(Collectors.toList());
    }

    /**
     * 获取加密方式支持的
     *
     * @return
     */
    public List<String> supportIdTokenSignatureAlgorithms() {
        return Arrays.stream(SignatureAlgorithm.values()).map(SignatureAlgorithm::getName).collect(Collectors.toList());
    }

    /**
     * 将字符串专成集合
     *
     * @param str
     * @return
     */
    private static Set<String> stringtoSet(String str) {
        return StringUtils.commaDelimitedListToSet(str);
    }

    /**
     * 判断set里面是否不包含all里面的字段
     *
     * @param set
     * @param all
     * @return
     */
    private static boolean notContains(Set<String> set, List<String> all) {
        return set.stream().filter(m -> !all.contains(m)).count() > 0;
    }
}
