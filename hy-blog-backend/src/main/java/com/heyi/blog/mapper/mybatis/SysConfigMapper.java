package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 Mapper，对应表 t_sys_config
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {}