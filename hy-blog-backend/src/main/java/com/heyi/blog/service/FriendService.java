/**
 * 友链管理服务接口
 *
 * 提供友链的增删改查及审核功能。
 * 管理员添加的友链默认直接通过，前台提交的需审核。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.entity.query.FriendQuery;
import com.heyi.blog.utils.R;

public interface FriendService extends IService<Friend> {

    /** 后台分页查询友链，可按状态筛选 */
    IPage<Friend> pageAdminFriends(FriendQuery query);

    /** 新增友链，管理员操作默认状态为通过 */
    R saveFriend(Friend friend);
    R updateFriend(Friend friend);

    /** 审核通过，设置 status=1 并清空拒绝理由 */
    R auditPass(Long id);

    /** 审核拒绝，设置 status=-1 并记录拒绝理由 */
    R auditReject(Long id, String reason);
}