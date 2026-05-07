package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Friend;
import com.heyi.blog.entity.query.FriendQuery;
import com.heyi.blog.mapper.mybatis.FriendMapper;
import com.heyi.blog.service.FriendService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;

/**
 * 友链管理业务实现类
 *
 * 提供友链 CRUD 及审核功能。管理员添加的友链默认状态为"通过"。
 * 审核状态：1=通过，-1=拒绝。
 */
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    /**
     * 后台分页查询友链，可按审核状态筛选
     */
    @Override
    public IPage<Friend> pageAdminFriends(FriendQuery query) {
        Page<Friend> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Friend> wrapper = new LambdaQueryWrapper<>();

        // 动态筛选：仅当传入 status 时才添加条件
        wrapper.eq(query.getStatus() != null, Friend::getStatus, query.getStatus());

        wrapper.orderByDesc(Friend::getCreateTime);

        return this.page(page, wrapper);
    }

    /**
     * 新增友链，管理员操作默认直接通过（status=1）
     */
    @Override
    public R saveFriend(Friend friend) {
        if (friend.getStatus() == null) {
            friend.setStatus(1);
        }
        return this.save(friend) ? R.success().message("添加成功") : R.error("添加失败");
    }

    @Override
    public R updateFriend(Friend friend) {
        return this.updateById(friend) ? R.success().message("更新成功") : R.error("更新失败");
    }

    /**
     * 审核通过：设置 status=1，清空之前的拒绝理由
     */
    @Override
    public R auditPass(Long id) {
        Friend friend = new Friend();
        friend.setId(id);
        friend.setStatus(1);
        friend.setReason(null);
        return this.updateById(friend) ? R.success().message("审核通过") : R.error("操作失败");
    }

    /**
     * 审核拒绝：设置 status=-1，记录拒绝理由
     */
    @Override
    public R auditReject(Long id, String reason) {
        Friend friend = new Friend();
        friend.setId(id);
        friend.setStatus(-1);
        friend.setReason(reason);
        return this.updateById(friend) ? R.success().message("已拒绝") : R.error("操作失败");
    }
}