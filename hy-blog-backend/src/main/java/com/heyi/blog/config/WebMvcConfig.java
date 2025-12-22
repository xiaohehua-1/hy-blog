package com.heyi.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Value("${file.access-path}")
    private String accessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件夹映射
        // addResourceHandler: 前端访问路径 (例如 /upload/**)
        // addResourceLocations: 后端真实物理路径 (例如 file:D:/blog-data/upload/)
        registry.addResourceHandler(accessPath)
                .addResourceLocations("file:" + uploadPath);
    }
}