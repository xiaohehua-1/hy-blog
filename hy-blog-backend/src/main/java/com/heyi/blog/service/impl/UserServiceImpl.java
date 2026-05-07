package com.heyi.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt; // 1. 引入 Hutool 的 BCrypt
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.dto.LoginDTO;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.mapper.mybatis.UserMapper;
import com.heyi.blog.service.UserService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户业务实现类
 *
 * 提供登录认证、用户 CRUD 及密码管理。
 * 密码使用 BCrypt 加盐哈希存储，登录态通过 Sa-Token 管理。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 后台分页查询用户，支持按用户名/昵称模糊搜索
     */
    @Override
    public IPage<User> pageAdminUsers(UserQuery query) {
        Page<User> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), User::getUsername, query.getUsername());
        wrapper.like(StringUtils.hasText(query.getNickname()), User::getNickname, query.getNickname());
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(page, wrapper);
    }

    /**
     * 新增用户，校验账号唯一性后使用 BCrypt 加密密码
     */
    @Override
    public R saveUser(User user) {
        if (checkUsernameUnique(user.getUsername(), null)) {
            return R.error("账号已存在");
        }

        // BCrypt.hashpw 自动生成随机盐并嵌入密文，每次加密结果不同
        String targetPwd = BCrypt.hashpw(user.getPassword());
        user.setPassword(targetPwd);

        // 未上传头像时使用默认头像
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }
        return this.save(user) ? R.success().message("创建成功") : R.error("创建失败");
    }

    /**
     * 更新用户资料，密码置空防止被误覆盖
     */
    @Override
    public R updateUser(User user) {
        if (checkUsernameUnique(user.getUsername(), user.getId())) {
            return R.error("账号已存在");
        }
        // 将 password 设为 null，MyBatis-Plus 更新时会跳过 null 字段，避免清空密码
        user.setPassword(null);
        return this.updateById(user) ? R.success().message("更新成功") : R.error("更新失败");
    }

    /**
     * 修改密码：校验旧密码正确后，使用 BCrypt 加密新密码
     */
    @Override
    public R updatePassword(com.heyi.blog.entity.dto.UpdateUserPwdDTO dto) {
        User user = this.getById(dto.getId());
        if (user == null) {
            return R.error("用户不存在");
        }

        // BCrypt.checkpw 自动从密文中提取盐值，比对明文密码
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            return R.error("旧密码错误");
        }

        String newPwd = BCrypt.hashpw(dto.getNewPassword());
        user.setPassword(newPwd);

        return this.updateById(user) ? R.success().message("密码修改成功") : R.error("修改失败");
    }

    /**
     * 删除用户，禁止删除当前登录账号（防止把自己删掉后无法操作）
     */
    @Override
    public R deleteUser(Long id) {
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            return R.error("禁止删除当前登录账号");
        }
        return this.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

    /**
     * 登录认证：查用户 → 验密码 → 签发 token
     * 统一返回"用户名或密码错误"避免泄露账号存在性
     */
    @Override
    public R login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);

        if (user == null) {
            return R.error("用户名或密码错误");
        }

        // BCrypt.checkpw 从密文中自动提取盐值进行比对
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return R.error("用户名或密码错误");
        }

        // Sa-Token 登录，用户ID作为登录标识
        StpUtil.login(user.getId());
        // 返回前清除密码，避免敏感信息泄露到前端
        user.setPassword(null);
        return R.success().data("token", StpUtil.getTokenInfo().tokenValue).data("user", user);
    }

    /**
     * 校验用户名唯一性，编辑时排除自身ID
     */
    private boolean checkUsernameUnique(String username, Long currentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (currentId != null) {
            wrapper.ne(User::getId, currentId);
        }
        return this.count(wrapper) > 0;
    }
}