package com.heyi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Comment;

public interface CommentService extends IService<Comment> {
    // 暂时不需要自定义方法，IService 已经包含了 count() 等基础方法
}