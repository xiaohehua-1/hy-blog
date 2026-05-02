package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.service.UserService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired private UserService userService;

    @PostMapping("/list")
    public R list(@RequestBody UserQuery query) {
        IPage<User> page = userService.pageAdminUsers(query);
        // 脱敏处理，不返回密码
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.success().data("page", page);
    }

    @PostMapping("/save")
    @BlogLog("新增用户")
    public R save(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/update")
    @BlogLog("更新用户资料")
    public R update(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/password")
    @BlogLog("修改用户密码")
    public R updatePassword(@RequestBody com.heyi.blog.entity.dto.UpdateUserPwdDTO dto) {
        return userService.updatePassword(dto);
    }

    @DeleteMapping("/{id}")
    @BlogLog("删除用户")
    public R delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}