package com.heyi.blog.controller.admin;

import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台系统配置控制器
 * 管理站点全局设置：站长信息、社交链接、个人简介等单例配置
 */
@RestController
@RequestMapping("/admin/sys/config")
public class AdminSysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 获取当前系统配置（全局唯一一条记录）
     */
    @GetMapping
    public R get() {
        return sysConfigService.getConfig();
    }

    /**
     * 更新系统配置
     */
    @PutMapping
    @BlogLog("更新系统配置")
    public R update(@RequestBody SysConfig sysConfig) {
        return sysConfigService.updateConfig(sysConfig);
    }
}