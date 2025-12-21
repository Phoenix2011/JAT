package com.massage.spa.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功
     * @param data 数据
     */
    public static <T> Result<T> success(T data) {
        return success(data, "操作成功");
    }

    /**
     * 成功
     * @param data 数据
     * @param message 消息
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public static <T> Result<T> error() {
        return error("操作失败");
    }

    /**
     * 失败
     * @param message 消息
     */
    public static <T> Result<T> error(String message) {
        return error(ResultCode.ERROR, message);
    }

    /**
     * 失败
     * @param code 状态码
     * @param message 消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 参数错误
     */
    public static <T> Result<T> paramError() {
        return error(ResultCode.PARAM_ERROR, "参数错误");
    }

    /**
     * 参数错误
     * @param message 消息
     */
    public static <T> Result<T> paramError(String message) {
        return error(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 未授权
     */
    public static <T> Result<T> unauthorized() {
        return error(ResultCode.UNAUTHORIZED, "未授权");
    }

    /**
     * 未授权
     * @param message 消息
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 禁止访问
     */
    public static <T> Result<T> forbidden() {
        return error(ResultCode.FORBIDDEN, "禁止访问");
    }

    /**
     * 禁止访问
     * @param message 消息
     */
    public static <T> Result<T> forbidden(String message) {
        return error(ResultCode.FORBIDDEN, message);
    }

    /**
     * 资源不存在
     */
    public static <T> Result<T> notFound() {
        return error(ResultCode.NOT_FOUND, "资源不存在");
    }

    /**
     * 资源不存在
     * @param message 消息
     */
    public static <T> Result<T> notFound(String message) {
        return error(ResultCode.NOT_FOUND, message);
    }

    /**
     * 服务器内部错误
     */
    public static <T> Result<T> serverError() {
        return error(ResultCode.SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 服务器内部错误
     * @param message 消息
     */
    public static <T> Result<T> serverError(String message) {
        return error(ResultCode.SERVER_ERROR, message);
    }
}
