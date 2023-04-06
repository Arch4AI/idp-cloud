package com.fit2cloud.cloud.oauth.enums.scope;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OIDC的作用域
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/13 2:39 PM
 **/
@Getter
@AllArgsConstructor
public enum OIDCScope implements AuthScope {

    /**
     * {@code scope} 含义，以{@code description} 为准
     */
    OPENID("openid", "获取sub", true),
    PROFILE("profile", "获取用户配置信息", true),
    EMAILS("email", "获取用户邮箱", false),
    ROLES("roles", "配置用户角色", false);

    private final String scope;
    private final String description;
    private final boolean isDefault;

}
