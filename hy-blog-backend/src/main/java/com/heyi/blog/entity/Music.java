package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 背景音乐实体，对应 t_music 表
 * 管理站点背景音乐列表，支持按歌名/歌手搜索和启用/禁用控制
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_music")
public class Music extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;                         // 主键ID
    private String title;                       // 音乐标题
    private String artist;                      // 艺术家
    private String fileName;                    // 文件名
    private String filePath;                    // 音乐文件路径
    private String coverPath;                   // 封面路径

    private Boolean enabled;                    // 是否启用
}