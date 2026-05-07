package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.vo.CommentVO;
import com.heyi.blog.service.BlogService;
import com.heyi.blog.service.CommentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台文章评论管理控制器
 * 负责评论的关键字搜索、删除及站长回复，查询时通过 VO 补全文章标题
 */
@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    /**
     * 分页查询评论列表，支持按内容/昵称模糊搜索
     * 查询后转为 CommentVO 补全文章标题，避免 JOIN 联表 SQL
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String keyword) {

        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Comment::getContent, keyword)
                    .or()
                    .like(Comment::getNickname, keyword));
        }

        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> commentPage = commentService.page(page, wrapper);

        // 转换为 VO，填充文章标题
        IPage<CommentVO> voPage = commentPage.convert(comment -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);
            if (comment.getBlogId() != null) {
                Blog blog = blogService.getById(comment.getBlogId());
                if (blog != null) {
                    vo.setBlogTitle(blog.getTitle());
                }
            }
            return vo;
        });

        return R.success().data("page", voPage);
    }

    /**
     * 删除违规评论
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除文章评论")
    public R delete(@PathVariable Long id) {
        return commentService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

    /**
     * 站长后台回复评论
     * 自动设置管理员标识、补全昵称/头像，校验文章归属防止空指针
     */
    @PostMapping("/reply")
    @BlogLog("后台回复文章评论")
    public R reply(@RequestBody Comment comment) {
        // 1. 设置站长标识，前端靠这个布尔值来渲染醒目的"站长"标签
        comment.setAdminComment(true);
        comment.setCreateTime(LocalDateTime.now());
        comment.setDeleted(0);

        // 2. 补全站长基础信息
        if (comment.getNickname() == null || comment.getNickname().isEmpty()) {
            comment.setNickname("站长");
        }
        // 补上官方头像
        if (comment.getAvatar() == null || comment.getAvatar().isEmpty()) {
            comment.setAvatar("src/assets/images/me.jpg");
        }
        // 3. 关联校验：回复文章评论，必须带上对应的博客 ID 和父级评论 ID
        if (comment.getBlogId() == null) {
            return R.error("回复失败：未找到对应的文章信息");
        }

        commentService.save(comment);
        return R.success().message("回复成功");
    }
}