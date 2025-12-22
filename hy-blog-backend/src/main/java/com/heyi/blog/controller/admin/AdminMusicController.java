package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.Music;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.query.MusicQuery;
import com.heyi.blog.service.MusicService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/music")
public class AdminMusicController {

    @Autowired private MusicService musicService;

    @PostMapping("/list")
    public R list(@RequestBody MusicQuery query) {
        IPage<Music> page = musicService.pageAdminMusics(query);
        return R.success().data("page", page);
    }

    @PostMapping("/save")
    @BlogLog("添加音乐")
    public R save(@RequestBody Music music) {
        return musicService.saveMusic(music);
    }

    @PutMapping("/update")
    @BlogLog("更新音乐")
    public R update(@RequestBody Music music) {
        return musicService.updateMusic(music);
    }

    @DeleteMapping("/{id}")
    @BlogLog("删除音乐")
    public R delete(@PathVariable Integer id) {
        return musicService.deleteMusic(id);
    }
}