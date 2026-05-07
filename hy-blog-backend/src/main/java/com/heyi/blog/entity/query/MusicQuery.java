package com.heyi.blog.entity.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 音乐查询条件，支持按歌名和歌手模糊搜索
 */
@Data
public class MusicQuery implements Serializable {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String title;  // 按歌名搜索
    private String artist; // 按歌手搜索
}