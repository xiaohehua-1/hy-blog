package com.heyi.blog.entity.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlogLog {
    String value() default ""; // 操作描述
}