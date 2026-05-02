package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.service.CommentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/front/comment")
public class FrontCommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取博客评论列表
     * 【修改点】：
     * 1. 路径改为 "/list" (去掉 /{blogId})
     * 2. 参数改为 @RequestParam (接收 ?blogId=xx)
     * 3. 返回值封装为 Page 对象 (适配通用组件)
     */
    @GetMapping("/list")
    public R list(@RequestParam(required = false) Long blogId,
                  @RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "10") Integer size) {

        // 1. 校验必填参数
        if (blogId == null) {
            return R.error("参数错误：缺少 blogId");
        }

        // 2. 查询该博客下的一级评论
        Page<Comment> page = new Page<>(current, size);
        LambdaQueryWrapper<Comment> rootWrapper = new LambdaQueryWrapper<>();
        rootWrapper.eq(Comment::getBlogId, blogId)
                .isNull(Comment::getRootCommentId)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> rootPage = commentService.page(page, rootWrapper);
        List<Comment> roots = rootPage.getRecords();

        if (roots.isEmpty()) {
            return R.ok().data("page", rootPage);
        }

        // 3. 查询子评论并组装 (保持原有逻辑)
        List<Long> rootIds = roots.stream().map(Comment::getId).collect(Collectors.toList());
        LambdaQueryWrapper<Comment> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.in(Comment::getRootCommentId, rootIds)
                .orderByAsc(Comment::getCreateTime);
        List<Comment> children = commentService.list(childWrapper);

        Map<Long, List<Comment>> childrenMap = children.stream()
                .collect(Collectors.groupingBy(Comment::getRootCommentId));

        roots.forEach(root -> {
            List<Comment> myChildren = childrenMap.get(root.getId());
            if (myChildren != null) {
                myChildren.forEach(child -> {
                    if (child.getParentCommentId() != null) {
                        child.setReplyNickname(findNicknameById(child.getParentCommentId(), roots, children));
                    }
                });
                root.setChildren(myChildren);
            }
        });

        // 4. 【关键】重新封装为 Page 对象，匹配 MessageList 组件的数据结构
        Page<Comment> resultPage = new Page<>();
        resultPage.setRecords(roots);
        resultPage.setTotal(rootPage.getTotal());
        resultPage.setCurrent(rootPage.getCurrent());
        resultPage.setSize(rootPage.getSize());

        return R.ok().data("page", resultPage);
    }

    private String findNicknameById(Long id, List<Comment> roots, List<Comment> children) {
        for (Comment c : children) { if (c.getId().equals(id)) return c.getNickname(); }
        for (Comment c : roots) { if (c.getId().equals(id)) return c.getNickname(); }
        return "未知用户";
    }

    /**
     * 提交评论
     */
    @PostMapping("/save")
    public R save(@RequestBody Comment comment) {
        if (!StringUtils.hasText(comment.getContent())) return R.error("请输入评论内容");
        if (comment.getBlogId() == null) return R.error("非法请求：缺少文章ID");

        if (!StringUtils.hasText(comment.getNickname())) comment.setNickname("匿名用户");
        if (!StringUtils.hasText(comment.getAvatar())) comment.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");

        comment.setCreateTime(LocalDateTime.now());
        comment.setAdminComment(false);

        boolean save = commentService.save(comment);
        return save ? R.ok().message("评论成功") : R.error("评论失败");
    }
}