package com.dataease.cloud.entity.request;

import lombok.Data;

/**
 * 查询注册客户端的请求体
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/14 10:27 AM
 **/
@Data
public class RegisteredClientQueryReq {

    private String clientName;//客户端名称
    private Boolean isExpire;//是否到期
    private String scope;//单个作用域
}

