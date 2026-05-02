package com.heyi.blog.entity.vo;

import lombok.Data;

@Data
public class StatisticsVO {
    // 访问量
    private Long totalViews;
    private Long todayViews;

    // 博文
    private Long totalBlogs;
    private Long todayBlogs;

    // 评论
    private Long totalComments;
    private Long todayComments;

    // 留言
    private Long totalMessages;
    private Long todayMessages;
}