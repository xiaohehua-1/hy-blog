package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Friend;
import org.apache.ibatis.annotations.Mapper;

/**
 * 友情链接 Mapper，对应表 t_friend，支持审核状态管理
 */
@Mapper
public interface FriendMapper extends BaseMapper<Friend> {}