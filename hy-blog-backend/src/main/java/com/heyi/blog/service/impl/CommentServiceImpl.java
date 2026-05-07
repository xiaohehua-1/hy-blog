package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.mapper.mybatis.CommentMapper;
import com.heyi.blog.service.CommentService;
import com.heyi.blog.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 博客评论业务实现类
 *
 * 覆写 save/removeById，在评论增删后通过 WebSocket 广播通知前端刷新。
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 保存评论后广播 refresh_blog_comment，触发前端实时更新
     */
    @Override
    public boolean save(Comment entity) {
        boolean saved = super.save(entity);
        if (saved) {
            // 广播博客评论刷新信号，前端收到后重新拉取评论列表
            webSocketServer.broadcast("refresh_blog_comment");
        }
        return saved;
    }

    /**
     * 删除评论后广播 refresh_blog_comment，触发前端实时更新
     */
    @Override
    public boolean removeById(Serializable id) {
        boolean removed = super.removeById(id);
        if (removed) {
            webSocketServer.broadcast("refresh_blog_comment");
        }
        return removed;
    }
}