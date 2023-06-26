package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
* 表主键对象
* 表说明:
*  用户角色表
*
* 表名:cloud_user_role
* Date: 2023/06/21
*/
@Data
public class CloudUserRoleKey implements Serializable {
    /**
    * 字段说明:
    * 用户ID
    *
    * 对应表中的列: cloud_user_role.user_id
    *
    */
    private String userId;

    /**
    * 字段说明:
    * 角色
    *
    * 对应表中的列: cloud_user_role.role
    *
    */
    private String role;

    private static final long serialVersionUID = 1L;
}
