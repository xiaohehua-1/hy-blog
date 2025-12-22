package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.SysLog;
import com.heyi.blog.mapper.SysLogMapper;
import com.heyi.blog.service.SysLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    // 你需要在接口 SysLogService 中定义这个方法
    public IPage<SysLog> pageSysLogs(Integer pageNum, Integer pageSize, String ip) {
        Page<SysLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(ip), SysLog::getIp, ip);
        wrapper.orderByDesc(SysLog::getCreateTime);
        return this.page(page, wrapper);
    }
}