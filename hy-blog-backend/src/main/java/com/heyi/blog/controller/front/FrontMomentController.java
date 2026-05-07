package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Moment;
import com.heyi.blog.entity.MomentComment;
import com.heyi.blog.mapper.mybatis.MomentCommentMapper;
import com.heyi.blog.service.MomentService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 前台动态（朋友圈）控制器
 * 提供动态列表、随机动态、点赞及评论的发布与查看
 */
@RestController
@RequestMapping("/front/moment")
public class FrontMomentController {

    @Autowired
    private MomentService momentService;

    @Autowired
    private MomentCommentMapper momentCommentMapper;

    /**
     * 获取公开动态分页列表（排除私密动态）
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer current,
                  @RequestParam(defaultValue = "9") Integer size) {
        IPage<Moment> page = momentService.pageMoments(current, size, false, null);
        return R.ok().data("page", page);
    }

    /**
     * 随机获取一条公开动态
     */
    @GetMapping("/random")
    public R random() {
        Moment moment = momentService.getRandomMoment();
        if (moment == null) return R.error("暂无动态");
        return R.ok().data("data", moment);
    }

    /**
     * 动态点赞（点赞数 +1）
     */
    @PostMapping("/like/{id}")
    public R like(@PathVariable Long id) {
        momentService.likeMoment(id);
        return R.ok();
    }

    /**
     * 提交动态评论，补全默认昵称和头像后写入 t_moment_comment 表
     */
    @PostMapping("/comment")
    public R saveComment(@RequestBody MomentComment comment) {
        if (comment.getMomentId() == null) return R.error("参数错误：动态ID不能为空");
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return R.error("写点什么吧~");
        }

        if (comment.getNickname() == null || comment.getNickname().isEmpty()) comment.setNickname("路人甲");
        if (comment.getAvatar() == null || comment.getAvatar().isEmpty()) {
            comment.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }

        comment.setCreateTime(LocalDateTime.now());
        comment.setAdminComment(false);
        comment.setDeleted(false);

        momentCommentMapper.insert(comment);
        return R.ok().message("评论成功");
    }

    /**
     * 查询指定动态的评论分页列表（仅返回未删除的）
     */
    @GetMapping("/comment/list/{momentId}")
    public R getCommentList(@PathVariable Long momentId,
                            @RequestParam(defaultValue = "1") Integer current,
                            @RequestParam(defaultValue = "10") Integer size) {
        Page<MomentComment> page = new Page<>(current, size);
        LambdaQueryWrapper<MomentComment> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(MomentComment::getMomentId, momentId);
        wrapper.eq(MomentComment::getDeleted, false);
        // 如果想看子评论，这里可能需要调整查询逻辑或者前端递归展示
        // 这里暂时查所有按时间倒序
        wrapper.orderByDesc(MomentComment::getCreateTime);

        IPage<MomentComment> pageResult = momentCommentMapper.selectPage(page, wrapper);
        return R.ok().data("page", pageResult);
    }
}