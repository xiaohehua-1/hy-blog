package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.mapper.mybatis.CommentMapper;
import com.heyi.blog.service.CommentService;
import com.heyi.blog.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public boolean save(Comment entity) {
        boolean saved = super.save(entity);
        if (saved) {
            // 发送消息，告知前端有新评论/回复，类型为 blog
            webSocketServer.broadcast("refresh_blog_comment");
        }
        return saved;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean removed = super.removeById(id);
        if (removed) {
            webSocketServer.broadcast("refresh_blog_comment");
        }
        return removed;
    }
}