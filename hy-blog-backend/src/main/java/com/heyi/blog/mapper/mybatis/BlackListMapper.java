package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.BlackList;
import org.apache.ibatis.annotations.Mapper;

/**
 * IP 黑名单 Mapper，对应表 t_black_list
 */
@Mapper
public interface BlackListMapper extends BaseMapper<BlackList> {}