package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章评论实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
public class Comment extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private Long rootCommentId;                 // 根评论ID
    private Long parentCommentId;               // 父评论ID
    private Long blogId;                        // 博客ID
    private String nickname;                    // 昵称
    private String email;                       // 邮箱
    private String content;                     // 评论内容
    private String address;                     // 评论者博客地址
    private String avatar;                      // 头像
    private Boolean adminComment;               // 是否为管理员评论
}