package com.heyi.blog.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heyi.blog.entity.Blog;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.entity.Message; // 引入 Message
import com.heyi.blog.entity.vo.StatisticsVO;
import com.heyi.blog.service.BlogService;
import com.heyi.blog.service.CommentService;
import com.heyi.blog.service.MessageService; // 引入 MessageService
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/front/statistics")
public class FrontStatisticsController {

    @Autowired private StringRedisTemplate redisTemplate;
    @Autowired private BlogService blogService;
    @Autowired private CommentService commentService;
    @Autowired private MessageService messageService; // 注入留言服务

    // Redis Key 定义
    private static final String KEY_VIEW_TOTAL = "blog:view:total";
    private static final String KEY_VIEW_DAY_PREFIX = "blog:view:day:";

    /**
     * 获取站点统计信息
     */
    @GetMapping("/info")
    public R getInfo() {
        StatisticsVO vo = new StatisticsVO();

        // 获取今日日期字符串 (yyyyMMdd) 用于 Redis Key
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 获取今日开始时间 (用于数据库查询今日新增)
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        // 1. 浏览量 (Redis)
        String totalViewsStr = redisTemplate.opsForValue().get(KEY_VIEW_TOTAL);
        vo.setTotalViews(totalViewsStr != null ? Long.parseLong(totalViewsStr) : 0L);

        String todayViewsStr = redisTemplate.opsForValue().get(KEY_VIEW_DAY_PREFIX + todayStr);
        vo.setTodayViews(todayViewsStr != null ? Long.parseLong(todayViewsStr) : 0L);

        // 2. 博文数 (DB)
        vo.setTotalBlogs(blogService.count(new LambdaQueryWrapper<Blog>().eq(Blog::getPublished, true)));
        vo.setTodayBlogs(blogService.count(new LambdaQueryWrapper<Blog>()
                .eq(Blog::getPublished, true)
                .ge(Blog::getCreateTime, todayStart)));

        // 3. 评论数 (DB)
        vo.setTotalComments(commentService.count());
        vo.setTodayComments(commentService.count(new LambdaQueryWrapper<Comment>()
                .ge(Comment::getCreateTime, todayStart)));

        // 4. 留言数 (DB) - 已启用
        vo.setTotalMessages(messageService.count());
        vo.setTodayMessages(messageService.count(new LambdaQueryWrapper<Message>()
                .ge(Message::getCreateTime, todayStart)));

        return R.success().data("data", vo);
    }

    /**
     * 上报访问量 (PV +1)
     */
    @PostMapping("/visit")
    public R addVisit() {
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 总量 +1
        redisTemplate.opsForValue().increment(KEY_VIEW_TOTAL);

        // 今日 +1 (设置30天过期，避免垃圾数据堆积)
        String dayKey = KEY_VIEW_DAY_PREFIX + todayStr;
        redisTemplate.opsForValue().increment(dayKey);
        redisTemplate.expire(dayKey, 30, TimeUnit.DAYS);

        return R.success();
    }
}