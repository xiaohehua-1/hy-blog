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

@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Autowired private CommentService commentService;
    @Autowired private BlogService blogService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        // 1. 分页查询评论
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> commentPage = commentService.page(page, new LambdaQueryWrapper<Comment>().orderByDesc(Comment::getCreateTime));

        // 2. 转换为 VO，填充文章标题 (方便管理员知道是在哪篇文章下的评论)
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

    @DeleteMapping("/{id}")
    @BlogLog("删除评论")
    public R delete(@PathVariable Long id) {
        return commentService.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}