package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heyi.blog.entity.Type;
import com.heyi.blog.service.TypeService;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/front/type")
public class FrontTypeController {

    @Autowired
    private TypeService typeService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // Redis Key 常量
    private static final String KEY_FRONT_TYPES = "blog:front:types";

    @GetMapping("/list")
    public R list() {
        // 1. 尝试从缓存获取
        String cacheJson = redisTemplate.opsForValue().get(KEY_FRONT_TYPES);
        if (StringUtils.hasText(cacheJson)) {
            try {
                List<Type> cachedList = objectMapper.readValue(cacheJson, new TypeReference<List<Type>>() {});
                return R.ok().data("list", cachedList);
            } catch (JsonProcessingException e) {
                redisTemplate.delete(KEY_FRONT_TYPES); // 缓存解析失败，清理掉
            }
        }

        // 2. 缓存不存在，查询所有分类
        List<Type> list = typeService.list();

        // 3. 写入缓存 (有效期 1 小时)
        try {
            redisTemplate.opsForValue().set(KEY_FRONT_TYPES, objectMapper.writeValueAsString(list), 1, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {}

        return R.ok().data("list", list);
    }
}