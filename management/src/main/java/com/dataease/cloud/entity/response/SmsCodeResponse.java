package com.dataease.cloud.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短信验证码返回体
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 3:43 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsCodeResponse {
    private String phone;
    private String code;
    private Date expireTime;//到期时间
}
