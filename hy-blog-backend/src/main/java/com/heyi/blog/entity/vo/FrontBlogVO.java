package com.heyi.blog.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.heyi.blog.entity.Tag;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 前台博客列表展示对象
 */
@Data
public class FrontBlogVO {
    private Long id;                // 文章ID
    private String title;           // 标题 (第一栏)
    private String description;     // 描述 (第二栏)
    private String author;          // 作者 (第三栏，来自系统配置)
    private String firstPicture;    // 首图 (决定是否左文右图)

    // 格式化时间 (第四栏第一部分)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer views;          // 浏览量 (第四栏第一部分)
    private Integer commentCount;   // 评论数 (第四栏第一部分)

    private List<Tag> tagList;      // 标签列表 (第四栏第二部分)
    private Integer copyright;      // 版权类型 (第四栏第二部分)
    private Boolean isOriginal;     // 辅助字段：是否原创
}