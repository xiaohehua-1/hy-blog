package com.heyi.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统操作日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sys_log")
public class SysLog extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;                         // 主键ID
    private String ip;                          // IP地址
    private String content;                     // 日志内容
    private String params;                      // 请求参数
    private String os;                          // 操作系统
    private String browser;                     // 浏览器类型
    private String spiderType;                  // 爬虫类型
    private String requestUrl;                  // 请求URL
    private String referer;                     // 引荐来源
}