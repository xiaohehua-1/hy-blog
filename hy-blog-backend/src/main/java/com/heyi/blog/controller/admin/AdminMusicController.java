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

    @Autowired
    private MusicService musicService;

    /**
     * 获取音乐列表
     */
    @PostMapping("/list")
    public R list(@RequestBody MusicQuery query) {
        IPage<Music> page = musicService.pageAdminMusics(query);
        return R.success().data("page", page);
    }

    /**
     * 添加音乐
     */
    @PostMapping("/save")
    @BlogLog("添加音乐")
    public R save(@RequestBody Music music) {
        // 保存逻辑通常需要特定校验，保留调用Service
        return musicService.saveMusic(music);
    }

    /**
     * 更新音乐（包含编辑信息 和 修改启用状态）
     */
    @PutMapping("/update")
    @BlogLog("更新音乐")
    public R update(@RequestBody Music music) {
        // 直接使用 MyBatis-Plus 的 updateById
        // 它可以处理部分更新（例如只传了 id 和 enabled），也可以处理全量编辑
        boolean result = musicService.updateById(music);

        if (result) {
            return R.success().message("更新成功");
        }
        return R.error().message("更新失败");
    }

    /**
     * 删除音乐
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除音乐")
    public R delete(@PathVariable Integer id) {
        // 删除逻辑可能涉及文件删除，建议保留调用Service
        return musicService.deleteMusic(id);
    }
}