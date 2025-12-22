package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

import java.io.Serializable;

/**
 * 博客文章实体
 * 对应表: t_blog
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_blog")
public class Blog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 关联分类ID
    private Long typeId;

    // 标题
    private String title;

    // 内容 (LongText)
    private String content;

    // 描述
    private String description;

    // 首图地址
    private String firstPicture;

    // 标记是否开启评论 (数据库 bit(1) -> Java Boolean)
    private Boolean commentabled;

    // 是否开启赞赏
    private Boolean appreciation;

    // 是否发布 (0-草稿 1-发布)
    private Boolean published;

    // 是否推荐
    private Boolean recommend;

    // 是否开启转载声明
    private Boolean shareStatement;

    // 版权开启 (例如：0-原创 1-转载)
    private Integer copyright;

    // 浏览量
    private Integer views;

    // 评论数
    private Integer commentCount;

    @TableField(exist = false) // 告诉 MyBatis-Plus：数据库里没这个字段，别报错
    private String typeName;   // 存储分类名称

    @TableField(exist = false) // 告诉 MyBatis-Plus：数据库里没这个字段
    private List<Tag> tagList; // 存储标签列表
}