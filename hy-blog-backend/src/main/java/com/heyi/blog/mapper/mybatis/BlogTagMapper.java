package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.BlogTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客-标签关联 Mapper，对应多对多中间表 t_blog_tag
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {}