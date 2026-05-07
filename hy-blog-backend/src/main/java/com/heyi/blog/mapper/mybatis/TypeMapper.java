package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Type;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类 Mapper，对应表 t_type
 */
@Mapper
public interface TypeMapper extends BaseMapper<Type> {}