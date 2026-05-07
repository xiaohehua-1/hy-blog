package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Moment;
import com.heyi.blog.service.MomentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台动态管理控制器
 * 负责动态（说说）的发布、编辑、删除及分页查询
 */
@RestController
@RequestMapping("/admin/moment")
public class AdminMomentController {

    @Autowired
    private MomentService momentService;

    /**
     * 分页查询动态列表，支持按内容模糊搜索（含私密动态）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) String content) {
        IPage<Moment> page = momentService.pageMoments(current, size, true, content);
        return R.ok().data("page", page);
    }

    /**
     * 发布/保存动态，未指定发布时间则默认立即发布
     */
    @PostMapping("/save")
    public R save(@RequestBody Moment moment) {
        moment.setCreateTime(LocalDateTime.now());
        moment.setUpdateTime(LocalDateTime.now());

        // 前端未传 publishTime 则立即发布，避免 null 导致定时任务异常
        if (moment.getPublishTime() == null) {
            moment.setPublishTime(LocalDateTime.now());
        }

        if (moment.getLikeCount() == null) moment.setLikeCount(0);
        momentService.save(moment);
        return R.ok();
    }

    /**
     * 更新动态
     */
    @PutMapping("/update")
    public R update(@RequestBody Moment moment) {
        moment.setUpdateTime(LocalDateTime.now());
        momentService.updateById(moment);
        return R.ok();
    }

    /**
     * 删除单条动态
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        momentService.removeById(id);
        return R.ok();
    }

    /**
     * 批量删除动态
     */
    @DeleteMapping("/batch")
    public R deleteBatch(@RequestBody List<Long> ids) {
        momentService.removeBatchByIds(ids);
        return R.ok();
    }

    /**
     * 获取动态详情（用于编辑回显）
     */
    @GetMapping("/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().data("data", momentService.getById(id));
    }
}