package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heyi.blog.entity.Music;
import com.heyi.blog.service.MusicService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/front/music")
public class FrontMusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 获取播放列表 (只返回已启用的)
     */
    @GetMapping("/list")
    public R list() {
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Music::getEnabled, true) // 只查启用的
                .orderByDesc(Music::getCreateTime); // 按上传时间倒序

        List<Music> list = musicService.list(wrapper);
        return R.ok().data("list", list);
    }
}