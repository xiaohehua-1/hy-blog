package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.SysLog;

public interface SysLogService extends IService<SysLog> {
    IPage<SysLog> pageSysLogs(Integer pageNum, Integer pageSize, String ip);
}