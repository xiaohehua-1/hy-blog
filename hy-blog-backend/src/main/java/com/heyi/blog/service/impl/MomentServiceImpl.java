package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Moment;
import com.heyi.blog.entity.MomentComment;
import com.heyi.blog.mapper.mybatis.MomentCommentMapper;
import com.heyi.blog.mapper.mybatis.MomentMapper;
import com.heyi.blog.service.MomentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态/说说的业务实现类
 *
 * 提供动态分页查询（管理员/前台双视角）、随机获取及点赞功能。
 * 前台查询时过滤私密和未发布动态。
 */
@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Autowired
    private MomentMapper momentMapper;

    @Autowired
    private MomentCommentMapper momentCommentMapper;

    /**
     * 分页查询动态：
     * - 前台（isAdmin=false）：过滤私密动态和定时发布的动态
     * - 后台（isAdmin=true）：查看全部
     * - 置顶动态优先，再按发布时间倒序
     * - 每条动态附带最新4条评论
     */
    @Override
    public IPage<Moment> pageMoments(Integer current, Integer size, Boolean isAdmin, String content) {
        Page<Moment> page = new Page<>(current, size);
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();

        // 前台用户只看已发布的公开动态（发布时间 <= 当前时间）
        if (!isAdmin) {
            wrapper.eq(Moment::getIsPrivate, false);
            wrapper.le(Moment::getPublishTime, LocalDateTime.now());
        }

        if (StringUtils.hasText(content)) {
            wrapper.like(Moment::getContent, content);
        }

        // 置顶优先，同级别按发布时间倒序
        wrapper.orderByDesc(Moment::getIsTop)
                .orderByDesc(Moment::getPublishTime);

        IPage<Moment> pageResult = this.page(page, wrapper);

        // 为每条动态填充最新评论（LIMIT 4，最多展示4条）
        if (pageResult.getRecords().size() > 0) {
            pageResult.getRecords().forEach(moment -> {
                List<MomentComment> commentList = momentCommentMapper.selectList(
                        new LambdaQueryWrapper<MomentComment>()
                                .eq(MomentComment::getMomentId, moment.getId())
                                .eq(MomentComment::getDeleted, false)
                                .orderByDesc(MomentComment::getCreateTime)
                                .last("LIMIT 4")  // 仅取最新4条，避免评论过多影响性能
                );
                moment.setComments(commentList);
            });
        }

        return pageResult;
    }

    @Override
    public Moment getRandomMoment() {
        return momentMapper.getRandomMoment();
    }

    /**
     * 点赞：直接累加计数器，生产环境建议配合 Redis 防刷
     */
    @Override
    public void likeMoment(Long id) {
        Moment moment = this.getById(id);
        if (moment != null) {
            moment.setLikeCount(moment.getLikeCount() + 1);
            this.updateById(moment);
        }
    }
}