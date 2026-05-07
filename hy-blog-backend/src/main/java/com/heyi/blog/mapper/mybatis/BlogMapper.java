package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 博客文章 Mapper，对应表 t_blog
 * 继承 MyBatis-Plus BaseMapper 获得通用 CRUD 能力，自定义随机推荐查询。
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {
    /**
     * 随机获取一篇已发布且未删除的文章 ID，用于前台"随机阅读"功能
     */
    @Select("SELECT id FROM t_blog WHERE published = 1 AND deleted = 0 ORDER BY RAND() LIMIT 1")
    Long getRandomBlogId();
}