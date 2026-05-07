/**
 * 统一 API 响应封装类
 *
 * 封装所有后端接口的返回数据，提供一致的 JSON 响应结构 {success, code, message, data}。
 * 采用静态工厂方法创建初始实例，通过链式调用灵活覆写字段或追加业务数据。
 *
 * 依赖：ResultCode（状态码枚举，提供标准 code/message）
 * 使用方：所有 Controller 层接口返回
 */
package com.heyi.blog.utils;

import java.util.HashMap;
import java.util.Map;

public class R {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    /**
     * 私有构造，强制通过静态工厂方法创建实例，保证响应结构一致性
     */
    private R() {}

    /**
     * 创建成功响应（code=20000）
     */
    public static R success() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        return r;
    }

    /**
     * success() 的语义别名，效果完全相同
     */
    public static R ok() {
        return success();
    }

    /**
     * 创建失败响应（code=20001，默认消息）
     */
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(ResultCode.ERROR.getMessage());
        return r;
    }

    /**
     * 创建失败响应并自定义错误消息（code=20001）
     *
     * @param message 具体错误提示，如"用户名已存在"
     */
    public static R error(String message){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(message);
        return r;
    }

    /**
     * 根据指定状态码枚举创建失败响应，用于需要精确状态码的场景（如 401/403/404）
     *
     * @param resultCode ResultCode 枚举值
     */
    public static R error(ResultCode resultCode) {
        R r = new R();
        r.setSuccess(false);
        r.setCode(resultCode.getCode());
        r.setMessage(resultCode.getMessage());
        return r;
    }

    /**
     * 链式覆写 success 字段
     */
    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    /**
     * 链式覆写 message 字段，可覆盖静态工厂方法设置的默认消息
     */
    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 链式覆写 code 字段，应对需要自定义非标准状态码的场景
     */
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 链式添加单个数据项，多次调用可追加多个 key-value
     *
     * @param key   数据键名
     * @param value 数据值，支持任意 Object 类型
     */
    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 链式批量替换 data 字段，适合数据已在 Map 中的场景
     */
    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    // ---- Getter & Setter ----
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
