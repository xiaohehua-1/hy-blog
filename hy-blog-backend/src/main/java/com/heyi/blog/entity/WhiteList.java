package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * IP白名单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_white_list")
public class WhiteList extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;                         // 主键ID
    private String ip;                          // IP地址
    private Integer status;                     // 状态【-1禁止 1允许】
}