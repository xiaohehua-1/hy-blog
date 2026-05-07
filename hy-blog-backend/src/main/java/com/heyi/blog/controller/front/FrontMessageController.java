package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Message;
import com.heyi.blog.service.MessageService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 前台留言板控制器
 * 提供留言树形列表查询（含子回复装配）和留言提交
 */
@RestController
@RequestMapping("/front/message")
public class FrontMessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取留言列表 (树形结构)
     * 【修复】：返回格式改为标准的 Page 对象，适配通用组件
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size) {

        // 1. 分页查询所有"一级留言" (rootMessageId 为 NULL)
        Page<Message> page = new Page<>(current, size);
        LambdaQueryWrapper<Message> rootWrapper = new LambdaQueryWrapper<>();
        rootWrapper.isNull(Message::getRootMessageId)
                .orderByDesc(Message::getCreateTime);

        Page<Message> rootPage = messageService.page(page, rootWrapper);
        List<Message> roots = rootPage.getRecords();

        if (roots.isEmpty()) {
            return R.ok().data("page", rootPage);
        }

        // 2. 收集一级留言ID，查询子留言
        List<Long> rootIds = roots.stream().map(Message::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Message> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(Message::getRootMessageId, rootIds)
                .orderByAsc(Message::getCreateTime);
        List<Message> children = messageService.list(childWrapper);

        // 3. 内存中组装树形结构
        Map<Long, List<Message>> childrenMap = children.stream()
                .collect(Collectors.groupingBy(Message::getRootMessageId));

        roots.forEach(root -> {
            List<Message> myChildren = childrenMap.get(root.getId());
            if (myChildren != null) {
                myChildren.forEach(child -> {
                    // 处理回复对象的昵称
                    if (child.getParentMessageId() != null) {
                        child.setReplyNickname(findNicknameById(child.getParentMessageId(), roots, children));
                    }
                });
                root.setChildren(myChildren);
            }
        });

        // 4. 【关键修改点】重新封装 Page 对象返回
        Page<Message> resultPage = new Page<>();
        resultPage.setRecords(roots);
        resultPage.setTotal(rootPage.getTotal());
        resultPage.setCurrent(rootPage.getCurrent());
        resultPage.setSize(rootPage.getSize());

        return R.ok().data("page", resultPage);
    }

    // 辅助方法：在内存列表中查找昵称
    private String findNicknameById(Long id, List<Message> roots, List<Message> children) {
        for (Message m : children) {
            if (m.getId().equals(id)) return m.getNickname();
        }
        for (Message m : roots) {
            if (m.getId().equals(id)) return m.getNickname();
        }
        return "未知用户";
    }

    /**
     * 提交留言
     */
    @PostMapping("/save")
    public R save(@RequestBody Message message) {
        if (!StringUtils.hasText(message.getContent())) return R.error("请输入内容");
        if (!StringUtils.hasText(message.getNickname())) return R.error("请输入昵称");

        // 补全默认信息
        if (!StringUtils.hasText(message.getAvatar())) {
            message.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }

        message.setCreateTime(LocalDateTime.now());
        message.setAdminMessage(false); // 默认为游客

        messageService.save(message);
        return R.ok().message("留言成功");
    }
}