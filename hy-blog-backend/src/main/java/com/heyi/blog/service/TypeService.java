/**
 * 文章分类服务接口
 *
 * 提供分类的增删改查，供后台管理和写文章时下拉选择使用。
 */
package com.heyi.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Type;
import com.heyi.blog.utils.R;

import java.util.List;

public interface TypeService extends IService<Type> {

    /** 后台分页查询分类列表 */
    IPage<Type> pageTypes(Page<Type> page);

    /** 获取全部分类，用于写文章下拉选择 */
    List<Type> listAll();

    // ---- 增删改 ----
    R saveType(Type type);
    R updateType(Type type);
    R deleteType(Long id);
}