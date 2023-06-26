package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
* 表主键对象
* 表说明:
*
*
* 表名:cloud_spring_session_attributes
* Date: 2023/06/21
*/
@Data
public class CloudSpringSessionAttributesKey implements Serializable {
    /**
    * 字段说明:
    *
    *
    * 对应表中的列: cloud_spring_session_attributes.session_primary_id
    *
    */
    private String sessionPrimaryId;

    /**
    * 字段说明:
    *
    *
    * 对应表中的列: cloud_spring_session_attributes.attribute_name
    *
    */
    private String attributeName;

    private static final long serialVersionUID = 1L;
}
