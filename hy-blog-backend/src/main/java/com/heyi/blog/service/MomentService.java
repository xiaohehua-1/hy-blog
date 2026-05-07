/**
 * 动态/说说服务接口
 *
 * 提供动态的分页查询、随机获取及点赞功能。
 * 前台用户只能看到已发布且非私密的动态。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Moment;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MomentService extends IService<Moment> {

    /** 分页查询动态，管理员可查看全部，前台过滤私密和未发布 */
    IPage<Moment> pageMoments(Integer current, Integer size, Boolean isAdmin, String content);

    /** 随机获取一条动态，用于前台展示 */
    Moment getRandomMoment();

    /** 点赞动态，直接累加计数 */
    void likeMoment(Long id);
}