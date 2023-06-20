package com.dataease.cloud.security.social.utils;

import com.dataease.cloud.security.LocalUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工具类
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/6/19 11:42 AM
 **/
public class UserUtils {

    /**
     * get Logined user info
     * must be {@link LocalUser} else has not real logined
     * @return
     */
    public static LocalUser getCurrentUser(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext != null){
            Authentication authentication = securityContext.getAuthentication();
            if(authentication != null && !(authentication instanceof AnonymousAuthenticationToken)){
                Object principal = authentication.getPrincipal();
                if(principal instanceof LocalUser){
                    return (LocalUser) principal;
                }
            }
        }
        return null;
    }
}
