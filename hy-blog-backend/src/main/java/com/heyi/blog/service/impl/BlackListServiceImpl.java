package com.heyi.blog.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.BlackList;
import com.heyi.blog.mapper.mybatis.BlackListMapper;
import com.heyi.blog.service.BlackListService;
import org.springframework.stereotype.Service;

/**
 * 黑名单业务实现类
 *
 * 继承 MyBatis-Plus ServiceImpl，提供黑名单的基础 CRUD 能力。
 */
@Service
public class BlackListServiceImpl extends ServiceImpl<BlackListMapper, BlackList> implements BlackListService {
}