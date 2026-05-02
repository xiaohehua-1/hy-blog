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

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public IPage<User> pageAdminUsers(UserQuery query) {
        Page<User> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), User::getUsername, query.getUsername());
        wrapper.like(StringUtils.hasText(query.getNickname()), User::getNickname, query.getNickname());
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public R saveUser(User user) {
        if (checkUsernameUnique(user.getUsername(), null)) {
            return R.error("账号已存在");
        }

        // === 修改点 1: 使用 BCrypt 加密 ===
        // 自动加盐，每次加密结果都不一样，非常安全
        String targetPwd = BCrypt.hashpw(user.getPassword());
        user.setPassword(targetPwd);

        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }
        return this.save(user) ? R.success().message("创建成功") : R.error("创建失败");
    }

    @Override
    public R updateUser(User user) {
        if (checkUsernameUnique(user.getUsername(), user.getId())) {
            return R.error("账号已存在");
        }
        // 防止密码被误修改
        user.setPassword(null);
        return this.updateById(user) ? R.success().message("更新成功") : R.error("更新失败");
    }

    @Override
    public R updatePassword(com.heyi.blog.entity.dto.UpdateUserPwdDTO dto) {
        User user = this.getById(dto.getId());
        if (user == null) {
            return R.error("用户不存在");
        }

        // === 修改点 2: 校验旧密码 ===
        // checkpw(明文, 数据库里的密文)
        if (!BCrypt.checkpw(dto.getOldPassword(), user.getPassword())) {
            return R.error("旧密码错误");
        }

        // === 修改点 3: 加密新密码 ===
        String newPwd = BCrypt.hashpw(dto.getNewPassword());
        user.setPassword(newPwd);

        return this.updateById(user) ? R.success().message("密码修改成功") : R.error("修改失败");
    }

    @Override
    public R deleteUser(Long id) {
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            return R.error("禁止删除当前登录账号");
        }
        return this.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

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

        // === 修改点 4: 登录校验 ===
        // BCrypt 甚至不需要知道盐是什么，它会从密文中自动提取
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return R.error("用户名或密码错误");
        }

        StpUtil.login(user.getId());
        user.setPassword(null);
        return R.success().data("token", StpUtil.getTokenInfo().tokenValue).data("user", user);
    }

    private boolean checkUsernameUnique(String username, Long currentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (currentId != null) {
            wrapper.ne(User::getId, currentId);
        }
        return this.count(wrapper) > 0;
    }
}