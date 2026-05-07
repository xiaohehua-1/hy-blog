package com.heyi.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

/**
 * Web MVC 静态资源配置
 * 将本地文件系统路径映射为 URL 访问路径，解决前后端分离场景下上传文件无法通过 URL 直接访问的问题
 * uploadPath 和 accessPath 从 application.yml 读取，支持开发/生产环境切换
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 本地文件存储的物理路径（如 D:/upload/），通过 ${file.upload-path} 注入
    @Value("${file.upload-path}")
    private String uploadPath;

    // URL 虚拟路径前缀（如 /upload/**），前端通过该路径访问本地文件
    @Value("${file.access-path}")
    private String accessPath;

    /**
     * 注册静态资源映射：URL 路径 → 本地文件系统路径
     * 例如 /upload/xxx.jpg → file:D:/upload/xxx.jpg
     * 设置 30 天浏览器缓存，减少重复请求
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(accessPath)
                .addResourceLocations("file:" + uploadPath)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
    }
}