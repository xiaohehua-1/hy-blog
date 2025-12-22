package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.query.FriendQuery;
import com.heyi.blog.service.FriendService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/friend")
public class AdminFriendController {

    @Autowired
    private FriendService friendService;

    // 列表查询
    @PostMapping("/list")
    public R list(@RequestBody FriendQuery query) {
        IPage<Friend> page = friendService.pageAdminFriends(query);
        return R.success().data("page", page);
    }

    // 新增
    @PostMapping("/save")
    @BlogLog("添加友链")
    public R save(@RequestBody Friend friend) {
        return friendService.saveFriend(friend);
    }

    // 修改
    @PutMapping("/update")
    @BlogLog("更新友链")
    public R update(@RequestBody Friend friend) {
        return friendService.updateFriend(friend);
    }

    // 删除
    @DeleteMapping("/{id}")
    @BlogLog("删除友链")
    public R delete(@PathVariable Long id) {
        return friendService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

    // 审核通过
    @PutMapping("/pass/{id}")
    @BlogLog("审核通过友链")
    public R pass(@PathVariable Long id) {
        return friendService.auditPass(id);
    }

    // 审核拒绝 (reason 通过 query param 或 body 传都行，这里简单点用 query param)
    @PutMapping("/reject/{id}")
    @BlogLog("拒绝友链申请")
    public R reject(@PathVariable Long id, @RequestParam String reason) {
        return friendService.auditReject(id, reason);
    }
}