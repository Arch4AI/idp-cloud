package com.dataease.cloud.security.external.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回信息
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/14 1:52 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalSecurityResponse {
    //来源ID
    private String sourceId;
    //用户的uuid
    private String uuid;
    // 类型
    private String type;
}
