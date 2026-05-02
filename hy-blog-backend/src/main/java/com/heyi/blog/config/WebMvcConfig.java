package com.heyi.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

/**
 * Web MVC 核心配置类
 * 作用：处理 Web 相关的全局配置。目前主要是为了解决“本地上传的图片前端无法访问”的问题。
 * 因为项目是前后端分离的，写博客时上传的图片实际上是存到了后端服务器的本地硬盘上。
 * 如果不加这个配置，前端用 URL 直接访问图片时，Spring Boot 默认是不允许直接读取本地硬盘的，会报 404 找不到。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 从 application.yml 配置文件中读取存图片的本地磁盘绝对路径
    // 为什么要写在 yml 配置文件里而不是写死在代码里？
    // 因为我本地开发是 Windows 环境（比如存在 D 盘），等毕设做好部署到 Linux 服务器上时路径就得换成 Linux 的格式，写在配置文件里后期改起来方便。
    @Value("${file.upload-path}")
    private String uploadPath;

    // 从 application.yml 中读取前端访问图片的 URL 虚拟路径前缀 (例如 /upload/**)
    @Value("${file.access-path}")
    private String accessPath;

    /**
     * 配置静态资源映射
     * 相当于给 Spring Boot 内部的路由导航指条路：当遇到特定前缀的请求时，去电脑的哪个文件夹底下找文件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 建立映射关系
        // 举个例子：当前端在浏览器请求 http://localhost:8080/upload/xxx.jpg 时，
        // Spring Boot 就会自动把 /upload/** 替换成 file:D:/你配置的路径/upload/
        // 这样前端就能顺藤摸瓜正常显示硬盘里的图片了，写博客时插入的图片也就不会裂开了。
        registry.addResourceHandler(accessPath)
                .addResourceLocations("file:" + uploadPath)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic());
    }
}