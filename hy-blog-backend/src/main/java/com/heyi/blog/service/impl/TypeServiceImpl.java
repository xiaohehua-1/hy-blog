package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Type;
import com.heyi.blog.mapper.TypeMapper;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类业务实现类 (纯后台版)
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

    @Override
    public IPage<Type> pageTypes(Page<Type> page) {
        return this.page(page, new LambdaQueryWrapper<Type>().orderByDesc(Type::getId));
    }

    @Override
    public List<Type> listAll() {
        return this.list();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveType(Type type) {
        // 1. 检查 ID 是否重复 (防止报错刷屏)
        if (type.getId() != null && this.getById(type.getId()) != null) {
            return R.error("该ID已存在，请重新输入");
        }

        // 2. 检查名称是否重复
        Type exist = this.getOne(new LambdaQueryWrapper<Type>().eq(Type::getName, type.getName()));
        if (exist != null) {
            return R.error("该分类名称已存在");
        }
        return this.save(type) ? R.success() : R.error("添加失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateType(Type type) {
        // 1. 检查名称是否重复 (排除自己)
        Type exist = this.getOne(new LambdaQueryWrapper<Type>().eq(Type::getName, type.getName()));
        if (exist != null && !exist.getId().equals(type.getId())) {
            return R.error("该分类名称已存在");
        }
        return this.updateById(type) ? R.success() : R.error("更新失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteType(Long id) {
        // 未来可以在这里加逻辑：如果该分类下有文章，禁止删除
        return this.removeById(id) ? R.success() : R.error("删除失败");
    }
}