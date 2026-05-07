package com.heyi.blog.controller.admin;

import com.heyi.blog.service.SysLogService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台操作日志控制器
 * 提供日志分页查询（支持按 IP 筛选）和单条删除，用于审计追踪
 */
@RestController
@RequestMapping("/admin/sys/log")
public class AdminSysLogController {

    @Autowired private SysLogService sysLogService;

    /**
     * 分页查询操作日志，可选按 IP 地址筛选
     * 强转为 SysLogServiceImpl 以调用自定义分页方法（接口未定义该扩展方法）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String ip) {
        return R.success().data("page", ((com.heyi.blog.service.impl.SysLogServiceImpl)sysLogService).pageSysLogs(pageNum, pageSize, ip));
    }

    /**
     * 删除单条日志记录
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return sysLogService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}