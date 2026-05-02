package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MomentMapper extends BaseMapper<Moment> {
    // 随机获取一条公开的动态 (用于首页组件)
    @Select("SELECT * FROM t_moment WHERE is_private = 0 ORDER BY RAND() LIMIT 1")
    Moment getRandomMoment();
}