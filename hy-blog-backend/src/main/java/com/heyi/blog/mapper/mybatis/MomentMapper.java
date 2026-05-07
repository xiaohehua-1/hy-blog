package com.heyi.blog.mapper.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Moment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 动态/说说 Mapper，对应表 t_moment
 */
@Mapper
public interface MomentMapper extends BaseMapper<Moment> {
    /**
     * 随机获取一条公开动态，过滤私密动态，用于前台首页"随机动态"组件
     */
    @Select("SELECT * FROM t_moment WHERE is_private = 0 ORDER BY RAND() LIMIT 1")
    Moment getRandomMoment();
}