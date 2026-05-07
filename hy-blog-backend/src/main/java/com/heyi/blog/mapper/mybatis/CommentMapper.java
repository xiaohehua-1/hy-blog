package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章评论 Mapper，对应表 t_comment
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {}