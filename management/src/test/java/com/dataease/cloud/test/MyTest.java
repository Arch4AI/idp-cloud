package com.dataease.cloud.test;

import com.dataease.cloud.security.jackson2.CustomJackson2Module;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;

import java.util.List;
import java.util.Map;

/**
 * [描述当前类是什么作用]
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/15 5:19 PM
 **/
public class MyTest {

    public static void main(String[] args) throws Exception{
        ObjectMapper objectMapper = new ObjectMapper();
        List<Module> securityModules = SecurityJackson2Modules.getModules(MyTest.class.getClassLoader());
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new CustomJackson2Module());
        String data = "{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"java.security.Principal\":{\"@class\":\"com.dataease.cloud.security.password.PasswordAuthenticationToken\",\"authorities\":[\"java.util.Collections$UnmodifiableRandomAccessList\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ROLE_ADMIN\"}]],\"details\":{\"@class\":\"org.springframework.security.web.authentication.WebAuthenticationDetails\",\"remoteAddress\":\"127.0.0.1\",\"sessionId\":null},\"authenticated\":true,\"principal\":{\"@class\":\"com.dataease.cloud.security.LocalUser\",\"userId\":\"bb027e46-0ded-49a5-930d-c57999c2267c\",\"userName\":\"admin\",\"nickName\":\"超级管理员\",\"gender\":1,\"phone\":\"13168334636\",\"email\":\"admin@cloud.io\",\"enabled\":true,\"createTime\":[\"java.util.Date\",1678695661000],\"updateTime\":[\"java.util.Date\",1678787279000],\"language\":\"ZH_CN\",\"authorities\":[\"java.util.HashSet\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ROLE_ADMIN\"}]],\"loginType\":\"PASSWORD\",\"username\":\"bb027e46-0ded-49a5-930d-c57999c2267c\",\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true},\"credentials\":null,\"name\":\"bb027e46-0ded-49a5-930d-c57999c2267c\"},\"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest\":{\"@class\":\"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest\",\"authorizationUri\":\"http://127.0.0.1:8090/oauth2/authorize\",\"authorizationGrantType\":{\"value\":\"authorization_code\"},\"responseType\":{\"value\":\"code\"},\"clientId\":\"idp_cloud\",\"redirectUri\":\"https://www.baidu.com\",\"scopes\":[\"java.util.Collections$UnmodifiableSet\",[\"profile\"]],\"state\":null,\"additionalParameters\":{\"@class\":\"java.util.Collections$UnmodifiableMap\"},\"authorizationRequestUri\":\"http://127.0.0.1:8090/oauth2/authorize?response_type=code&client_id=idp_cloud&scope=profile&redirect_uri=https://www.baidu.com\",\"attributes\":{\"@class\":\"java.util.Collections$UnmodifiableMap\"}},\"state\":\"gtelGsrKFmdKyxHWy0Q06lurUmGD0iuVYE6m-Wqqjl4=\"}";
        Map<String, Object> objectMap = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {});

        System.out.println(objectMap.keySet().size());

    }
}
