package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  社交登录配置
*
* 表名:cloud_social_setting
* Date: 2023/06/21
*/
@Data
public class CloudSocialSetting implements Serializable {
    /**
    * 字段说明:
    * 主键
    *
    * 对应表中的列: cloud_social_setting.id
    *
    */
    private String id;

    /**
    * 字段说明:
    * 客户端名称
    *
    * 对应表中的列: cloud_social_setting.client_name
    *
    */
    private String clientName;

    /**
    * 字段说明:
    * 显示图标
    *
    * 对应表中的列: cloud_social_setting.icon
    *
    */
    private String icon;

    /**
    * 字段说明:
    * 客户端ID
    *
    * 对应表中的列: cloud_social_setting.client_id
    *
    */
    private String clientId;

    /**
    * 字段说明:
    * 客户端密钥
    *
    * 对应表中的列: cloud_social_setting.client_secret
    *
    */
    private String clientSecret;

    /**
    * 字段说明:
    * 客服端类型
    *
    * 对应表中的列: cloud_social_setting.client_type
    *
    */
    private String clientType;

    /**
    * 字段说明:
    * 客户端认证方法
    *
    * 对应表中的列: cloud_social_setting.client_auth_method
    *
    */
    private String clientAuthMethod;

    /**
    * 字段说明:
    * 授权方法
    *
    * 对应表中的列: cloud_social_setting.auth_grant_type
    *
    */
    private String authGrantType;

    /**
    * 字段说明:
    * 逗号隔开
    *
    * 对应表中的列: cloud_social_setting.scopes
    *
    */
    private String scopes;

    /**
    * 字段说明:
    * 分隔符为空的时候默认就是空格
    *
    * 对应表中的列: cloud_social_setting.scope_separator
    *
    */
    private String scopeSeparator;

    /**
    * 字段说明:
    * 认证地址
    *
    * 对应表中的列: cloud_social_setting.auth_uri
    *
    */
    private String authUri;

    /**
    * 字段说明:
    * token地址
    *
    * 对应表中的列: cloud_social_setting.token_uri
    *
    */
    private String tokenUri;

    /**
    * 字段说明:
    * 用户信息地址
    *
    * 对应表中的列: cloud_social_setting.user_info_uri
    *
    */
    private String userInfoUri;

    /**
    * 字段说明:
    * 用户唯一属性
    *
    * 对应表中的列: cloud_social_setting.user_unique_attr
    *
    */
    private String userUniqueAttr;

    /**
    * 字段说明:
    * 用户信息认证方法
    *
    * 对应表中的列: cloud_social_setting.user_auth_method
    *
    */
    private String userAuthMethod;

    /**
    * 字段说明:
    * 是否启用
    *
    * 对应表中的列: cloud_social_setting.enable
    *
    */
    private Boolean enable;

    /**
    * 字段说明:
    * 创建时间
    *
    * 对应表中的列: cloud_social_setting.create_time
    *
    */
    private Date createTime;

    /**
    * 字段说明:
    * 修改时间
    *
    * 对应表中的列: cloud_social_setting.update_time
    *
    */
    private Date updateTime;

    /**
    * 字段说明:
    * 认证密钥
    *
    * 对应表中的列: cloud_social_setting.auth_secret
    *
    */
    private String authSecret;

    private static final long serialVersionUID = 1L;
}
