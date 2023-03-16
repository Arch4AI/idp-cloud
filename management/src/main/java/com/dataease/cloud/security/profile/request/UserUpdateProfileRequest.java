package com.dataease.cloud.security.profile.request;

import com.dataease.cloud.base.domain.CloudUser;
import lombok.Data;

/**
 * 用户更新信息
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 4:36 PM
 **/
@Data
public class UserUpdateProfileRequest extends CloudUser {
    //来源ID
    private String sourceId;
    //用户的uuid
    private String uuid;
}
