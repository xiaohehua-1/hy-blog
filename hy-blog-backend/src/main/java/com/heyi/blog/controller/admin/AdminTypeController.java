package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Type;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章分类管理控制器
 * 负责分类的增删改查，提供分页列表和全量列表两种查询
 */
@RestController
@RequestMapping("/admin/type")
public class AdminTypeController {

    @Autowired
    private TypeService typeService;

    /**
     * 分页查询分类列表
     */
    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Type> page = new Page<>(pageNum, pageSize);
        IPage<Type> pageResult = typeService.pageTypes(page);
        return R.success().data("page", pageResult);
    }

    /**
     * 获取全部分类（不分页，用于下拉选择等场景）
     */
    @GetMapping("/listAll")
    public R listAll() {
        return R.success().data("list", typeService.listAll());
    }

    /**
     * 新增分类
     */
    @PostMapping("/save")
    @BlogLog("添加分类")
    public R save(@RequestBody Type type) {
        return typeService.saveType(type);
    }

    /**
     * 更新分类名称
     */
    @PutMapping("/update")
    @BlogLog("更新分类")
    public R update(@RequestBody Type type) {
        return typeService.updateType(type);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @BlogLog("删除分类")
    public R delete(@PathVariable Long id) {
        return typeService.deleteType(id);
    }
}