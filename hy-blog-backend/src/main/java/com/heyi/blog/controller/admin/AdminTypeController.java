package com.heyi.blog.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heyi.blog.entity.Type;
import com.heyi.blog.entity.annotation.BlogLog;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/type")
public class AdminTypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/list")
    public R list(@RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Type> page = new Page<>(pageNum, pageSize);
        IPage<Type> pageResult = typeService.pageTypes(page);
        return R.success().data("page", pageResult);
    }

    @GetMapping("/listAll")
    public R listAll() {
        return R.success().data("list", typeService.listAll());
    }

    @PostMapping("/save")
    @BlogLog("添加分类")
    public R save(@RequestBody Type type) {
        return typeService.saveType(type);
    }

    @PutMapping("/update")
    @BlogLog("更新分类")
    public R update(@RequestBody Type type) {
        return typeService.updateType(type);
    }

    @DeleteMapping("/{id}")
    @BlogLog("删除分类")
    public R delete(@PathVariable Long id) {
        return typeService.deleteType(id);
    }
}