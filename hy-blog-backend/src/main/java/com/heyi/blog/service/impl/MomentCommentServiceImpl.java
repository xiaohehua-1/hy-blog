package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.MomentComment;
import com.heyi.blog.mapper.mybatis.MomentCommentMapper;
import com.heyi.blog.service.MomentCommentService;
import com.heyi.blog.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 动态评论业务实现类
 *
 * 覆写 save/removeById，在评论增删后通过 WebSocket 广播通知前端刷新。
 */
@Service
public class MomentCommentServiceImpl extends ServiceImpl<MomentCommentMapper, MomentComment> implements MomentCommentService {

    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 保存评论后广播 refresh_moment_comment，触发前端实时更新
     */
    @Override
    public boolean save(MomentComment entity) {
        boolean saved = super.save(entity);
        if (saved) {
            webSocketServer.broadcast("refresh_moment_comment");
        }
        return saved;
    }

    /**
     * 删除评论后广播 refresh_moment_comment，触发前端实时更新
     */
    @Override
    public boolean removeById(Serializable id) {
        boolean removed = super.removeById(id);
        if (removed) {
            webSocketServer.broadcast("refresh_moment_comment");
        }
        return removed;
    }
}