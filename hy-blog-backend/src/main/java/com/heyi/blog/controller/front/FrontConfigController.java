package com.heyi.blog.controller.front;

import com.heyi.blog.entity.SysConfig;
import com.heyi.blog.service.SysConfigService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/front/config")
public class FrontConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 获取站点配置信息 (个人简介、社交链接等)
     */
    @GetMapping("/detail")
    public R getDetail() {
        // 假设只有一条配置记录，ID为1
        SysConfig config = sysConfigService.getById(1);
        if (config == null) {
            return R.error("系统配置未初始化");
        }

        // 为了安全，如果实体里有敏感字段（如密码、密钥），建议创建一个 VO 类来过滤
        // 但根据目前的 SysConfig 实体，字段都是公开展示用的，直接返回即可
        return R.ok().data("data", config);
    }
}