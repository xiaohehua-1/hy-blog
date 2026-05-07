package com.heyi.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.Type;
import com.heyi.blog.mapper.mybatis.TypeMapper;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类业务实现类
 *
 * 提供分类的增删改查，包含名称唯一性校验。
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

    /**
     * 新增分类，校验ID和名称唯一性
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R saveType(Type type) {
        // 手动指定ID时检查是否冲突，避免主键重复异常
        if (type.getId() != null && this.getById(type.getId()) != null) {
            return R.error("该ID已存在，请重新输入");
        }

        Type exist = this.getOne(new LambdaQueryWrapper<Type>().eq(Type::getName, type.getName()));
        if (exist != null) {
            return R.error("该分类名称已存在");
        }
        return this.save(type) ? R.success() : R.error("添加失败");
    }

    /**
     * 更新分类，名称唯一性校验时排除自身
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R updateType(Type type) {
        // 排除自身ID后再判断名称是否与其他分类冲突
        Type exist = this.getOne(new LambdaQueryWrapper<Type>().eq(Type::getName, type.getName()));
        if (exist != null && !exist.getId().equals(type.getId())) {
            return R.error("该分类名称已存在");
        }
        return this.updateById(type) ? R.success() : R.error("更新失败");
    }

    /**
     * 删除分类（预留：可在此处增加"有文章则禁止删除"的判断）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public R deleteType(Long id) {
        return this.removeById(id) ? R.success() : R.error("删除失败");
    }
}