package com.massage.spa.common.result;

/**
 * 响应状态码常量类
 */
public class ResultCode {

    /**
     * 成功
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败
     */
    public static final Integer ERROR = 500;

    /**
     * 参数错误
     */
    public static final Integer PARAM_ERROR = 400;

    /**
     * 未授权
     */
    public static final Integer UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final Integer FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    public static final Integer NOT_FOUND = 404;

    /**
     * 服务器内部错误
     */
    public static final Integer SERVER_ERROR = 500;
}
