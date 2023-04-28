package com.dataease.cloud.aspect;

import com.dataease.cloud.annotation.SysLog;
import com.dataease.cloud.base.domain.SysOperLog;
import com.dataease.cloud.base.mapper.CloudUserMapper;
import com.dataease.cloud.constant.LogStatus;
import com.dataease.cloud.manager.AsyncManager;
import com.dataease.cloud.manager.factory.AsyncFactory;
import com.dataease.cloud.security.LocalUser;
import com.dataease.cloud.utils.AddressUtils;
import com.dataease.cloud.utils.IpUtils;
import com.dataease.cloud.utils.ServletUtils;
import com.dataease.cloud.utils.StringUtils;
import jakarta.annotation.Resource;
import java.util.Date;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 操作日志记录处理
 *
 * @author junhong
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    private CloudUserMapper cloudUserMapper;

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, SysLog controllerLog, Object jsonResult) {
        handleLog(joinPoint, controllerLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, SysLog controllerLog, Exception e) {
        handleLog(joinPoint, controllerLog, e, null);
    }

    /**
     * 记录操作日志
     *
     * @param joinPoint
     * @param controllerLog
     * @param e
     * @param jsonResult
     */
    protected void handleLog(final JoinPoint joinPoint, SysLog controllerLog, final Exception e,
                             Object jsonResult) {
        try {
            // 获取当前的用户
            LocalUser localUser =
                (LocalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            // 请求 URL
            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            // 请求方式
            operLog.setRequestType(ServletUtils.getRequest().getMethod());
            // 业务类型, 从注解参数设置
            getControllerMethodDescription(joinPoint, controllerLog, operLog, jsonResult);
            // 操作用户
            if (localUser != null) {
                // 这里取到的只是用户 ID
                String userId = localUser.getUsername();
                operLog.setOperName(cloudUserMapper.selectByPrimaryKey(userId).getUserName());
            }
            // 操作用户 IP
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setOperIp(ip);
            // 操作地址
            operLog.setOperLocation(AddressUtils.getRealAddressByIP(ip));
            // 操作状态
            operLog.setStatus(LogStatus.NORMAL.name());
            // 如果操作异常，记录状态
            if (e != null) {
                operLog.setStatus(LogStatus.ERROR.name());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            operLog.setOperTime(new Date());
            // 保存数据库
            AsyncManager.me().execute(AsyncFactory.recordOper(operLog));

        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志注解
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, SysLog log, SysOperLog operLog,
                                               Object jsonResult) throws Exception {
        // 设置标题
        operLog.setOperTitle(log.title());
        // 设置action动作
        operLog.setBusinessType(log.logBusinessType().name());
    }
}
