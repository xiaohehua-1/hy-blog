package com.heyi.blog.utils;

/**
 * 统一响应状态码枚举
 *
 * 定义后端所有 API 接口的状态码和默认响应消息，按业务域分类：
 * - 通用操作结果（20xxx）
 * - 认证与授权（401/403）
 * - 业务错误（400/404/500）
 *
 * 供 R.java 静态工厂方法和全局异常处理器引用，确保全项目状态码一致。
 */
public enum ResultCode {

    // ---- 通用操作结果 ----
    SUCCESS(20000, "操作成功"),
    ERROR(20001, "操作失败"),

    // ---- 认证与授权 ----
    UNAUTHORIZED(401, "尚未登录或登录已过期"),
    FORBIDDEN(403, "没有相关权限"),

    // ---- 业务错误 ----
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /** 获取状态码 */
    public Integer getCode() {
        return code;
    }

    /** 获取默认响应消息 */
    public String getMessage() {
        return message;
    }
}
