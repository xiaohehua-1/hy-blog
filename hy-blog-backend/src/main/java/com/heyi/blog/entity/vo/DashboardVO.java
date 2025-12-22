package com.heyi.blog.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {
    private Long blogCount;     // 文章数
    private Long tagCount;      // 标签数
    private Long typeCount;     // 分类数
    private Long commentCount;  // 评论数
    private Long messageCount;  // 留言数
    private Long viewCount;     // 总访问量
    private Long todayViews;
    private Long todayBlogCount;
    private Long todayCommentCount;
    private Long todayMessageCount;

    // 图表1：内容构成 (饼图)
    private List<String> categoryNames; // 分类名列表
    private List<Long> categoryValues;  // 分类对应文章数

    // 图表2：趋势统计 (折线图) - x轴
    private List<String> dateList;      // 日期列表 (如 ["周一", "周二"...])

    // 图表2：趋势统计 (折线图) - y轴数据
    private List<Long> commentTrend;    // 评论趋势
    private List<Long> messageTrend;    // 留言趋势
    private List<Long> viewTrend;       // 访问趋势
}