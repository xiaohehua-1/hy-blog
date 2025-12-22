package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Message;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.MessageService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/message")
public class AdminMessageController {

    @Autowired private MessageService messageService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        return R.success().data("page", messageService.page(page, new LambdaQueryWrapper<Message>().orderByDesc(Message::getCreateTime)));
    }

    @DeleteMapping("/{id}")
    @BlogLog("删除留言")
    public R delete(@PathVariable Long id) {
        return messageService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}