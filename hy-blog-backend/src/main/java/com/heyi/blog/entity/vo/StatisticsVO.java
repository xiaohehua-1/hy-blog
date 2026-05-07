package com.heyi.blog.entity.vo;

import lombok.Data;

/**
 * 站点统计视图对象，用于页脚/侧边栏展示总体及今日数据
 */
@Data
public class StatisticsVO {
    // ===== 访问量 =====
    private Long totalViews;
    private Long todayViews;

    // ===== 博文 =====
    private Long totalBlogs;
    private Long todayBlogs;

    // ===== 评论 =====
    private Long totalComments;
    private Long todayComments;

    // ===== 留言 =====
    private Long totalMessages;
    private Long todayMessages;
}