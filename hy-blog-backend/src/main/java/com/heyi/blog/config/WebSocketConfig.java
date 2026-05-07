package com.heyi.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 配置
 * 注册 ServerEndpointExporter，使 @ServerEndpoint 注解的 WebSocket 端点生效
 */
@Configuration
public class WebSocketConfig {

    /**
     * 扫描并注册所有 @ServerEndpoint 标注的 WebSocket 端点
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
