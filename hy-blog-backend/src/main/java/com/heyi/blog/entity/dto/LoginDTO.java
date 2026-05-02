package com.heyi.blog.entity.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求参数：用于管理员后台登录校验
 */
@Data
public class LoginDTO implements Serializable {

    @NotBlank(message = "用户名不能为空")           // 校验用户名非空
    private String username;                    // 用户名/账号

    @NotBlank(message = "密码不能为空")             // 校验密码非空
    private String password;                    // 登录密码
}