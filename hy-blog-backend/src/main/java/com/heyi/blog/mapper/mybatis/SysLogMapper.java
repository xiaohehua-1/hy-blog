package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统操作日志 Mapper，对应表 t_sys_log
 */
@Mapper
public interface SysLogMapper extends BaseMapper<SysLog> {}