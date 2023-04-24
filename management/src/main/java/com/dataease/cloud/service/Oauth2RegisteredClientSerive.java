package com.dataease.cloud.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationConsentExample;
import com.dataease.cloud.base.domain.CloudOauth2AuthorizationExample;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClient;
import com.dataease.cloud.base.domain.CloudOauth2RegisteredClientExample;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationConsentMapper;
import com.dataease.cloud.base.mapper.CloudOauth2AuthorizationMapper;
import com.dataease.cloud.base.mapper.CloudOauth2RegisteredClientMapper;
import com.dataease.cloud.constant.RegxContants;
import com.dataease.cloud.entity.request.RegisteredClientModifyReq;
import com.dataease.cloud.entity.request.RegisteredClientQueryReq;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.i18n.I18nService;
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

    @Resource
    private CloudOauth2AuthorizationMapper cloudOauth2AuthorizationMapper;

    @Resource
    private CloudOauth2AuthorizationConsentMapper cloudOauth2AuthorizationConsentMapper;

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
     * 查询所有的客户端
     * 条件暂时未确定
     * 后续可以根据条件去查询
     *
     * @return
     */
    public List<CloudOauth2RegisteredClient> list(RegisteredClientQueryReq req) {
        CloudOauth2RegisteredClientExample example = new CloudOauth2RegisteredClientExample();
        CloudOauth2RegisteredClientExample.Criteria criteria = example.createCriteria();
        if (StringUtils.hasText(req.getClientName()))
            criteria.andClientNameLike(req.getClientName());
        if (req.getIsExpire() != null) {
            if (req.getIsExpire()) {
                criteria.andClientSecretExpiresAtLessThan(Calendar.getInstance().getTime());
            } else {
                criteria.andClientSecretExpiresAtGreaterThanOrEqualTo(Calendar.getInstance().getTime());
            }
        }
        if (StringUtils.hasText(req.getScope()))
            criteria.andScopesLike(req.getScope());
        return cloudOauth2RegisteredClientMapper.selectByExample(example);
    }

    /**
     * 重置密钥
     *
     * @param id
     * @param secret 可选
     */
    public void resetClientSecret(String id, String secret) {
        CloudOauth2RegisteredClient client = cloudOauth2RegisteredClientMapper.selectByPrimaryKey(id);
        if (client == null)
            CloudException.throwException(I18nService.getMessage("client_not_found"));
        // 如果不为空且已经到期的话需要修改他为null
        if (client.getClientSecretExpiresAt() != null && client.getClientSecretExpiresAt().before(Calendar.getInstance().getTime()))
            client.setClientSecretExpiresAt(null);
        if (StringUtils.hasText(secret)) {
            //需要判断密钥是不是已经存在或者是不是和现在的一致
            if (client.getClientSecret().equals(secret))
                CloudException.throwException(I18nService.getMessage("client_secret_not_modify"));
            CloudOauth2RegisteredClientExample clientExample = new CloudOauth2RegisteredClientExample();
            clientExample.createCriteria().andClientSecretEqualTo(secret);
            if (cloudOauth2RegisteredClientMapper.countByExample(clientExample) > 0)
                CloudException.throwException(I18nService.getMessage("client_secret_exist"));
            client.setClientSecret(secret);
        } else {
            client.setClientSecret(IdUtil.nanoId(32));
        }
        //直接修改全部才可以覆盖掉已经到期的时间限制
        cloudOauth2RegisteredClientMapper.updateByPrimaryKey(client);
    }


    /**
     * 修改客户端信息
     *
     * @param id
     * @param req
     */
    public void updateClient(String id, RegisteredClientModifyReq req) {
        CloudOauth2RegisteredClient client = cloudOauth2RegisteredClientMapper.selectByPrimaryKey(id);
        if (client == null)
            CloudException.throwException(I18nService.getMessage("client_not_found"));
        if (!StringUtils.hasText(req.getClientName()) && !StringUtils.hasText(req.getScopes()) && req.getClientSecretExpiresAt() == null)
            CloudException.throwException(I18nService.getMessage("confirm_the_attributes_that_you_want_to_modify"));
        if (StringUtils.hasText(req.getScopes()) && notContains(stringtoSet(req.getScopes()), supportSocops()))
            CloudException.throwException(I18nService.getMessage("scope_not_support"));
        //判断是不是符合要求
        client = new CloudOauth2RegisteredClient();
        client.setId(id);
        if (StringUtils.hasText(req.getClientName()))
            client.setClientName(req.getClientName());
        if (req.getClientSecretExpiresAt() != null)
            client.setClientSecretExpiresAt(req.getClientSecretExpiresAt());
        if (StringUtils.hasText(req.getScopes()))
            client.setScopes(req.getScopes());
        cloudOauth2RegisteredClientMapper.updateByPrimaryKeySelective(client);
    }

    /**
     * 根据ID移除客户端
     *
     * @param id
     */
    public void deleteById(String id) {
        cloudOauth2RegisteredClientMapper.deleteByPrimaryKey(id);
        //移除这个客户端关联的认证记录
        CloudOauth2AuthorizationExample authorizationExample = new CloudOauth2AuthorizationExample();
        authorizationExample.createCriteria().andRegisteredClientIdEqualTo(id);
        cloudOauth2AuthorizationMapper.deleteByExample(authorizationExample);
        //移除首次同意认证的记录
        CloudOauth2AuthorizationConsentExample consentExample = new CloudOauth2AuthorizationConsentExample();
        consentExample.createCriteria().andRegisteredClientIdEqualTo(id);
        cloudOauth2AuthorizationConsentMapper.deleteByExample(consentExample);
    }

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
            CloudException.throwException(I18nService.getMessage("oauth_client_exist"));
        cloudOauth2RegisteredClientMapper.insertSelective(client);
    }

    /**
     * 校验客户端
     *
     * @param client
     */
    public void validateClient(CloudOauth2RegisteredClient client) {
        if (!StringUtils.hasText(client.getClientName()))
            CloudException.throwException(I18nService.getMessage("oauth_client_name_required"));
        if (!StringUtils.hasText(client.getScopes()))
            CloudException.throwException(I18nService.getMessage("oauth_client_scope_required"));
        //判断回调地址也不能为空
        if (!StringUtils.hasText(client.getRedirectUris()))
            CloudException.throwException(I18nService.getMessage("oauth_redirect_uris_required"));
        //判断回调地址是否符合规则
        if (stringtoSet(client.getRedirectUris()).stream().filter(uri -> !ReUtil.isMatch(RegxContants.URL_REGX, uri)).count() > 0)
            CloudException.throwException(I18nService.getMessage("oauth_redirect_uri_not_conforming_to_the_rules"));
        //判断认证方法
        if (notContains(stringtoSet(client.getClientAuthenticationMethods()), supportAuthenticaitionMethods()))
            CloudException.throwException(I18nService.getMessage("oauth_authentication_method_not_support"));
        //判断授权类型
        if (notContains(stringtoSet(client.getAuthorizationGrantTypes()), supportGrantTypes()))
            CloudException.throwException(I18nService.getMessage("oauth_authentication_grant_type_not_support"));
        //判断作用域
        if (notContains(stringtoSet(client.getScopes()), supportSocops()))
            CloudException.throwException(I18nService.getMessage("oauth_scope_not_support"));
        //判断accesstoken的格式化
        if (StringUtils.hasText(client.getAccessTokenFormat()) && !supportTokenFormats().contains(client.getAccessTokenFormat()))
            CloudException.throwException(I18nService.getMessage("oauth_access_token_format_not_support"));
        //判断id token的加密方式
        if (StringUtils.hasText(client.getIdTokenSignatureAlgorithm()) && !supportIdTokenSignatureAlgorithms().contains(client.getIdTokenSignatureAlgorithm()))
            CloudException.throwException(I18nService.getMessage("oauth_id_token_signature_algorithm_not_support"));
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
