package com.heyi.blog.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.dto.LoginDTO;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.mapper.UserMapper;
import com.heyi.blog.service.UserService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

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
        // 1. 校验用户名重复
        if (checkUsernameUnique(user.getUsername(), null)) {
            return R.error("账号已存在");
        }

        // 2. 密码加密 (MD5)
        String md5Pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Pwd);

        // 3. 默认头像
        if (!StringUtils.hasText(user.getAvatar())) {
            user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }

        return this.save(user) ? R.success().message("创建成功") : R.error("创建失败");
    }

    @Override
    public R updateUser(User user) {
        // 1. 校验用户名重复 (排除自己)
        if (checkUsernameUnique(user.getUsername(), user.getId())) {
            return R.error("账号已存在");
        }

        // 2. 强制置空密码，防止前端误传导致密码被篡改成明文
        user.setPassword(null);

        return this.updateById(user) ? R.success().message("更新成功") : R.error("更新失败");
    }

    @Override
    public R updatePassword(com.heyi.blog.entity.dto.UpdateUserPwdDTO dto) {
        // 1. 查询用户
        User user = this.getById(dto.getId());
        if (user == null) {
            return R.error("用户不存在");
        }

        // 2. 校验旧密码
        String oldMd5 = DigestUtils.md5DigestAsHex(dto.getOldPassword().getBytes(StandardCharsets.UTF_8));
        if (!oldMd5.equalsIgnoreCase(user.getPassword())) {
            return R.error("旧密码错误");
        }

        // 3. 设置新密码
        String newMd5 = DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(newMd5);

        return this.updateById(user) ? R.success().message("密码修改成功") : R.error("修改失败");
    }

    @Override
    public R deleteUser(Long id) {
        // 安全拦截：禁止删除当前登录的账号
        if (id.equals(StpUtil.getLoginIdAsLong())) {
            return R.error("禁止删除当前登录账号");
        }
        return this.removeById(id) ? R.success().message("删除成功") : R.error("删除失败");
    }

    // 登录逻辑 (保持原样)
    @Override
    public R login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = this.getOne(wrapper);
        if (user == null) return R.error("用户名或密码错误");
        String inputPwdMd5 = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (!inputPwdMd5.equalsIgnoreCase(user.getPassword())) return R.error("用户名或密码错误");
        StpUtil.login(user.getId());
        user.setPassword(null);
        return R.success().data("token", StpUtil.getTokenInfo().tokenValue).data("user", user);
    }

    // 辅助方法：检查用户名是否重复
    private boolean checkUsernameUnique(String username, Long currentId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        if (currentId != null) {
            wrapper.ne(User::getId, currentId); // 排除自己
        }
        return this.count(wrapper) > 0;
    }
}