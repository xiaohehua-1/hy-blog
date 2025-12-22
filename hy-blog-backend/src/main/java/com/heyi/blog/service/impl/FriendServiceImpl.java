package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.entity.query.FriendQuery;
import com.heyi.blog.mapper.FriendMapper;
import com.heyi.blog.service.FriendService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Override
    public IPage<Friend> pageAdminFriends(FriendQuery query) {
        Page<Friend> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();

        // 动态筛选状态
        wrapper.eq(query.getStatus() != null, Friend::getStatus, query.getStatus());

        // 按创建时间倒序
        wrapper.orderByDesc(Friend::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public R saveFriend(Friend friend) {
        // 管理员添加的，默认状态设为 1 (已通过)
        if (friend.getStatus() == null) {
            friend.setStatus(1);
        }
        return this.save(friend) ? R.success().message("添加成功") : R.error("添加失败");
    }

    @Override
    public R updateFriend(Friend friend) {
        return this.updateById(friend) ? R.success().message("更新成功") : R.error("更新失败");
    }

    @Override
    public R auditPass(Long id) {
        Friend friend = new Friend();
        friend.setId(id);
        friend.setStatus(1); // 1 = 通过
        friend.setReason(null); // 清空之前的拒绝理由
        return this.updateById(friend) ? R.success().message("审核通过") : R.error("操作失败");
    }

    @Override
    public R auditReject(Long id, String reason) {
        Friend friend = new Friend();
        friend.setId(id);
        friend.setStatus(-1); // -1 = 拒绝
        friend.setReason(reason);
        return this.updateById(friend) ? R.success().message("已拒绝") : R.error("操作失败");
    }
}