package com.heyi.blog.entity.annotation;

import java.lang.annotation.*;

/**
 * 操作日志记录注解，用于 AOP 切面拦截
 * 标注在需要记录操作日志的 Controller 方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlogLog {
    /** 操作描述，用于标识当前操作的业务含义 */
    String value() default "";
}