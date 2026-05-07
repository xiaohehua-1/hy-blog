package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.service.MusicService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台音乐管理控制器
 * 负责背景音乐的增删改查及启用/禁用控制
 */
@RestController
@RequestMapping("/admin/music")
public class AdminMusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 分页查询音乐列表，支持按歌名和歌手搜索
     */
    @PostMapping("/list")
    public R list(@RequestBody MusicQuery query) {
        IPage<Music> page = musicService.pageAdminMusics(query);
        return R.success().data("page", page);
    }

    /**
     * 添加新音乐
     */
    @PostMapping("/save")
    @BlogLog("添加音乐")
    public R save(@RequestBody Music music) {
        return musicService.saveMusic(music);
    }

    /**
     * 更新音乐信息（支持全量编辑和部分字段如 enabled 状态切换）
     */
    @PutMapping("/update")
    @BlogLog("更新音乐")
    public R update(@RequestBody Music music) {
        // MyBatis-Plus updateById 自动忽略 null 字段，支持部分更新
        boolean result = musicService.updateById(music);
        if (result) {
            return R.success().message("更新成功");
        }
        return R.error().message("更新失败");
    }

    /**
     * 删除音乐（Service 层会同时清理服务器上的物理文件）
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除音乐")
    public R delete(@PathVariable Integer id) {
        return musicService.deleteMusic(id);
    }
}