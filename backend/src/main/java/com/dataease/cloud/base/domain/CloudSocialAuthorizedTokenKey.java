package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
* 表主键对象
* 表说明:
*  社交登录授权Token
*
* 表名:cloud_social_authorized_token
* Date: 2023/06/21
*/
@Data
public class CloudSocialAuthorizedTokenKey implements Serializable {
    /**
    * 字段说明:
    *
    *
    * 对应表中的列: cloud_social_authorized_token.client_registration_id
    *
    */
    private String clientRegistrationId;

    /**
    * 字段说明:
    *
    *
    * 对应表中的列: cloud_social_authorized_token.principal_name
    *
    */
    private String principalName;

    private static final long serialVersionUID = 1L;
}
