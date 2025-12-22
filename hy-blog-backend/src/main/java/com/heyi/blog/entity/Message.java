package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 留言板实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_message")
public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private String nickname;                    // 昵称
    private String email;                       // 邮箱
    private String content;                     // 留言内容
    private String avatar;                      // 头像
    private String address;                     // 留言者博客地址
    private Long rootMessageId;                 // 根留言ID
    private Long parentMessageId;               // 父留言ID
    private Boolean adminMessage;               // 是否为管理员留言
}