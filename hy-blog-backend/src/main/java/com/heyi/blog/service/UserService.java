/**
 * 用户服务接口
 *
 * 提供登录认证、用户 CRUD 及密码管理功能。
 * 密码使用 BCrypt 加密存储，通过 Sa-Token 管理登录态。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.dto.LoginDTO;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.utils.R;

public interface UserService extends IService<User> {

    /**
     * 登录认证：校验用户名密码，成功后返回 token 和用户信息
     */
    R login(LoginDTO loginDTO);

    /** 后台分页查询用户列表 */
    IPage<User> pageAdminUsers(UserQuery query);

    /** 新增用户，密码自动 BCrypt 加密 */
    R saveUser(User user);

    /** 更新用户资料，不含密码 */
    R updateUser(User user);

    /** 修改密码，需校验旧密码 */
    R updatePassword(com.heyi.blog.entity.dto.UpdateUserPwdDTO dto);

    /** 删除用户，禁止删除当前登录账号 */
    R deleteUser(Long id);
}