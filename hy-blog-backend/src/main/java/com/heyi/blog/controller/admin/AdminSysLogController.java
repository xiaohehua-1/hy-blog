package com.heyi.blog.controller.admin;

import com.heyi.blog.service.SysLogService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sys/log")
public class AdminSysLogController {

    @Autowired private SysLogService sysLogService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String ip) {
        return R.success().data("page", ((com.heyi.blog.service.impl.SysLogServiceImpl)sysLogService).pageSysLogs(pageNum, pageSize, ip));
    }

    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return sysLogService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}