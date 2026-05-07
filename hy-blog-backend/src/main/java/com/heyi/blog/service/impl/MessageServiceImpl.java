package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Message;
import com.heyi.blog.mapper.mybatis.MessageMapper;
import com.heyi.blog.service.MessageService;
import com.heyi.blog.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 留言/消息业务实现类
 *
 * 覆写 save/removeById，在留言增删后通过 WebSocket 广播通知前端刷新。
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 保存留言后广播 refresh_message，触发前端实时更新
     */
    @Override
    public boolean save(Message entity) {
        boolean saved = super.save(entity);
        if (saved) {
            webSocketServer.broadcast("refresh_message");
        }
        return saved;
    }

    /**
     * 删除留言后广播 refresh_message，触发前端实时更新
     */
    @Override
    public boolean removeById(Serializable id) {
        boolean removed = super.removeById(id);
        if (removed) {
            webSocketServer.broadcast("refresh_message");
        }
        return removed;
    }
}