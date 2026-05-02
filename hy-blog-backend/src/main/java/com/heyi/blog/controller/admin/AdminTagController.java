package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Tag;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.TagService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/tag")
public class AdminTagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Tag> page = new Page<>(pageNum, pageSize);
        IPage<Tag> pageResult = tagService.pageTags(page);
        return R.success().data("page", pageResult);
    }

    @GetMapping("/listAll")
    public R listAll() {
        return R.success().data("list", tagService.listAll());
    }

    @PostMapping("/save")
    @BlogLog("添加标签")
    public R save(@RequestBody Tag tag) {
        return tagService.saveTag(tag);
    }

    @PutMapping("/update")
    @BlogLog("更新标签")
    public R update(@RequestBody Tag tag) {
        return tagService.updateTag(tag);
    }

    @DeleteMapping("/{id}")
    @BlogLog("删除标签")
    public R delete(@PathVariable Long id) {
        return tagService.deleteTag(id);
    }
}