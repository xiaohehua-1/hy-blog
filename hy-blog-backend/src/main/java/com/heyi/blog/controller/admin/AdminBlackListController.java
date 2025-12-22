package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.BlackList;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.BlackListService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/blacklist")
public class AdminBlackListController {

    @Autowired private BlackListService blackListService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BlackList> page = new Page<>(pageNum, pageSize);
        return R.success().data("page", blackListService.page(page, new LambdaQueryWrapper<BlackList>().orderByDesc(BlackList::getCreateTime)));
    }

    @PostMapping("/save")
    @BlogLog("添加黑名单IP")
    public R save(@RequestBody BlackList blackList) {
        return blackListService.save(blackList) ? R.success().message("添加成功") : R.error("添加失败");
    }

    @PutMapping("/update")
    @BlogLog("更新黑名单状态")
    public R update(@RequestBody BlackList blackList) {
        return blackListService.updateById(blackList) ? R.success().message("更新成功") : R.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @BlogLog("移除黑名单IP")
    public R delete(@PathVariable Long id) {
        return blackListService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}