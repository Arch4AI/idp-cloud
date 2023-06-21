package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 表说明:
*  首次授权同意记录
* 
* 表名:cloud_oauth2_authorization_consent
* Date: 2023/06/21
*/
@Data
public class CloudOauth2AuthorizationConsent extends CloudOauth2AuthorizationConsentKey implements Serializable {
    /**
    * 字段说明:
    * 选中的作用域
    * 
    * 对应表中的列: cloud_oauth2_authorization_consent.authorities
    * 
    */ 
    private String authorities;

    private static final long serialVersionUID = 1L;
}