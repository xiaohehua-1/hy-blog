package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.utils.R;

public interface MusicService extends IService<Music> {
    IPage<Music> pageAdminMusics(MusicQuery query);
    R saveMusic(Music music);
    R updateMusic(Music music);
    R deleteMusic(Integer id);
}