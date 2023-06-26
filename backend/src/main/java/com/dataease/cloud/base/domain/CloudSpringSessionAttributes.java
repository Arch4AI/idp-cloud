package com.dataease.cloud.base.domain;

import lombok.Data;

import java.io.Serializable;

/**
* 表说明:
*
*
* 表名:cloud_spring_session_attributes
* Date: 2023/06/21
*/
@Data
public class CloudSpringSessionAttributes extends CloudSpringSessionAttributesKey implements Serializable {
    /**
    * 字段说明:
    *
    *
    * 对应表中的列: cloud_spring_session_attributes.attribute_bytes
    *
    */
    private byte[] attributeBytes;

    private static final long serialVersionUID = 1L;
}
