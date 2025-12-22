package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.User;
import com.heyi.blog.entity.dto.LoginDTO;
import com.heyi.blog.entity.query.UserQuery;
import com.heyi.blog.utils.R;

public interface UserService extends IService<User> {

    /**
     * 登录业务
     * @param loginDTO 登录参数
     * @return 结果
     */
    R login(LoginDTO loginDTO);

    // 分页查询
    IPage<User> pageAdminUsers(UserQuery query);

    // 新增用户 (需要加密密码)
    R saveUser(User user);

    // 修改资料 (不含密码)
    R updateUser(User user);

    // 修改密码
    R updatePassword(com.heyi.blog.entity.dto.UpdateUserPwdDTO dto);

    // 删除用户
    R deleteUser(Long id);
}