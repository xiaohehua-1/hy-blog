package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Moment;
import com.heyi.blog.service.MomentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/moment")
public class AdminMomentController {

    @Autowired
    private MomentService momentService;

    // 分页列表
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size,
                  @RequestParam(required = false) String content) {
        IPage<Moment> page = momentService.pageMoments(current, size, true, content);
        return R.ok().data("page", page);
    }

    // 发布/保存
    // 在 save 方法中补充逻辑
    @PostMapping("/save")
    public R save(@RequestBody Moment moment) {
        moment.setCreateTime(LocalDateTime.now());
        moment.setUpdateTime(LocalDateTime.now());

        // 如果前端没传 publishTime，则默认立即发布
        if (moment.getPublishTime() == null) {
            moment.setPublishTime(LocalDateTime.now());
        }

        if (moment.getLikeCount() == null) moment.setLikeCount(0);
        momentService.save(moment);
        return R.ok();
    }

    // 更新
    @PutMapping("/update")
    public R update(@RequestBody Moment moment) {
        moment.setUpdateTime(LocalDateTime.now());
        momentService.updateById(moment);
        return R.ok();
    }

    // 删除
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        momentService.removeById(id);
        return R.ok();
    }

    // 批量删除
    @DeleteMapping("/batch")
    public R deleteBatch(@RequestBody List<Long> ids) {
        momentService.removeBatchByIds(ids);
        return R.ok();
    }

    // 获取详情
    @GetMapping("/{id}")
    public R info(@PathVariable Long id) {
        return R.ok().data("data", momentService.getById(id));
    }
}