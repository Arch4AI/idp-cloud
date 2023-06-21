package com.dataease.cloud.entity.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author ShrChang.Liu
 * @version v1.0
 * @date 2023/3/7 1:52 PM
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 默认成功的返回
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T t) {
        return (Result<T>) Result.builder()
                .code(HttpStatus.OK.value())
                .data(t)
                .build();
    }

    /**
     * 默认的异常返回
     *
     * @param e
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(Exception e) {
        return error(e.getLocalizedMessage());
    }

    /**
     * 默认的异常返回
     *
     * @param e
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(Exception e, T t) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage(), t);
    }

    /**
     * 默认的异常返回
     *
     * @param e
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(HttpStatus httpStatus, Exception e, T t) {
        return error(httpStatus, e.getLocalizedMessage(), t);
    }

    /**
     * 默认异常返回
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 默认异常返回
     *
     * @param status
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(HttpStatus status, String message) {
        return (Result<T>) Result.builder()
                .code(status.value())
                .msg(message)
                .build();
    }

    /**
     * 默认异常返回
     *
     * @param status
     * @param message
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(HttpStatus status, String message, T t) {
        return (Result<T>) Result.builder()
                .code(status.value())
                .msg(message)
                .data(t)
                .build();
    }
}
