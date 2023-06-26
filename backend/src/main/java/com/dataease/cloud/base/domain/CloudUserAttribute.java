package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
* 表说明:
*  用户属性表
*
* 表名:cloud_user_attribute
* Date: 2023/06/21
*/
@Data
public class CloudUserAttribute extends CloudUserAttributeKey implements Serializable {
    /**
    * 字段说明:
    * 属性名称
    *
    * 对应表中的列: cloud_user_attribute.attribute_name
    *
    */
    private String attributeName;

    /**
    * 字段说明:
    * 属性值
    *
    * 对应表中的列: cloud_user_attribute.attribute_value
    *
    */
    private String attributeValue;

    /**
    * 字段说明:
    * 创建时间
    *
    * 对应表中的列: cloud_user_attribute.create_time
    *
    */
    private Date createTime;

    /**
    * 字段说明:
    * 更新时间
    *
    * 对应表中的列: cloud_user_attribute.update_time
    *
    */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
