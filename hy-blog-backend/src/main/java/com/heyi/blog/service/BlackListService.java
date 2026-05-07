/**
 * 黑名单管理服务接口
 *
 * 继承 IService，提供黑名单的基础 CRUD 能力。
 */
package com.heyi.blog.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.BlackList;

public interface BlackListService extends IService<BlackList> {
}