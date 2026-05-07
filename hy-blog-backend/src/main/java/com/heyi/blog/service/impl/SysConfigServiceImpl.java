package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.mapper.mybatis.SysConfigMapper;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统配置业务实现类
 *
 * 管理博客全局配置，采用单例模式：表中始终只维护一条记录。
 * 首次获取时自动创建默认配置，后续操作强制对齐到该记录的ID。
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    /**
     * 获取系统配置，表为空时自动创建默认记录
     */
    @Override
    public R getConfig() {
        List<SysConfig> list = this.list();
        if (list.isEmpty()) {
            // 首次访问：创建默认配置记录，保证表里始终有一条数据
            SysConfig config = new SysConfig();
            this.save(config);
            return R.success().data("config", config);
        }
        // 始终返回第一条（即唯一的那条）
        return R.success().data("config", list.get(0));
    }

    /**
     * 更新系统配置，强制对齐到已有记录的ID，保证单例
     */
    @Override
    public R updateConfig(SysConfig sysConfig) {
        // 强制设置ID为已有记录的ID，防止意外插入第二条记录
        List<SysConfig> list = this.list();
        if (!list.isEmpty()) {
            sysConfig.setId(list.get(0).getId());
        }
        return this.updateById(sysConfig) ? R.success().message("保存成功") : R.error("保存失败");
    }
}