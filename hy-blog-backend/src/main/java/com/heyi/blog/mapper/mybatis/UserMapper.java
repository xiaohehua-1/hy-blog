package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper，对应表 t_user
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {}