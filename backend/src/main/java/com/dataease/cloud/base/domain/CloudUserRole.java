package com.dataease.cloud.base.domain;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
* 表说明:
*  用户角色表
* 
* 表名:cloud_user_role
* Date: 2023/06/21
*/
@Data
public class CloudUserRole extends CloudUserRoleKey implements Serializable {
    /**
    * 字段说明:
    * 创建时间
    * 
    * 对应表中的列: cloud_user_role.create_time
    * 
    */ 
    private Date createTime;

    /**
    * 字段说明:
    * 修改时间
    * 
    * 对应表中的列: cloud_user_role.update_time
    * 
    */ 
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}