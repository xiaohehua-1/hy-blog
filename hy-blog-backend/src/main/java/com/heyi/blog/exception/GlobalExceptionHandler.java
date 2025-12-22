package com.heyi.blog.exception;

import com.heyi.blog.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 拦截所有未处理的异常
    @ExceptionHandler(Exception.class)
    public R handlerException(Exception e) {
        logger.error("系统异常：", e);
        return R.error("系统繁忙，请稍后重试"); // 或者 e.getMessage()，视情况而定
    }

    // 你可以拦截特定异常，比如 RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public R handlerRuntimeException(RuntimeException e) {
        logger.error("运行时异常：", e);
        return R.error(e.getMessage());
    }
}