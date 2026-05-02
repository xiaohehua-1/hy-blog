package com.heyi.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Moment;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MomentService extends IService<Moment> {
    IPage<Moment> pageMoments(Integer current, Integer size, Boolean isAdmin, String content);
    Moment getRandomMoment();
    void likeMoment(Long id);
}