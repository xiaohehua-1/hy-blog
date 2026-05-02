package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.MomentComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MomentCommentMapper extends BaseMapper<MomentComment> {
}