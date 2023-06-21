package com.dataease.cloud.base.domain;

import java.io.Serializable;
import lombok.Data;

/**
* 表主键对象
* 表说明:
*  用户属性表
* 
* 表名:cloud_user_attribute
* Date: 2023/06/21
*/
@Data
public class CloudUserAttributeKey implements Serializable {
    /**
    * 字段说明:
    * 用户ID
    * 
    * 对应表中的列: cloud_user_attribute.user_id
    * 
    */ 
    private String userId;

    /**
    * 字段说明:
    * 属性key
    * 
    * 对应表中的列: cloud_user_attribute.attribute_key
    * 
    */ 
    private String attributeKey;

    private static final long serialVersionUID = 1L;
}