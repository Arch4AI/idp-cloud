package com.dataease.cloud.entity.request;

import lombok.Data;

import java.util.Date;

/**
 * 修改客户端
 * 默认只能修改以下字段
 * 1. 客户端名称
 * 2. 客户端密钥到期时间
 * 3. 作用域范围
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 10:10 AM
 **/
@Data
public class RegisteredClientModifyReq {
    //客户端名称
    private String clientName;
    //客户端密钥的时间
    private Date clientSecretExpiresAt;
    //作用域 逗号隔开
    private String scopes;
}
