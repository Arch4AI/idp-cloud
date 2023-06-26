package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  用户表
*
* 表名:cloud_user
* Date: 2023/06/21
*/
@Data
public class CloudUser implements Serializable {
    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_user.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * 用户名
    *
    * 对应表中的列: cloud_user.user_name
    *
    */
    private String userName;

    /**
    * 字段说明:
    * 显示名称
    *
    * 对应表中的列: cloud_user.nick_name
    *
    */
    private String nickName;

    /**
    * 字段说明:
    * 性别
    *
    * 对应表中的列: cloud_user.gender
    *
    */
    private Integer gender;

    /**
    * 字段说明:
    * 手机号码
    *
    * 对应表中的列: cloud_user.phone
    *
    */
    private String phone;

    /**
    * 字段说明:
    * 邮箱
    *
    * 对应表中的列: cloud_user.email
    *
    */
    private String email;

    /**
    * 字段说明:
    * 密码
    *
    * 对应表中的列: cloud_user.password
    *
    */
    private String password;

    /**
    * 字段说明:
    * 是否可用
    *
    * 对应表中的列: cloud_user.enabled
    *
    */
    private Boolean enabled;

    /**
    * 字段说明:
    * 创建时间
    *
    * 对应表中的列: cloud_user.create_time
    *
    */
    private Date createTime;

    /**
    * 字段说明:
    * 修改时间
    *
    * 对应表中的列: cloud_user.update_time
    *
    */
    private Date updateTime;

    /**
    * 字段说明:
    * 语言
    *
    * 对应表中的列: cloud_user.language
    *
    */
    private String language;

    private static final long serialVersionUID = 1L;
}
