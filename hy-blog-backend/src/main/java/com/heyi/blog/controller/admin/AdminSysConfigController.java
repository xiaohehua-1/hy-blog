package com.heyi.blog.controller.admin;

import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sys/config")
public class AdminSysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping
    public R get() {
        return sysConfigService.getConfig();
    }

    @PutMapping
    @BlogLog("更新系统配置")
    public R update(@RequestBody SysConfig sysConfig) {
        return sysConfigService.updateConfig(sysConfig);
    }
}