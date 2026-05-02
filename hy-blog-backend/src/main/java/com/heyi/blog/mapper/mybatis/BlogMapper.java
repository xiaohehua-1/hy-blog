package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    @Select("SELECT id FROM t_blog WHERE published = 1 AND deleted = 0 ORDER BY RAND() LIMIT 1")
    Long getRandomBlogId();
}