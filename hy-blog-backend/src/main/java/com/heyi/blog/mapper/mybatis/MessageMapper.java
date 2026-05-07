package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 留言板 Mapper，对应表 t_message，支持多级留言嵌套
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {}