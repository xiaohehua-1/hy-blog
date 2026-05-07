/**
 * 系统日志服务接口
 *
 * 提供操作日志的分页查询，支持按 IP 筛选。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.SysLog;

public interface SysLogService extends IService<SysLog> {

    /** 分页查询系统日志，可按IP模糊搜索 */
    IPage<SysLog> pageSysLogs(Integer pageNum, Integer pageSize, String ip);

    /** 清理 N 天前的日志，返回删除条数 */
    int cleanOldLogs(int days);
}