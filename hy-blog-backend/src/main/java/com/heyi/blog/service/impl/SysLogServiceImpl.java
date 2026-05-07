package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.SysLog;
import com.heyi.blog.mapper.mybatis.SysLogMapper;
import com.heyi.blog.service.SysLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统日志业务实现类
 *
 * 提供操作日志的分页查询，支持按 IP 模糊搜索。
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    /**
     * 分页查询系统日志，按创建时间倒序
     */
    public IPage<SysLog> pageSysLogs(Integer pageNum, Integer pageSize, String ip) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(ip), SysLog::getIp, ip);
        wrapper.orderByDesc(SysLog::getCreateTime);
        return this.page(page, wrapper);
    }
}