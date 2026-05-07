package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.MomentComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态评论 Mapper，对应表 t_moment_comment
 */
@Mapper
public interface MomentCommentMapper extends BaseMapper<MomentComment> {
}