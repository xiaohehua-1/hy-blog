package com.heyi.blog.utils;

/**
 * 全局响应状态码枚举
 */
public enum ResultCode {
    
    SUCCESS(20000, "操作成功"),
    ERROR(20001, "操作失败"),
    
    // 用户认证相关
    UNAUTHORIZED(401, "尚未登录或登录已过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 业务错误
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
