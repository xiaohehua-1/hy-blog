package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 网站访问量统计实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_views")
public class Views extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;                            // 主键ID
    private Long totalViews;                    // 总浏览量
    private Long yesterdayViews;                // 昨日浏览量
}