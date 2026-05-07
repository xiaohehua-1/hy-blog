package com.heyi.blog.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 仪表盘视图对象，聚合后台首页所需的各项统计数据
 * 包含概览数字、内容分类构成（饼图）、趋势统计（折线图）
 */
@Data
public class DashboardVO {
    // ===== 概览统计 =====
    private Long blogCount;         // 文章总数
    private Long tagCount;          // 标签总数
    private Long typeCount;         // 分类总数
    private Long commentCount;      // 评论总数
    private Long messageCount;      // 留言总数
    private Long viewCount;         // 总访问量
    private Long todayViews;        // 今日访问量
    private Long todayBlogCount;    // 今日新增文章
    private Long todayCommentCount; // 今日新增评论
    private Long todayMessageCount; // 今日新增留言

    // ===== 内容构成（饼图）=====
    private List<String> categoryNames; // 分类名称列表
    private List<Long> categoryValues;  // 各分类下文章数量

    // ===== 趋势统计（折线图）=====
    private List<String> dateList;      // X轴日期列表

    private List<Long> commentTrend;    // 评论趋势数据
    private List<Long> messageTrend;    // 留言趋势数据
    private List<Long> viewTrend;       // 访问量趋势数据
}