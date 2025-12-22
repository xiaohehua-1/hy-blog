package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.mapper.CommentMapper;
import com.heyi.blog.service.CommentService;
import org.springframework.stereotype.Service;

@Service // <--- 关键！没有这个就会报 "找不到 Bean"
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}