package com.heyi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.utils.R;

public interface SysConfigService extends IService<SysConfig> {
    // 获取配置 (因为只有一条记录，所以直接返回对象)
    R getConfig();
    // 更新配置
    R updateConfig(SysConfig sysConfig);
}