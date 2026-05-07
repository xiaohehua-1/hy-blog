package com.heyi.blog.mapper.mybatis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heyi.blog.entity.Music;
import org.apache.ibatis.annotations.Mapper;

/**
 * 背景音乐 Mapper，对应表 t_music
 */
@Mapper
public interface MusicMapper extends BaseMapper<Music> {}