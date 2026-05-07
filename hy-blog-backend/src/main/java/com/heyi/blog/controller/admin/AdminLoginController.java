/**
 * 后台登录认证控制器
 * 处理管理员登录、退出及当前用户信息获取
 * 登录认证基于 Sa-Token 框架，密码校验委托给 UserService
 */
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
    private com.heyi.blog.service.UserService userService;

    /**
     * 管理员登录，验证用户名密码后签发 Sa-Token 并记录操作日志
     */
    @PostMapping("/login")
    @BlogLog("后台登录")
    public R login(@RequestBody @Validated LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 退出登录：清除 Sa-Token 登录状态并销毁 Session，双重保障会话安全
     */
    @GetMapping("/logout")
    @BlogLog("退出登录")
    public R logout(HttpSession session) {
        // 清除 Sa-Token 登录状态
        StpUtil.logout();
        // 彻底销毁 Session (修复原项目 Bug)
        session.invalidate();
        return R.success().message("退出成功");
    }

    /**
     * 获取当前登录用户信息，返回前对密码字段脱敏
     */
    @GetMapping("/info")
    public R info() {
        long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(loginId);
        user.setPassword(null); // 脱敏
        return R.success().data("user", user);
    }
}