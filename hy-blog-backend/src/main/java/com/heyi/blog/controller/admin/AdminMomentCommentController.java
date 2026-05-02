package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.MomentComment;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.MomentCommentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 后台动态评论管理 Controller
 * 作用：管理朋友圈动态下方的网友评论。
 */
@RestController
@RequestMapping("/admin/momentComment")
public class AdminMomentCommentController {

    @Autowired
    private MomentCommentService momentCommentService;

    /**
     * 分页获取动态评论列表（带关键字搜索）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize,
                  @RequestParam(required = false) String keyword) {

        Page<MomentComment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MomentComment> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(MomentComment::getContent, keyword)
                    .or()
                    .like(MomentComment::getNickname, keyword));
        }

        wrapper.orderByDesc(MomentComment::getCreateTime);
        return R.success().data("page", momentCommentService.page(page, wrapper));
    }

    /**
     * 删除动态评论
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除动态评论")
    public R delete(@PathVariable Long id) {
        boolean removed = momentCommentService.removeById(id);
        return removed ? R.success().message("删除成功") : R.error("删除失败");
    }

    /**
     * 站长后台回复动态评论
     * 【重要优化】：加入了站长头像兜底和归属关联校验。
     */
    @PostMapping("/reply")
    @BlogLog("后台回复动态评论")
    public R replyComment(@RequestBody MomentComment comment) {
        // 1. 打上站长标识（给前端渲染样式用的判断依据）
        comment.setAdminComment(true);
        comment.setCreateTime(LocalDateTime.now());
        comment.setDeleted(false);

        // 2. 补全站长基础信息
        if (comment.getNickname() == null || comment.getNickname().isEmpty()) {
            comment.setNickname("站长");
        }
        // 补上官方头像
        if (comment.getAvatar() == null || comment.getAvatar().isEmpty()) {
            comment.setAvatar("src/assets/images/me.jpg");
        }

        // 3. 关联校验：动态的评论回复，不仅要知道回复的是谁（ParentId），还要知道回复的是哪条动态（MomentId）
        if (comment.getMomentId() == null) {
            return R.error("回复失败：未找到对应的动态信息");
        }

        momentCommentService.save(comment);
        return R.success().message("回复成功");
    }
}