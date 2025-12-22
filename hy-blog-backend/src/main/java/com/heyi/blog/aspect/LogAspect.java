package com.heyi.blog.aspect;

import com.alibaba.fastjson2.JSON;
import com.heyi.blog.entity.SysLog;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    // 定义切点：只要加了 @BlogLog 注解的方法都拦截
    @Pointcut("@annotation(com.heyi.blog.entity.annotation.BlogLog)")
    public void logPointCut() {}

    // 在方法执行成功后记录日志
    @AfterReturning(value = "logPointCut()", returning = "jsonResult")
    public void saveLog(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        try {
            // 1. 获取注解上的描述
            BlogLog controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) return;

            SysLog sysLog = new SysLog();

            // 2. 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // IP地址 (简单获取，生产环境可能需要处理反向代理)
            sysLog.setIp(request.getRemoteAddr());
            // 请求URL
            sysLog.setRequestUrl(request.getRequestURI());
            // 请求方式
            // sysLog.setMethod(request.getMethod());

            // 3. 设置操作内容
            sysLog.setContent(controllerLog.value());

            // 4. 获取参数 (转JSON存储，太长则截断)
            Object[] args = joinPoint.getArgs();
            String params = JSON.toJSONString(args);
            if (params.length() > 2000) params = params.substring(0, 2000);
            sysLog.setParams(params);

            // 5. 其他信息 (浏览器、系统等，这里简化处理，有需要可以用 UserAgentUtils)
            sysLog.setBrowser(request.getHeader("User-Agent"));

            sysLog.setCreateTime(LocalDateTime.now());
            sysLog.setUpdateTime(LocalDateTime.now());

            // 6. 异步保存到数据库 (简单起见这里用同步)
            sysLogService.save(sysLog);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private BlogLog getAnnotationLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(BlogLog.class);
    }
}