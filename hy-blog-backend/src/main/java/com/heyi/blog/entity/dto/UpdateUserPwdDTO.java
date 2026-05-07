package com.heyi.blog.entity.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 修改密码请求参数，用于管理员个人密码更新
 */
@Data
public class UpdateUserPwdDTO implements Serializable {
    private Long id;                // 用户ID
    private String oldPassword;     // 旧密码
    private String newPassword;     // 新密码
}