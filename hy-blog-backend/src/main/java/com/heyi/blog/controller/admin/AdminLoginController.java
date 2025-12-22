package com.heyi.blog.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.entity.dto.LoginDTO;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private com.heyi.blog.service.UserService userService; // 确保注入了 UserService

    /**
     * 登录接口
     */
    @PostMapping("/login")
    @BlogLog("后台登录")
    public R login(@RequestBody @Validated LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    @BlogLog("退出登录")
    public R logout(HttpSession session) { // 1. 注入 Session
        // 2. 清除 Sa-Token 登录状态
        StpUtil.logout();
        // 3. 彻底销毁 Session (修复原项目 Bug)
        session.invalidate();
        return R.success().message("退出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public R info() {
        long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(loginId);
        user.setPassword(null); // 脱敏
        return R.success().data("user", user);
    }
}