package com.heyi.blog.exception;

import com.heyi.blog.utils.R;
import org.apache.catalina.connector.ClientAbortException;
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
    /**
     * 捕获客户端断开连接异常 (看视频/听歌时暂停或跳转经常触发)，直接忽略，不打印堆栈
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(ClientAbortException e) {
        // 什么都不做，或者只打印一行简单的日志
        System.out.println("用户断开了连接 (ClientAbortException)");
    }
}