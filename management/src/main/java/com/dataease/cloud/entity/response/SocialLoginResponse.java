package com.dataease.cloud.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交登录响应
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/20 11:19 AM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginResponse {
    //社交登录配置ID
    private String registrationId;
    //社交用户唯一属性
    private String uuid;
    //社交登录客户端名称
    private String clientName;
}
