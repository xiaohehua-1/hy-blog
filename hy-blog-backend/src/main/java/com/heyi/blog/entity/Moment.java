package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 动态/说说实体，对应 t_moment 表
 * 支持定时发布、私密、置顶等特性
 */
@Data
@TableName("t_moment")
public class Moment implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String content;     // 内容
    private String images;      // 图片(逗号分隔)
    private String location;    // 位置
    private String extraUrl;     // 新增：外部链接
    private Boolean isPrivate;  // 是否私密
    private Boolean isTop;      // 是否置顶
    private Integer likeCount;  // 点赞数
    // 定时发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;

    // 格式化时间，解决前台显示T的问题
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    // 用于前端展示评论列表 (不对应数据库字段)
    @TableField(exist = false)
    private List<MomentComment> comments;
}