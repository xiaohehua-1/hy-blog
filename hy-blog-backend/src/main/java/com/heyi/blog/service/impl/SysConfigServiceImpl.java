package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.mapper.SysConfigMapper;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public R getConfig() {
        List<SysConfig> list = this.list();
        if (list.isEmpty()) {
            // 如果表里没数据，创建一个默认的
            SysConfig config = new SysConfig();
            this.save(config);
            return R.success().data("config", config);
        }
        // 始终只取第一条
        return R.success().data("config", list.get(0));
    }

    @Override
    public R updateConfig(SysConfig sysConfig) {
        // 强制 ID 为 1 (或数据库里的那个ID)，保证单例配置
        List<SysConfig> list = this.list();
        if (!list.isEmpty()) {
            sysConfig.setId(list.get(0).getId());
        }
        return this.updateById(sysConfig) ? R.success().message("保存成功") : R.error("保存失败");
    }
}