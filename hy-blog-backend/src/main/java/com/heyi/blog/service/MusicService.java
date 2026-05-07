/**
 * 音乐管理服务接口
 *
 * 提供后台音乐列表的增删改查功能。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.utils.R;

public interface MusicService extends IService<Music> {

    /** 后台分页查询音乐列表（按标题/艺术家模糊搜索） */
    IPage<Music> pageAdminMusics(MusicQuery query);

    /** 新增音乐，需先上传文件 */
    R saveMusic(Music music);

    /** 更新音乐信息 */
    R updateMusic(Music music);

    /** 删除音乐记录（仅删数据库，不删磁盘文件） */
    R deleteMusic(Integer id);
}