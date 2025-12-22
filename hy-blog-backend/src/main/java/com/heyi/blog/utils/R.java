package com.heyi.blog.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果类
 */
public class R {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    // 状态码常量
    public static final Integer SUCCESS = 20000;
    public static final Integer ERROR = 20001;

    private R() {}

    // 成功静态方法
    public static R success() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(SUCCESS);
        r.setMessage("操作成功");
        return r;
    }

    // 失败静态方法
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ERROR);
        r.setMessage("操作失败");
        return r;
    }

    // 自定义失败消息
    public static R error(String message){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ERROR);
        r.setMessage(message);
        return r;
    }

    // 链式编程方法
    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }

    // Getter & Setter
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}