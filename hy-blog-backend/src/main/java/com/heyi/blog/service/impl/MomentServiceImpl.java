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

@Service
public class MomentServiceImpl extends ServiceImpl<MomentMapper, Moment> implements MomentService {

    @Autowired
    private MomentMapper momentMapper;

    @Autowired
    private MomentCommentMapper momentCommentMapper;

    @Override
    public IPage<Moment> pageMoments(Integer current, Integer size, Boolean isAdmin, String content) {
        Page<Moment> page = new Page<>(current, size);
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();

        // 1. 筛选条件
        if (!isAdmin) {
            wrapper.eq(Moment::getIsPrivate, false);
            wrapper.le(Moment::getPublishTime, LocalDateTime.now());
        }

        if (StringUtils.hasText(content)) {
            wrapper.like(Moment::getContent, content);
        }

        // 2. 排序
        wrapper.orderByDesc(Moment::getIsTop)
                .orderByDesc(Moment::getPublishTime);

        IPage<Moment> pageResult = this.page(page, wrapper);

        // 3. 填充评论 (现在 pageResult 已经存在了，不会报红了)
        if (pageResult.getRecords().size() > 0) {
            pageResult.getRecords().forEach(moment -> {
                // 查询该动态下的最新3条评论
                List<MomentComment> commentList = momentCommentMapper.selectList(
                        new LambdaQueryWrapper<MomentComment>()
                                .eq(MomentComment::getMomentId, moment.getId()) // 修复 Moment.java 后这里就有 getId 了
                                .eq(MomentComment::getDeleted, false)
                                .orderByDesc(MomentComment::getCreateTime)
                                .last("LIMIT 4")
                );
                moment.setComments(commentList); // 修复 Moment.java 后这里就有 setComments 了
            });
        }

        // 4. 返回处理好的结果 (不要再调用 this.page 了)
        return pageResult;
    }

    @Override
    public Moment getRandomMoment() {
        return momentMapper.getRandomMoment();
    }

    @Override
    public void likeMoment(Long id) {
        // 简单点赞实现，实际建议配合 Redis 防刷
        Moment moment = this.getById(id);
        if (moment != null) {
            moment.setLikeCount(moment.getLikeCount() + 1);
            this.updateById(moment);
        }
    }
}