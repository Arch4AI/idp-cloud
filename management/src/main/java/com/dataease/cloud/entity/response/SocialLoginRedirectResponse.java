package com.dataease.cloud.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交登录重定向请求
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 6:09 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginRedirectResponse {
    //重定向的地址
    private String uri;
}
