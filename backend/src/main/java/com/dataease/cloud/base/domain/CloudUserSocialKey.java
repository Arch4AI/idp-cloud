package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 表主键对象
* 表说明:
*  用户与社交登录的关联
* 
* 表名:cloud_user_social
* Date: 2023/06/21
*/
@Data
public class CloudUserSocialKey implements Serializable {
    /**
    * 字段说明:
    * 社交登录设置ID
    * 
    * 对应表中的列: cloud_user_social.social_setting_id
    * 
    */ 
    private String socialSettingId;

    /**
    * 字段说明:
    * 社交用户UUID
    * 
    * 对应表中的列: cloud_user_social.social_user_uuid
    * 
    */ 
    private String socialUserUuid;

    private static final long serialVersionUID = 1L;
}