package com.dataease.cloud.aspect;

import com.dataease.cloud.annotation.SysLog;
import com.dataease.cloud.base.domain.CloudSysOperLogWithBLOBs;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.constant.LogStatus;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.service.SystemLogService;
import com.dataease.cloud.utils.AddressUtils;
import com.dataease.cloud.utils.IpUtils;
import com.dataease.cloud.utils.ObjectMapperUtils;
import com.dataease.cloud.utils.ServletUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作日志记录处理
 *
 * @author junhong
 * @modifyBy ShrChang.Liu
 * @Date 2023-05-23 16:22
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    private SystemLogService systemLogService;

    /**
     * 处理完请求后执行
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, SysLog controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param e 异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, SysLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 记录操作日志
     *
     * @param controllerLog
     * @param e
     */
    protected void handleLog(final JoinPoint joinPoint, SysLog controllerLog, final Exception e, Object jsonResult) {
        try {

            LocalUser localUser = getCurrentUser();
            CloudSysOperLogWithBLOBs operLog = new CloudSysOperLogWithBLOBs();
            // 设置标题
            operLog.setTitle(controllerLog.title());
            // 操作类型
            operLog.setOperType(controllerLog.type().name());
            // 请求方法
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            if (localUser != null) {
                operLog.setOperUserId(localUser.getUserId());
                operLog.setOperUserName(localUser.getNickName());
            } else {
                operLog.setOperUserId("anonymousUser");
                operLog.setOperUserName("匿名用户");
            }
            // 请求路径
            operLog.setRequestUrl(ServletUtils.getRequest().getRequestURI());
            // 操作用户 IP
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setIp(ip);
            // 操作地址
            operLog.setLocation(AddressUtils.getRealAddressByIP(ip));
            // 操作状态
            operLog.setStatus(LogStatus.OK.name());
            // 如果操作异常，记录状态
            if (e != null) {
                operLog.setStatus(LogStatus.ERROR.name());
                operLog.setErrorMsg(e.getMessage());
            }
            if (controllerLog.isSaveRequestData()) {
                // 获取参数的信息，传入到数据库中。
                setRequestValue(joinPoint, operLog);
            }
            // 保存响应体
            if (controllerLog.isSaveResponseData() && !ObjectUtils.isEmpty(jsonResult)) {
                operLog.setReponseBody(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(jsonResult));
            }
            operLog.setCreateTime(Calendar.getInstance().getTime());
            // 保存数据库
            systemLogService.addSystemLog(operLog);

        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("Sys Oper Login record error:", e);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, CloudSysOperLogWithBLOBs operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            List<String> params = Arrays.stream(joinPoint.getArgs())
                    .filter(o -> !ObjectUtils.isEmpty(o) && !isFilterObject(o))
                    .map(o -> {
                        try {
                            return ObjectMapperUtils.simpleObjectMapper.writeValueAsString(o);
                        } catch (Exception e) {
                            log.error("request param convert json error:", e);
                        }
                        return "";
                    }).collect(Collectors.toList());
            if (!params.isEmpty()) {
                operLog.setRequestParam(StringUtils.collectionToDelimitedString(params, " "));
            }
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setRequestParam(ObjectMapperUtils.simpleObjectMapper.writeValueAsString(paramsMap));
        }
    }

    /**
     * 过滤对象
     *
     * @param o
     * @return
     */
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    private LocalUser getCurrentUser() {
        try {
            if (SecurityContextHolder.getContext() != null) {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                if (securityContext.getAuthentication() != null && !(securityContext.getAuthentication() instanceof AnonymousAuthenticationToken)) {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication.getPrincipal() instanceof LocalUser) {
                        return (LocalUser) authentication.getPrincipal();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Get Current User Error:", e);
        }
        return null;
    }
}
