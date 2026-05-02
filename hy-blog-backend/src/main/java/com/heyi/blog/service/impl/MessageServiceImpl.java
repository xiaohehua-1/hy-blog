package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Message;
import com.heyi.blog.mapper.mybatis.MessageMapper;
import com.heyi.blog.service.MessageService;
import com.heyi.blog.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public boolean save(Message entity) {
        boolean saved = super.save(entity);
        if (saved) {
            webSocketServer.broadcast("refresh_message");
        }
        return saved;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean removed = super.removeById(id);
        if (removed) {
            webSocketServer.broadcast("refresh_message");
        }
        return removed;
    }
}