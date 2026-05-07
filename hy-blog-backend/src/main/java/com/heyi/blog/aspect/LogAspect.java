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
 * 操作日志 AOP 切面
 * 拦截所有 @BlogLog 注解的方法，自动采集请求 IP、URL、参数、UA 等信息写入 t_sys_log 表
 * 整个 handleLog 包裹在 try-catch 中，日志记录失败不影响主业务
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 切点：匹配所有标注 @BlogLog 的方法（仅记录需要审计的写操作，避免日志表膨胀）
     */
    @Pointcut("@annotation(com.heyi.blog.entity.annotation.BlogLog)")
    public void logPointCut() {}

    /**
     * 后置通知：目标方法正常返回后异步记录日志
     * 仅记录成功操作，异常由全局异常处理器单独处理
     */
    @AfterReturning(value = "logPointCut()", returning = "jsonResult")
    public void saveLog(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null);
    }

    /**
     * 日志记录核心逻辑：采集注解描述、请求元信息、参数快照、UA 解析并持久化
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

            // 4. 参数截断：超过 2000 字符则截断，防止数据库字段溢出（如博客正文场景）
            if (params.length() > 2000) params = params.substring(0, 2000);
            sysLog.setParams(params);

            // 5. 使用 Hutool UserAgentUtil 解析 User-Agent 请求头，提取浏览器和操作系统
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

            // 6. 持久化到 t_sys_log
            sysLogService.save(sysLog);

        } catch (Exception exp) {
            // 日志记录异常仅打印堆栈，绝对不能向上抛出影响主业务流程
            exp.printStackTrace();
        }
    }

    /**
     * 通过反射获取方法上的 @BlogLog 注解，提取 value 作为操作描述
     */
    private BlogLog getAnnotationLog(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(BlogLog.class);
    }
}