/**
 * WebSocket 实时通信服务端
 *
 * 负责维护客户端长连接，接收用户消息并支持向所有在线客户端广播通知。
 * 典型使用场景：文章评论实时推送、系统公告广播、在线人数统计。
 *
 * 端点：/websocket/interaction
 * 依赖：Jakarta WebSocket API
 */
package com.heyi.blog.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint("/websocket/interaction")
public class WebSocketServer {

    /**
     * 在线会话集合
     *
     * 使用 static 修饰的原因：@ServerEndpoint 为每次连接创建新的 WebSocketServer 实例，
     * 必须用 static 集合跨实例共享所有会话。CopyOnWriteArraySet 保证并发遍历安全。
     */
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    /**
     * 连接建立时触发，将会话注册到全局集合
     */
    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        log.info("WebSocket 连接建立, 当前连接数: {}", SESSIONS.size());
    }

    /**
     * 连接关闭时触发，从全局集合中移除会话
     */
    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        log.info("WebSocket 连接关闭, 当前连接数: {}", SESSIONS.size());
    }

    /**
     * 收到客户端文本消息时触发，当前仅记录日志
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端消息: {}", message);
    }

    /**
     * 连接发生异常时触发，记录错误日志避免连接泄漏
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误: {}", error.getMessage());
    }

    /**
     * 向所有在线客户端广播消息
     *
     * 遍历全局会话集合，跳过已关闭的连接，逐条推送。
     * 单条推送失败不影响其他客户端，仅记录错误日志。
     *
     * @param message 广播消息内容
     */
    public void broadcast(String message) {
        log.info("广播 WebSocket 消息: {}", message);
        for (Session session : SESSIONS) {
            // 跳过已关闭的会话，避免 sendText 抛异常
            if (session.isOpen()) {
                try {
                    // 同步阻塞发送，适合轻量消息场景
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    // 单条推送失败不中断广播，保证其他客户端正常接收
                    log.error("WebSocket 消息推送失败: {}", e.getMessage());
                }
            }
        }
    }
}
