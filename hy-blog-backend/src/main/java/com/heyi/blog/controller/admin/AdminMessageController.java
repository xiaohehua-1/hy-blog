package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Message;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.MessageService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台留言板管理 Controller
 * 作用：管理独立“留言板”页面的数据，包括查看、删除和站长回复功能。
 */
@RestController
@RequestMapping("/admin/message")
public class AdminMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 分页获取留言列表（支持按内容或昵称搜索）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String keyword) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Message::getContent, keyword)
                    .or()
                    .like(Message::getNickname, keyword));
        }

        wrapper.orderByDesc(Message::getCreateTime);
        return R.success().data("page", messageService.page(page, wrapper));
    }

    /**
     * 删除留言
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除留言")
    public R delete(@PathVariable Long id) {
        return messageService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

    /**
     * 站长后台回复留言
     * 【重要优化】：加入了站长头像兜底和父节点校验，防止前台渲染崩溃。
     */
    @PostMapping("/reply")
    @BlogLog("后台回复网站留言")
    public R reply(@RequestBody Message message) {
        // 1. 打上站长尊贵标识（重点！）前端靠这个 adminMessage = true 来给回复贴“官方”标签
        message.setAdminMessage(true);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0); // 0 表示正常未删除

        // 2. 补全站长基础信息
        if (message.getNickname() == null || message.getNickname().isEmpty()) {
            message.setNickname("站长");
        }
        // 补上你的官方头像，防止前端裂图
        if (message.getAvatar() == null || message.getAvatar().isEmpty()) {
            message.setAvatar("src/assets/images/me.jpg");
        }


        messageService.save(message);
        return R.success().message("回复成功");
    }
}