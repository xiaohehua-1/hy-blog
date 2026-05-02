/**
 * ================================================================================
 * 文件名：AdminLoginController.java
 * 项目名：HyBlog Backend
 *
 * 【核心职责】
 * 处理后台管理系统的登录认证相关请求，包括用户登录、退出登录、获取当前用户信息等功能，
 * 是整个系统安全认证的核心入口。
 *
 * 【主要功能模块】
 * 1. POST /admin/login - 用户登录接口，验证用户名密码并生成Sa-Token
 * 2. GET /admin/logout - 退出登录接口，清除登录状态和Session
 * 3. GET /admin/info - 获取当前登录用户信息接口，用于前端展示用户信息
 *
 * 【依赖关系】
 * - 依赖：UserService（用户业务逻辑服务，处理密码验证）
 * - 被依赖：前端登录页面（login.vue）
 * - 数据库：t_user（用户表）
 * - 框架：Sa-Token（权限认证框架）
 *
 * 【设计思路】
 * 这里我把登录相关的接口都放在同一个Controller里，这样管理起来比较集中。
 * 考虑到安全，我在登录时用了@Validated注解来做参数校验，防止空值传过来。
 * 退出登录时，我不仅清除了Sa-Token的状态，还加了Session.invalidate()，
 * 这样能彻底销毁会话，避免安全隐患。
 *
 * 作者：毕设项目开发团队
 * 创建时间：2025-2026
 * ================================================================================
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
    // [原注释] 确保注入了 UserService
    // 我的补充：这里我注入UserService来处理具体的登录业务逻辑，Controller层只负责请求接收和响应返回
    private com.heyi.blog.service.UserService userService;

    /**
     * 用户登录接口
     *
     * 【设计思路】
     * 这里我设计这个接口时，考虑到登录是系统的安全入口，所以做了双重保障：
     * 1. 先用@Validated注解做参数校验，确保username和password都不为空
     * 2. 然后把具体的密码验证逻辑交给UserService去处理
     * 3. 最后用@BlogLog注解记录这次登录操作到操作日志表
     *
     * @param loginDTO 登录请求参数，包含username（用户名）和password（密码）两个字段
     * @return 统一响应结果R，成功时包含token和user信息，失败时返回错误提示
     */
    @PostMapping("/login")
    @BlogLog("后台登录")
    public R login(@RequestBody @Validated LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 退出登录接口
     *
     * 【设计思路】
     * 这里我退出登录时，不仅调用了Sa-Token的logout()来清除登录状态，
     * 还特意加了session.invalidate()来彻底销毁Session，
     * 当时考虑到双重清除能更安全，避免任何遗留的会话信息导致安全问题。
     * 另外还用@BlogLog记录了这次退出操作。
     *
     * @param session HTTP会话对象，用于彻底销毁会话
     * @return 统一响应结果R，返回"退出成功"提示
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
     * 获取当前登录用户信息接口
     *
     * 【设计思路】
     * 这里我先通过StpUtil.getLoginIdAsLong()获取当前登录用户的ID，
     * 然后根据ID查询完整的用户信息，最后特意把password设为null，
     * 这一步很重要，是为了数据脱敏，绝对不能把密码返回给前端，
     * 即使是加密后的密码也不行，安全第一。
     *
     * @return 统一响应结果R，包含脱敏后的用户信息（不含密码）
     */
    @GetMapping("/info")
    public R info() {
        long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(loginId);
        user.setPassword(null); // 脱敏
        return R.success().data("user", user);
    }
}