package com.dataease.cloud.controller.handler;

import com.dataease.cloud.entity.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/9 11:48 AM
 **/
@ControllerAdvice
@Slf4j
public class CloudExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public Result handException(Exception e){
        log.error("Exception happend:",e);
        return Result.error(e);
    }
}
