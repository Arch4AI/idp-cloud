package com.dataease.cloud.aspect;

import com.dataease.cloud.annotation.NoRateLimit;
import com.dataease.cloud.exception.CloudException;
import com.dataease.cloud.properties.CommonProperties;
import com.dataease.cloud.utils.IpUtils;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 限流切面
 * 默认只能针对controller中的访问
 * 无法实现对security中filter进行限流
 * 默认针对用户,如果没有用户的话 就针对IP地址
 *
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "cloud.common",name = "enable-rate-limit",havingValue = "true")
public class RateLimiterAspect {

    //采用线程安全的Map会占用大量内存
    private static final ConcurrentMap<String, RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();


    @Pointcut("execution(* com.dataease.cloud.controller..*Controller.*(..))")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        log.info("**************rate limt begin*****************");
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if(!method.isAnnotationPresent(NoRateLimit.class)){
            //获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            //从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            //获取用户ID 需要注意这里会返回匿名的 如果是匿名的话 需要返回IP地址
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = null;
            //判断已存在且不是匿名的token
            if(authentication != null && !(authentication instanceof AnonymousAuthenticationToken)){
                userId = authentication.getName();
            }
            //获取用户或者IP
             String prefix = !StringUtils.hasText(userId) ? IpUtils.getIpAddr(request) : userId;
            //获取key
            String key = prefix;
            if (RATE_LIMITER_CACHE.get(key) == null) {
                RATE_LIMITER_CACHE.put(key,RateLimiter.create(CommonProperties.getRateLimitQps()));
                log.info("【{}'s QPS:: {}", key, RATE_LIMITER_CACHE.get(key).getRate());
            }else{
                if(!RATE_LIMITER_CACHE.get(key).tryAcquire(CommonProperties.getRateLimitTime(), CommonProperties.getRateLimitTimeUnit())){
                    CloudException.throwException("Too many interface requests, please try again later!");
                }
            }
        }
        return point.proceed();
    }

}
