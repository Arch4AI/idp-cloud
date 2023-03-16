package com.dataease.cloud.entity.request;

import lombok.Data;

/**
 * 短信校验
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 11:36 AM
 **/
@Data
public class SmsRequest {

    private String phone;
    private String code;
}
