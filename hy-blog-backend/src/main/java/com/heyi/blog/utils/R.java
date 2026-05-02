/**
 * ================================================================================
 * 文件名：R.java
 * 项目名：HyBlog Backend
 *
 * 【核心职责】
 * 统一封装后端所有API接口的响应结果，确保前后端数据交互格式一致，
 * 是整个项目数据传输的标准载体。
 *
 * 【主要功能模块】
 * 1. success() - 成功响应静态工厂方法，返回默认成功状态
 * 2. error() - 失败响应静态工厂方法，支持自定义错误信息
 * 3. 链式调用方法 - success()/message()/code()/data()，支持流式API设计
 *
 * 【依赖关系】
 * - 依赖：ResultCode枚举（定义所有响应码和消息）
 * - 被依赖：项目中所有Controller类
 *
 * 【设计思路】
 * 这里我设计这个类时，采用了静态工厂方法+链式调用的模式，
 * 当时考虑到这样写代码会很流畅，比如 R.success().data("key", value).message("操作成功")，
 * 而且统一用一个类封装所有响应，前端处理起来也方便，不用关心每个接口返回什么结构。
 * data字段我用了Map<String, Object>，这样灵活性很高，可以放任意类型的数据。
 *
 * 作者：毕设项目开发团队
 * 创建时间：2025-2026
 * ================================================================================
 */
package com.heyi.blog.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果类 (重构版：使用 ResultCode 枚举)
 * [原注释] 统一返回结果类
 * 我的补充：这个类是整个后端API响应的标准格式，所有接口都返回这个类型
 */
public class R {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    /**
     * 私有构造方法，防止外部直接new对象，必须通过静态工厂方法创建
     *
     * 【设计思路】
     * 这里我把构造方法设为private，是为了强制使用静态工厂方法来创建R对象，
     * 这样能确保所有R对象都是通过标准方法创建的，不会出现不一致的情况。
     */
    private R() {}

    /**
     * 成功响应静态工厂方法
     *
     * 【设计思路】
     * 这里我创建一个默认的成功响应，success设为true，
     * code和message从ResultCode.SUCCESS枚举中获取，
     * 保证所有成功响应的标准一致。
     *
     * @return 标准成功响应对象R
     */
    public static R success() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMessage(ResultCode.SUCCESS.getMessage());
        return r;
    }

    /**
     * ok()方法，success()的别名
     *
     * 【设计思路】
     * 提供这个别名方法是为了让代码写起来更简洁，
     * 有些开发者喜欢写R.ok()而不是R.success()，效果完全一样。
     *
     * @return 标准成功响应对象R
     */
    public static R ok() {
        return success();
    }

    /**
     * 失败响应静态工厂方法（默认错误消息）
     *
     * 【设计思路】
     * 创建一个默认的失败响应，success设为false，
     * code和message从ResultCode.ERROR枚举中获取。
     *
     * @return 标准失败响应对象R
     */
    public static R error() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(ResultCode.ERROR.getMessage());
        return r;
    }

    /**
     * 自定义失败消息的静态工厂方法
     *
     * 【设计思路】
     * 这里我允许传入自定义的错误消息，
     * 因为有些业务场景需要返回更具体的错误提示给前端，
     * 比如"用户名已存在"、"密码错误"等。
     *
     * @param message 自定义错误消息内容
     * @return 带自定义错误消息的响应对象R
     */
    public static R error(String message){
        R r = new R();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR.getCode());
        r.setMessage(message);
        return r;
    }

    /**
     * 指定状态码的失败响应静态工厂方法
     *
     * 【设计思路】
     * 这个方法允许传入ResultCode枚举，
     * 当时考虑到有些错误需要用特定的状态码，比如401未授权、403禁止访问等，
     * 这样前端可以根据不同的code做不同的处理。
     *
     * @param resultCode ResultCode枚举对象，包含code和message
     * @return 指定状态码的响应对象R
     */
    public static R error(ResultCode resultCode) {
        R r = new R();
        r.setSuccess(false);
        r.setCode(resultCode.getCode());
        r.setMessage(resultCode.getMessage());
        return r;
    }

    /**
     * 链式调用方法 - 设置success字段
     *
     * 【设计思路】
     * 这里我返回this，是为了支持链式调用，
     * 比如 R.success().success(false)，虽然success()本身已经设为true了，
     * 但提供这个方法能让API更完整。
     *
     * @param success 是否成功
     * @return 当前对象this，支持链式调用
     */
    public R success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    /**
     * 链式调用方法 - 设置message字段
     *
     * 【设计思路】
     * 这个方法用于自定义响应消息，比如默认消息是"操作成功"，
     * 可以链式调用 R.success().message("文章发布成功") 来覆盖默认消息。
     *
     * @param message 自定义响应消息
     * @return 当前对象this，支持链式调用
     */
    public R message(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * 链式调用方法 - 设置code字段
     *
     * 【设计思路】
     * 这个方法允许自定义状态码，虽然一般建议用ResultCode枚举，
     * 但提供这个方法能让API更灵活，应对特殊场景。
     *
     * @param code 自定义响应状态码
     * @return 当前对象this，支持链式调用
     */
    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 链式调用方法 - 添加单个数据项
     *
     * 【设计思路】
     * 这是最常用的方法，添加key-value形式的数据到响应中，
     * 比如 R.success().data("token", token).data("user", user)，
     * 这种设计非常灵活，可以添加任意多个数据项。
     *
     * @param key 数据的键名
     * @param value 数据的值，可以是任意Object类型
     * @return 当前对象this，支持链式调用
     */
    public R data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 链式调用方法 - 批量添加数据
     *
     * 【设计思路】
     * 这个方法允许一次性传入一个Map来批量设置data，
     * 当时考虑到有些场景数据已经在Map里了，直接传入更方便。
     *
     * @param map 包含多个key-value的Map对象
     * @return 当前对象this，支持链式调用
     */
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