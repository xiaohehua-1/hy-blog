package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.TagService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台标签管理控制器
 * 负责文章标签的增删改查，提供分页列表和全量列表两种查询
 */
@RestController
@RequestMapping("/admin/tag")
public class AdminTagController {

    @Autowired
    private TagService tagService;

    /**
     * 分页查询标签列表
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Tag> page = new Page<>(pageNum, pageSize);
        IPage<Tag> pageResult = tagService.pageTags(page);
        return R.success().data("page", pageResult);
    }

    /**
     * 获取全部标签（不分页，用于下拉选择等场景）
     */
    @GetMapping("/listAll")
    public R listAll() {
        return R.success().data("list", tagService.listAll());
    }

    /**
     * 新增标签
     */
    @PostMapping("/save")
    @BlogLog("添加标签")
    public R save(@RequestBody Tag tag) {
        return tagService.saveTag(tag);
    }

    /**
     * 更新标签名称
     */
    @PutMapping("/update")
    @BlogLog("更新标签")
    public R update(@RequestBody Tag tag) {
        return tagService.updateTag(tag);
    }

    /**
     * 删除标签（Service 层会同步清理博客-标签关联记录）
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除标签")
    public R delete(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }
}