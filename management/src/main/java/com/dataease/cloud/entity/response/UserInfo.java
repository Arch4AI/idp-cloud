package com.dataease.cloud.entity.response;

import com.dataease.cloud.base.domain.CloudUser;
import com.dataease.cloud.base.domain.CloudUserRole;
import lombok.Data;

import java.util.List;

/**
 * 用户信息
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/8 6:40 PM
 **/
@Data
public class UserInfo extends CloudUser {

    // 用户对应的角色
    private List<CloudUserRole> cloudUserRoles;
}
