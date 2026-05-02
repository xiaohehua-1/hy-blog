package com.heyi.blog.aspect;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
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
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面处理类
 * 作用：利用 Spring AOP 统一拦截加了 @BlogLog 注解的接口，自动获取请求 IP、参数并存到数据库里。
 * 这样就不用在每个 Controller 里面挨个去写记录日志的代码了，看着比较干净。
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 定义切点
     * 也就是规定到底拦截哪些方法。这里指定只拦截贴了 @BlogLog 注解的。
     * 因为像很多普通的查询接口（比如查博客列表）没必要记日志，不然日志表一下子就撑爆了。
     */
    @Pointcut("@annotation(com.heyi.blog.entity.annotation.BlogLog)")
    public void logPointCut() {}

    /**
     * 方法执行成功后触发
     * 只有业务没报错，成功走完了才去记日志。要是报错了的操作就不记了。
     */
    @AfterReturning(value = "logPointCut()", returning = "jsonResult")
    public void saveLog(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null);
    }

    /**
     * 记日志的具体逻辑
     * 注意这里整体加了 try-catch。因为记日志只是个附属功能，就算解析参数或者存数据库时报错了，
     * 也不能抛出去影响用户本来的正常操作。
     */
    protected void handleLog(final JoinPoint joinPoint, final Exception e) {
        try {
            // 1. 获取注解里写的描述，比如 "发布文章"
            BlogLog controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) return;

            SysLog sysLog = new SysLog();

            // 2. 获取当前的 HTTP 请求对象，从而拿到 IP 和请求的路径
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            sysLog.setIp(request.getRemoteAddr());
            sysLog.setRequestUrl(request.getRequestURI());
            sysLog.setMethod(request.getMethod());
            sysLog.setContent(controllerLog.value());

            // 3. 把前端传来的参数转成 JSON 存下来，方便以后排查问题
            Object[] args = joinPoint.getArgs();
            List<Object> arguments = new ArrayList<>();
            for (Object arg : args) {
                // 【避坑点】：如果是上传文件、或者 Request/Response 原生对象，千万不能转 JSON，不然会直接抛序列化异常报错！直接跳过。
                if (arg instanceof MultipartFile || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                    continue;
                }
                arguments.add(arg);
            }
            String params = JSONUtil.toJsonStr(arguments);

            // 4. 防止参数过长报错
            // 如果是发博客之类的接口，正文可能会很长。这里做个截断，超过2000个字符就丢掉后面的，免得存库时报字段超长的错。
            if (params.length() > 2000) params = params.substring(0, 2000);
            sysLog.setParams(params);

            // 5. 获取用户的浏览器和操作系统信息
            // 借助 Hutool 工具类解析 User-Agent 请求头，代码简单很多
            String userAgentStr = request.getHeader("User-Agent");
            UserAgent ua = UserAgentUtil.parse(userAgentStr);

            if (ua != null) {
                sysLog.setBrowser(ua.getBrowser().toString() + " " + ua.getVersion());
                sysLog.setOs(ua.getOs().toString());
            } else {
                sysLog.setBrowser("Unknown");
                sysLog.setOs("Unknown");
            }

            sysLog.setCreateTime(LocalDateTime.now());
            sysLog.setUpdateTime(LocalDateTime.now());

            // 6. 保存进数据库
            sysLogService.save(sysLog);

        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * 用反射拿到执行方法上的 @BlogLog 注解对象，主要为了拿它里面的 value（说明文字）
     */
    private BlogLog getAnnotationLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(BlogLog.class);
    }
}