package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签 Mapper，对应表 t_tag
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {}