package com.dataease.cloud.entity.response;

import lombok.Data;

/**
 * 阿里云短信回调
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/4/11 3:33 PM
 **/
@Data
public class AliyunSmsCodeResponse {
    private String RequestId;
    private String Message;
    private String code;
}
