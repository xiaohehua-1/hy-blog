/**
 * 系统配置服务接口
 *
 * 管理博客全局配置项（单例模式：表中始终只有一条记录）。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.utils.R;

public interface SysConfigService extends IService<SysConfig> {

    /** 获取系统配置，表中无记录时自动创建默认配置 */
    R getConfig();

    /** 更新系统配置，强制对齐到已有记录的ID */
    R updateConfig(SysConfig sysConfig);
}