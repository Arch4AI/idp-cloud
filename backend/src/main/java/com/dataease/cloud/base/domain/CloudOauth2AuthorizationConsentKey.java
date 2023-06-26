package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
* 表主键对象
* 表说明:
*  首次授权同意记录
*
* 表名:cloud_oauth2_authorization_consent
* Date: 2023/06/21
*/
@Data
public class CloudOauth2AuthorizationConsentKey implements Serializable {
    /**
    * 字段说明:
    * 注册客户端ID
    *
    * 对应表中的列: cloud_oauth2_authorization_consent.registered_client_id
    *
    */
    private String registeredClientId;

    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_oauth2_authorization_consent.user_id
    *
    */
    private String userId;

    private static final long serialVersionUID = 1L;
}
