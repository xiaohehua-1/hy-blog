package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Type;
import com.heyi.blog.utils.R;

import java.util.List;

public interface TypeService extends IService<Type> {
    // 分页查询
    IPage<Type> pageTypes(Page<Type> page);

    // 获取所有（用于写文章下拉选择）
    List<Type> listAll();

    // 增删改
    R saveType(Type type);
    R updateType(Type type);
    R deleteType(Long id);
}