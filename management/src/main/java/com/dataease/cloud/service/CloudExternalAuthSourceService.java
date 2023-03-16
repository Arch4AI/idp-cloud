package com.dataease.cloud.service;

import cn.hutool.core.codec.Base64;
import com.dataease.cloud.base.domain.*;
import com.dataease.cloud.base.mapper.CloudExternalAuthSourceMapper;
import com.dataease.cloud.base.mapper.CloudUserAuthSourceRelationMapper;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.entity.response.ExternalLoginUri;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.security.exception.SecurityException;
import com.dataease.cloud.security.external.request.ExternalSecurityRequest;
import com.fit2cloud.cloud.http.config.HttpConfig;
import com.fit2cloud.cloud.oauth.config.AuthConfig;
import com.fit2cloud.cloud.oauth.config.AuthDefaultSource;
import com.fit2cloud.cloud.oauth.model.AuthResponse;
import com.fit2cloud.cloud.oauth.model.AuthUser;
import com.fit2cloud.cloud.oauth.request.*;
import jakarta.annotation.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 第三方认证源服务
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 3:18 PM
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudExternalAuthSourceService {

    @Resource
    private CloudExternalAuthSourceMapper cloudExternalAuthSourceMapper;

    @Resource
    private CloudUserMapper cloudUserMapper;

    @Resource
    private CloudUserAuthSourceRelationMapper cloudUserAuthSourceRelationMapper;


    /**
     * 返回所有的提供商信息 最好就是不要返回密码信息 暂时未处理
     * @return
     */
    public List<CloudExternalAuthSource> listAll(){
        return cloudExternalAuthSourceMapper.selectByExample(null);
    }

    /**
     * 获取可用的需要显示到页面上的信息
     * 最好是自定义显示几个字段即可 不需要显示所有的字段
     * 自己定义Mappger.xml
     * @return
     */
    public List<CloudExternalAuthSource> listActive(){
        CloudExternalAuthSourceExample example = new CloudExternalAuthSourceExample();
        example.createCriteria().andEnableEqualTo(true);
        List<CloudExternalAuthSource> settings = cloudExternalAuthSourceMapper.selectByExample(example);
        return settings;
    }

    /**
     * 获取支持的接入客户端类型
     * 如果需要支持显示名称需要另外开发
     */
    public List<String> getSupportClientTypes(){
       return Arrays.stream(AuthDefaultSource.values()).map(defaultSource -> defaultSource.name()).collect(Collectors.toList());
    }

    /**
     * 获取返回的数据
     * @param id
     */
    public ExternalLoginUri getSettingById(String id){
        CloudExternalAuthSource setting = cloudExternalAuthSourceMapper.selectByPrimaryKey(id);
        if(setting == null)
            CloudException.throwException("Provider setting not found!");
        if(!setting.getEnable())
            CloudException.throwException("Provider not enable!");
        String state = Base64.encode(id);
        AuthDefaultSource source = Arrays.stream(AuthDefaultSource.values()).filter(defaultSource->defaultSource.name().equals(setting.getClientType())).findFirst().orElse(null);
        if(source == null)
            CloudException.throwException("Provider not support!");
        AuthDefaultRequest authDefaultRequest = getRequestBySource(source,setting);
        if(authDefaultRequest == null)
            CloudException.throwException("Auth request not found!");
        String uri = authDefaultRequest.authorize(state);
        return ExternalLoginUri
                .builder()
                .id(setting.getId())
                .clientType(setting.getClientType())
                .uri(uri)
                .build();
    }

    /**
     * 根据Source获取请求体
     * @param source
     * @param setting
     * @return
     */
    private AuthDefaultRequest getRequestBySource(AuthDefaultSource source,CloudExternalAuthSource setting){
        AuthDefaultRequest request = null;
        AuthConfig authConfig = builder(setting);
        switch (source){
            case GITHUB:
                request = new AuthGithubRequest(authConfig);
                break;
            case WEIBO:
                request = new AuthWeiboRequest(authConfig);
                break;
            case GITEE:
                request = new AuthGiteeRequest(authConfig);
                break;
            case DINGTALK:
                request = new AuthDingTalkRequest(authConfig);
                break;
            case DINGTALK_ACCOUNT:
                request = new AuthDingTalkAccountRequest(authConfig);
                break;
            case BAIDU:
                request = new AuthBaiduRequest(authConfig);
                break;
            case CSDN:
                request = new AuthCsdnRequest(authConfig);
                break;
            case CODING:
                request = new AuthCodingRequest(authConfig);
                break;
            case OSCHINA:
                request = new AuthOschinaRequest(authConfig);
                break;
            case ALIPAY:
                request = new AuthAlipayRequest(authConfig,setting.getAlipayPublicKey());
                break;
            case QQ:
                request = new AuthQqRequest(authConfig);
                break;
            case WECHAT_OPEN:
                request = new AuthWeChatOpenRequest(authConfig);
                break;
            case WECHAT_MP:
                request = new AuthWeChatMpRequest(authConfig);
                break;
            case TAOBAO:
                request = new AuthTaobaoRequest(authConfig);
                break;
            case GOOGLE:
                request = new AuthGoogleRequest(authConfig);
                break;
            case FACEBOOK:
                request = new AuthFacebookRequest(authConfig);
                break;
            case DOUYIN:
                request = new AuthDouyinRequest(authConfig);
                break;
            case LINKEDIN:
                request = new AuthLinkedinRequest(authConfig);
                break;
            case MICROSOFT:
                request = new AuthMicrosoftRequest(authConfig);
                break;
            case MICROSOFT_CN:
                request = new AuthMicrosoftCnRequest(authConfig);
                break;
            case MI:
                request = new AuthMiRequest(authConfig);
                break;
            case TOUTIAO:
                request = new AuthToutiaoRequest(authConfig);
                break;
            case TEAMBITION:
                request = new AuthTeambitionRequest(authConfig);
                break;
            case RENREN:
                request = new AuthRenrenRequest(authConfig);
                break;
            case PINTEREST:
                request = new AuthPinterestRequest(authConfig);
                break;
            case STACK_OVERFLOW:
                request = new AuthStackOverflowRequest(authConfig);
                break;
            case HUAWEI:
                request = new AuthHuaweiRequest(authConfig);
                break;
            case WECHAT_ENTERPRISE:
                request = new AuthWeChatEnterpriseQrcodeRequest(authConfig);
                break;
            case WECHAT_ENTERPRISE_QRCODE_THIRD:
                request = new AuthWeChatEnterpriseThirdQrcodeRequest(authConfig);
                break;
            case WECHAT_ENTERPRISE_WEB:
                request = new AuthWeChatEnterpriseWebRequest(authConfig);
                break;
            case KUJIALE:
                request = new AuthKujialeRequest(authConfig);
                break;
            case GITLAB:
                request = new AuthGitlabRequest(authConfig);
                break;
            case MEITUAN:
                request = new AuthMeituanRequest(authConfig);
                break;
            case ELEME:
                request = new AuthElemeRequest(authConfig);
                break;
            case TWITTER:
                request = new AuthTwitterRequest(authConfig);
                break;
            case FEISHU:
                request = new AuthFeishuRequest(authConfig);
                break;
            case JD:
                request = new AuthJdRequest(authConfig);
                break;
            case ALIYUN:
                request = new AuthAliyunRequest(authConfig);
                break;
            case XMLY:
                request = new AuthXmlyRequest(authConfig);
                break;
            case AMAZON:
                request = new AuthAmazonRequest(authConfig);
                break;
            case SLACK:
                request = new AuthSlackRequest(authConfig);
                break;
            case LINE:
                request = new AuthLineRequest(authConfig);
                break;
            case OKTA:
                request = new AuthOktaRequest(authConfig);
                break;
            case PROGINN:
                request = new AuthProginnRequest(authConfig);
                break;
            case OIDC:
                request = new AuthOIDCRequest(authConfig);
                break;
            default:
                break;
        }
        return request;
    }

    /**
     * 返回一个参数
     * @param setting
     * @return
     */
    private AuthConfig builder(CloudExternalAuthSource setting){
        AuthConfig authConfig = AuthConfig
                .builder()
                .clientId(setting.getClientId())
                .clientSecret(setting.getClientSecret())
                .redirectUri(setting.getRedirectUri())
                .agentId(setting.getAgentId())
                .authServerId(setting.getAuthServerId())
                .deviceId(setting.getDeviceId())
                .domainPrefix(setting.getDomainPrefix())
                .stackOverflowKey(setting.getStackOverflowKey())
                .authUri(setting.getAuthUri())
                .ignoreCheckRedirectUri(false)
                .ignoreCheckState(true)
                .tokenUri(setting.getTokenUri())
                .usertype(setting.getUserType())
                .userInfoUri(setting.getUserInfoUri())
                .build();
        // 是否开启http代理
        if(setting.getOpenProxy() != null && setting.getOpenProxy()){
            HttpConfig httpConfig = HttpConfig.builder()
                    .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(setting.getProxyHost(),setting.getProxyPort())))
                    .build();
            authConfig.setHttpConfig(httpConfig);
        }
        return authConfig;
    }


    /**
     * 添加一个认证源
     * @param cloudOauthProviderSetting
     * @return
     */
    public CloudExternalAuthSource add(CloudExternalAuthSource cloudOauthProviderSetting){
        //判断是否复合
        validateProviderSetting(cloudOauthProviderSetting);
        cloudOauthProviderSetting.setCreateTime(Calendar.getInstance().getTime());
        cloudOauthProviderSetting.setUpdateTime(Calendar.getInstance().getTime());
        //不需要判断是否重复
        cloudExternalAuthSourceMapper.insertSelective(cloudOauthProviderSetting);
        return cloudOauthProviderSetting;
    }

    /**
     * 校验认证源参数
     * @param setting
     */
    private void validateProviderSetting(CloudExternalAuthSource setting){
        if(!StringUtils.hasText(setting.getDisplayName()))
            CloudException.throwException("auth provider displayName required!");
        if(!StringUtils.hasText(setting.getClientId()))
            CloudException.throwException("auth provider clientId required!");
        if(!StringUtils.hasText(setting.getClientSecret()))
            CloudException.throwException("auth provider clientSecret required!");
        if(!StringUtils.hasText(setting.getClientType()))
            CloudException.throwException("auth provider clientType required!");
        boolean support = Arrays.stream(AuthDefaultSource.values()).filter(authDefaultSource -> authDefaultSource.name().equals(setting.getClientType())).count() > 0;
        if(!support)
            CloudException.throwException("auth provider clientType not support!");
        if(!StringUtils.hasText(setting.getRedirectUri()))
            CloudException.throwException("auth provider redirectUri required!");
        if(setting.getEnable() == null)
            CloudException.throwException("auth provider enable required!");
        String clientType = setting.getClientType();
        // 支付宝
        if(AuthDefaultSource.ALIPAY.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getAlipayPublicKey()))
                CloudException.throwException("Alipay provider alipayPublicKey required!");
        }
        //stack_overflow
        if(AuthDefaultSource.STACK_OVERFLOW.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getStackOverflowKey()))
                CloudException.throwException("Stackoverflow provider stackOverflowKey required!");
        }
        //企业微信 内部登录
        if(AuthDefaultSource.WECHAT_ENTERPRISE.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getAgentId()))
                CloudException.throwException("Wechat enterprise provider agentId required!");
        }
        //企业微信扫码
        if(AuthDefaultSource.WECHAT_ENTERPRISE_QRCODE_THIRD.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getUserType()))
                CloudException.throwException("wechat enterprise qrcode third provider userType required!");
        }

        if(AuthDefaultSource.CODING.name().equals(clientType) || AuthDefaultSource.OKTA.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getDomainPrefix()))
                CloudException.throwException("provider domainPrefix required!");
        }

        if(AuthDefaultSource.XMLY.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getDeviceId()))
                CloudException.throwException("provider deviceId required!");
        }

        if(AuthDefaultSource.OKTA.name().equals(clientType) && !StringUtils.hasText(setting.getAuthServerId()))
            setting.setAuthServerId("defalut");

        if(AuthDefaultSource.OIDC.name().equals(clientType)){
            if(!StringUtils.hasText(setting.getAuthUri()))
                CloudException.throwException("provider authUri required!");
            if(!StringUtils.hasText(setting.getTokenUri()))
                CloudException.throwException("provider tokenUri required!");
            if(!StringUtils.hasText(setting.getUserInfoUri()))
                CloudException.throwException("provider userInfoUri required!");
        }
    }

    /**
     * 根据认证源来获取外部用户
     * @param externalSecurityRequest 请求体
     */
    public AuthUser getAuthUser(ExternalSecurityRequest externalSecurityRequest){
        String sourceId = Base64.decodeStr(externalSecurityRequest.getState());
        CloudExternalAuthSource setting = cloudExternalAuthSourceMapper.selectByPrimaryKey(sourceId);
        if(setting == null)
            SecurityException.throwException("Provider not found!");
        if(!setting.getEnable())
            SecurityException.throwException("Provider not enable!");
        AuthDefaultSource source = Arrays.stream(AuthDefaultSource.values()).filter(defaultSource->defaultSource.name().equals(setting.getClientType())).findFirst().orElse(null);
        if(source == null)
            SecurityException.throwException("provider not support!");
        AuthDefaultRequest authDefaultRequest = getRequestBySource(source,setting);
        AuthResponse authResponse = authDefaultRequest.login(externalSecurityRequest);
        if(!authResponse.ok())
            SecurityException.throwException(authResponse.getMsg());
        return (AuthUser)authResponse.getData();
    }

    /**
     * 绑定第三方用户
     * @param state state 对应的是provider sourceId
     * @param authUser 第三方用户
     * @param authUser
     */
    public void bindUser(String state,AuthUser authUser){
        String sourceId = Base64.decodeStr(state);
        LocalUser localUser = (LocalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(localUser == null)
            SecurityException.throwException("User not found!");
        //查询用户是否是可用
        CloudUser cloudUser = cloudUserMapper.selectByPrimaryKey(localUser.getUserId());
        if(cloudUser == null)
            SecurityException.throwException("User not found!");
        if(!cloudUser.getEnabled())
            SecurityException.throwException("User has disabled!");
        CloudUserAuthSourceRelationKey cloudUserAuthSourceRelationKey = new CloudUserAuthSourceRelationKey();
        cloudUserAuthSourceRelationKey.setAuthSourceId(sourceId);
        cloudUserAuthSourceRelationKey.setAuthUserUuid(authUser.getUuid());
        CloudUserAuthSourceRelation cloudUserAuthSourceRelation = cloudUserAuthSourceRelationMapper.selectByPrimaryKey(cloudUserAuthSourceRelationKey);
        if(cloudUserAuthSourceRelation != null)
            SecurityException.throwException("Auth User has binded!");
        addUserRelation(authUser, sourceId, localUser.getUserId());
    }

    /**
     * 添加用户与认证的关系
     * @param authUser
     * @param sourceId
     * @param userId
     */
    private void addUserRelation(AuthUser authUser, String sourceId, String userId) {
        CloudUserAuthSourceRelation cloudUserAuthSourceRelation;
        cloudUserAuthSourceRelation = new CloudUserAuthSourceRelation();
        cloudUserAuthSourceRelation.setAuthSourceId(sourceId);
        cloudUserAuthSourceRelation.setAuthUserUuid(authUser.getUuid());
        cloudUserAuthSourceRelation.setAuthUserRaw(authUser.getRawUserInfo().toJSONString());
        cloudUserAuthSourceRelation.setUserId(userId);
        cloudUserAuthSourceRelation.setAuthSource(authUser.getSource());
        cloudUserAuthSourceRelation.setAuthAvatar(authUser.getAvatar());
        cloudUserAuthSourceRelation.setAuthEmail(authUser.getEmail());
        cloudUserAuthSourceRelation.setAuthNickName(authUser.getNickname());
        cloudUserAuthSourceRelation.setAuthUserName(authUser.getUsername());
        cloudUserAuthSourceRelation.setCreateTime(Calendar.getInstance().getTime());
        cloudUserAuthSourceRelation.setUpdateTime(Calendar.getInstance().getTime());
        cloudUserAuthSourceRelationMapper.insertSelective(cloudUserAuthSourceRelation);
    }

    /**
     * 登录的逻辑在这里
     * @param authUser
     * @param state
     */
    public CloudUser loginUser(AuthUser authUser,String state){
        String sourceId = Base64.decodeStr(state);
        //查询对应的数据 如果没有查到就直接返回就行了
        CloudUserAuthSourceRelationKey cloudUserAuthSourceRelationKey = new CloudUserAuthSourceRelationKey();
        cloudUserAuthSourceRelationKey.setAuthSourceId(sourceId);
        cloudUserAuthSourceRelationKey.setAuthUserUuid(authUser.getUuid());
        CloudUserAuthSourceRelation cloudUserAuthSourceRelation = cloudUserAuthSourceRelationMapper.selectByPrimaryKey(cloudUserAuthSourceRelationKey);
        if(cloudUserAuthSourceRelation == null || !StringUtils.hasText(cloudUserAuthSourceRelation.getUserId())){
            //创建一个空的记录 但是他会抛出异常 所以他无法插入
            SecurityException.throwException("User not found!");
        }
        CloudUser cloudUser = cloudUserMapper.selectByPrimaryKey(cloudUserAuthSourceRelation.getUserId());
        return cloudUser;
    }


    /**
     * 保存一下这个信息
     * @param authUser
     * @param state
     */
    public void saveUser(AuthUser authUser,String state){
        String sourceId = Base64.decodeStr(state);
        CloudUserAuthSourceRelationKey cloudUserAuthSourceRelationKey = new CloudUserAuthSourceRelationKey();
        cloudUserAuthSourceRelationKey.setAuthSourceId(sourceId);
        cloudUserAuthSourceRelationKey.setAuthUserUuid(authUser.getUuid());
        CloudUserAuthSourceRelation cloudUserAuthSourceRelation = cloudUserAuthSourceRelationMapper.selectByPrimaryKey(cloudUserAuthSourceRelationKey);
        if(cloudUserAuthSourceRelation == null){
            addUserRelation(authUser,sourceId,null);
        }
    }

    /**
     * 如果这个第三方已经绑定直接报错
     * @param userId
     * @param sourceId
     * @param uuid
     */
    public void bindUserRelation(String userId,String sourceId,String uuid){
        //最好判断一下是不是已经绑定了
        CloudUserAuthSourceRelationKey cloudUserAuthSourceRelationKey = new CloudUserAuthSourceRelationKey();
        cloudUserAuthSourceRelationKey.setAuthSourceId(sourceId);
        cloudUserAuthSourceRelationKey.setAuthUserUuid(uuid);
        CloudUserAuthSourceRelation cloudUserAuthSourceRelation = cloudUserAuthSourceRelationMapper.selectByPrimaryKey(cloudUserAuthSourceRelationKey);
        if(StringUtils.hasText(cloudUserAuthSourceRelation.getUserId()))
            SecurityException.throwException("external user has bind!");
        cloudUserAuthSourceRelation.setUserId(userId);
        cloudUserAuthSourceRelation.setUpdateTime(Calendar.getInstance().getTime());
        cloudUserAuthSourceRelationMapper.updateByPrimaryKeySelective(cloudUserAuthSourceRelation);
    }

}
