package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.BlackList;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.BlackListService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台 IP 黑名单管理控制器
 * 管理被封禁 IP 的增删改查，拦截恶意刷评论/访问的用户
 */
@RestController
@RequestMapping("/admin/blacklist")
public class AdminBlackListController {

    @Autowired private BlackListService blackListService;

    /**
     * 分页查询黑名单列表，按创建时间倒序（最新拉黑的排最前）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BlackList> page = new Page<>(pageNum, pageSize);
        return R.success().data("page", blackListService.page(page, new LambdaQueryWrapper<BlackList>().orderByDesc(BlackList::getCreateTime)));
    }

    /**
     * 新增黑名单记录
     * @BlogLog 由 AOP 切面拦截，自动写入操作日志表
     */
    @PostMapping("/save")
    @BlogLog("添加黑名单IP")
    public R save(@RequestBody BlackList blackList) {
        return blackListService.save(blackList) ? R.success().message("添加成功") : R.error("添加失败");
    }

    /**
     * 更新黑名单状态（如临时解封、修改封禁原因）
     */
    @PutMapping("/update")
    @BlogLog("更新黑名单状态")
    public R update(@RequestBody BlackList blackList) {
        return blackListService.updateById(blackList) ? R.success().message("更新成功") : R.error("更新失败");
    }

    /**
     * 删除黑名单记录（解封 IP），id 通过 URL 路径传递
     */
    @DeleteMapping("/{id}")
    @BlogLog("移除黑名单IP")
    public R delete(@PathVariable Long id) {
        return blackListService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}