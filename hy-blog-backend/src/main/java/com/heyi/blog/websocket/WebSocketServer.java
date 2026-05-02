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

    // 虽然 @Component 默认是单例，但 WebSocket 每次连接都会创建一个新的对象
    // 所以需要用 static 集合来保存所有的 session
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        log.info("WebSocket 连接建立, 当前连接数: {}", SESSIONS.size());
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        log.info("WebSocket 连接关闭, 当前连接数: {}", SESSIONS.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到客户端消息: {}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误: {}", error.getMessage());
    }

    /**
     * 广播消息给所有连接的客户端
     * @param message 消息内容
     */
    public void broadcast(String message) {
        log.info("广播 WebSocket 消息: {}", message);
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("WebSocket 消息推送失败: {}", e.getMessage());
                }
            }
        }
    }
}
