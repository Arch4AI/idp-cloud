package com.dataease.cloud.security.external.request;

import com.fit2cloud.cloud.oauth.model.AuthCallback;
import lombok.Data;

/**
 * 外部认证请求参数
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 10:53 AM
 **/
@Data
public class ExternalSecurityRequest extends AuthCallback {
    private String type;//预留 是通过什么进来的
}
