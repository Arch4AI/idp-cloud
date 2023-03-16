package com.dataease.cloud.security.register.request;

import com.dataease.cloud.base.domain.CloudUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求体
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 5:03 PM
 **/
@Data
public class RegisterRequest extends CloudUser implements Serializable {
    //认证源ID
    private String sourceId;
    //认证用户UUID
    private String uuid;
}
