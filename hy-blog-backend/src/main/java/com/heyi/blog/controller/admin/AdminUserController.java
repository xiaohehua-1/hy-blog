package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.service.UserService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理控制器
 * 负责管理员用户的增删改查及密码修改
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Autowired private UserService userService;

    /**
     * 分页查询用户列表，返回前对所有记录的密码字段脱敏
     */
    @PostMapping("/list")
    public R list(@RequestBody UserQuery query) {
        IPage<User> page = userService.pageAdminUsers(query);
        // 脱敏处理：绝不允许密码（即使是密文）泄露到前端
        page.getRecords().forEach(u -> u.setPassword(null));
        return R.success().data("page", page);
    }

    /**
     * 新增用户
     */
    @PostMapping("/save")
    @BlogLog("新增用户")
    public R save(@RequestBody User user) {
        return userService.saveUser(user);
    }

    /**
     * 更新用户资料（不含密码）
     */
    @PutMapping("/update")
    @BlogLog("更新用户资料")
    public R update(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 修改用户密码，需提供旧密码验证
     */
    @PutMapping("/password")
    @BlogLog("修改用户密码")
    public R updatePassword(@RequestBody com.heyi.blog.entity.dto.UpdateUserPwdDTO dto) {
        return userService.updatePassword(dto);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除用户")
    public R delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}