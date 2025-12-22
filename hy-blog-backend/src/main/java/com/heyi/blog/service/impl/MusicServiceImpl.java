package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.mapper.MusicMapper;
import com.heyi.blog.service.MusicService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    @Override
    public IPage<Music> pageAdminMusics(MusicQuery query) {
        Page<Music> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(query.getTitle()), Music::getTitle, query.getTitle());
        wrapper.like(StringUtils.hasText(query.getArtist()), Music::getArtist, query.getArtist());

        wrapper.orderByDesc(Music::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public R saveMusic(Music music) {
        // 简单校验
        if (!StringUtils.hasText(music.getFilePath())) {
            return R.error("请先上传音乐文件");
        }
        return this.save(music) ? R.success().message("添加成功") : R.error("添加失败");
    }

    @Override
    public R updateMusic(Music music) {
        return this.updateById(music) ? R.success().message("更新成功") : R.error("更新失败");
    }

    @Override
    public R deleteMusic(Integer id) {
        // 这里实际上还应该删除磁盘上的文件，为了毕设简单起见，暂时只删数据库记录
        return this.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}