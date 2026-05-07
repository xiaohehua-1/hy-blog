package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.mapper.mybatis.MusicMapper;
import com.heyi.blog.service.MusicService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 音乐管理业务实现类
 *
 * 提供音乐列表的后台增删改查。删除操作仅移除数据库记录，不删除磁盘文件（毕设简化处理）。
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    /**
     * 后台分页查询音乐，支持按标题/艺术家模糊搜索
     */
    @Override
    public IPage<Music> pageAdminMusics(MusicQuery query) {
        Page<Music> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(query.getTitle()), Music::getTitle, query.getTitle());
        wrapper.like(StringUtils.hasText(query.getArtist()), Music::getArtist, query.getArtist());

        wrapper.orderByDesc(Music::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 新增音乐，需确保文件已上传
     */
    @Override
    public R saveMusic(Music music) {
        if (!StringUtils.hasText(music.getFilePath())) {
            return R.error("请先上传音乐文件");
        }
        return this.save(music) ? R.success().message("添加成功") : R.error("添加失败");
    }

    @Override
    public R updateMusic(Music music) {
        return this.updateById(music) ? R.success().message("更新成功") : R.error("更新失败");
    }

    /**
     * 删除音乐（仅删数据库记录，磁盘文件保留）
     */
    @Override
    public R deleteMusic(Integer id) {
        return this.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }
}