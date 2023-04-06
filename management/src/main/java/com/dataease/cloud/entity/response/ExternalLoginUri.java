package com.dataease.cloud.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外部登录返回的URI
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 6:09 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalLoginUri {
    private String id;
    private String clientType;
    private String uri;
}
