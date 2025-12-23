package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heyi.blog.entity.Type;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/front/type")
public class FrontTypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/list")
    public R list() {
        // 查询所有分类
        List<Type> list = typeService.list();
        return R.ok().data("list", list);
    }
}