package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IP黑名单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_black_list")
public class BlackList extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private String ip;                          // IP地址
    private Boolean status;                     // 状态 (1:封禁 0:禁用)
}