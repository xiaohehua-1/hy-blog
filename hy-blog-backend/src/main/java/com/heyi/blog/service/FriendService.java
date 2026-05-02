package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.entity.query.FriendQuery;
import com.heyi.blog.utils.R;

public interface FriendService extends IService<Friend> {

    // 分页查询
    IPage<Friend> pageAdminFriends(FriendQuery query);

    // 新增/修改 (管理员操作，默认直接通过)
    R saveFriend(Friend friend);
    R updateFriend(Friend friend);

    // 审核通过
    R auditPass(Long id);

    // 审核拒绝
    R auditReject(Long id, String reason);
}