package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  用户与社交登录的关联
*
* 表名:cloud_user_social
* Date: 2023/06/21
*/
@Data
public class CloudUserSocial extends CloudUserSocialKey implements Serializable {
    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_user_social.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * 创建时间
    *
    * 对应表中的列: cloud_user_social.create_time
    *
    */
    private Date createTime;

    /**
    * 字段说明:
    * 修改时间
    *
    * 对应表中的列: cloud_user_social.update_time
    *
    */
    private Date updateTime;

    /**
    * 字段说明:
    * 用户原始数据
    *
    * 对应表中的列: cloud_user_social.user_raw
    *
    */
    private String userRaw;

    private static final long serialVersionUID = 1L;
}
