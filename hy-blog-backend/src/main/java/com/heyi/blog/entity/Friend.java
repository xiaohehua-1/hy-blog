package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友情链接实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_friend")
public class Friend extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private String blogAddress;                 // 博客地址
    private String blogName;                    // 博客名称
    private String blogDescription;             // 博客描述
    private String pictureAddress;              // 图片地址
    private String email;                       // 审核通知邮箱地址
    private Integer netStatus;                  // 友链网络状态
    private Integer status;                     // 审核状态
    private String reason;                      // 审核失败理由
}