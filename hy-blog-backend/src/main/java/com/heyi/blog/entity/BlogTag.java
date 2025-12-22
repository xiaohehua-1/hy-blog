package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 博客-标签关联实体 (ID 修正为 Long)
 */
@Data
@TableName("t_blog_tag")
public class BlogTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;      // <--- 这里必须是 Long

    private Long blogId;  // <--- 这里必须是 Long

    private Long tagId;   // <--- 这里必须是 Long
}