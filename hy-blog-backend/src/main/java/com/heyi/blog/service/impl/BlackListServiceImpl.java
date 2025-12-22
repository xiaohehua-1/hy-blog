package com.heyi.blog.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heyi.blog.entity.BlackList;
import com.heyi.blog.mapper.BlackListMapper;
import com.heyi.blog.service.BlackListService;
import org.springframework.stereotype.Service;

@Service
public class BlackListServiceImpl extends ServiceImpl<BlackListMapper, BlackList> implements BlackListService {
}