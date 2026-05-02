package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体类
 */
@Data
@TableName("t_sys_config")
public class SysConfig {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;                         // 主键ID
    private String author;                      // 站长名称
    private String aboutMeIntroduction;         // 自我介绍
    private String aboutMeContent;              // 内容
    private String aboutMeSkill;                // 我的技能
    private String siteProfile;                 // 个人简介
    private String siteLocation;                // 位置
    private String siteEmail;                   // 邮箱
    private String siteQq;                      // QQ
    private String siteWechat;                  // 微信
    private String csdnSession;                 // CSDN Session
    private String siteBilibili;                // B站链接
    private String siteGithub;                  // GitHub链接
    private String siteCsdn;                    // CSDN链接
}