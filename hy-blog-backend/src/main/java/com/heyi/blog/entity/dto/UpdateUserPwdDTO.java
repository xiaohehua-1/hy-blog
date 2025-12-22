package com.heyi.blog.entity.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UpdateUserPwdDTO implements Serializable {
    private Long id;
    private String oldPassword;
    private String newPassword;
}