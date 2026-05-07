package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.service.FriendService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台友链控制器
 * 提供友链列表展示（只返回审核通过的）和友链申请提交
 */
@RestController
@RequestMapping("/front/friend")
public class FrontFriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 获取所有审核通过的友链
     */
    @GetMapping("/list")
    public R list() {
        // status = 1 代表审核通过
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friend::getStatus, 1)
                .orderByDesc(Friend::getCreateTime);

        List<Friend> list = friendService.list(wrapper);
        return R.ok().data("list", list);
    }

    /**
     * 申请友链
     */
    @PostMapping("/apply")
    public R apply(@RequestBody Friend friend) {
        if (!StringUtils.hasText(friend.getBlogName())) return R.error("请输入博客名称");
        if (!StringUtils.hasText(friend.getBlogAddress())) return R.error("请输入博客地址");
        if (!StringUtils.hasText(friend.getPictureAddress())) return R.error("请输入图片地址");
        if (!StringUtils.hasText(friend.getBlogDescription())) return R.error("请输入博客描述");
        if (!StringUtils.hasText(friend.getEmail())) return R.error("请输入邮箱");

        // 设置初始状态为 0 (审核中)
        friend.setStatus(0);

        return friendService.save(friend) ? R.success() : R.error("申请失败");
    }
}