package com.heyi.blog.controller.admin;

import cn.hutool.core.date.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heyi.blog.entity.Comment;
import com.heyi.blog.entity.Message;
import com.heyi.blog.entity.SysLog;
import com.heyi.blog.entity.vo.DashboardVO;
import com.heyi.blog.service.*;
import com.heyi.blog.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminDashboardController {

    @Autowired private BlogService blogService;
    @Autowired private TagService tagService;
    @Autowired private TypeService typeService;
    @Autowired private CommentService commentService;
    @Autowired private MessageService messageService;
    @Autowired private SysLogService sysLogService;

    @GetMapping("/stats")
    public R stats(@RequestParam(defaultValue = "week") String type) {
        DashboardVO vo = new DashboardVO();

        // 1. 顶部六大卡片数据 (不变)
        vo.setBlogCount(blogService.count());
        vo.setTagCount(tagService.count());
        vo.setTypeCount(typeService.count());
        vo.setCommentCount(commentService.count());
        vo.setMessageCount(messageService.count());
        vo.setViewCount(sysLogService.count());

        // 2. 准备图表数据容器
        List<String> dateList = new ArrayList<>();
        List<Long> commentTrend = new ArrayList<>();
        List<Long> messageTrend = new ArrayList<>();
        List<Long> viewTrend = new ArrayList<>();

        // 3. 计算自然周期的 开始时间 和 结束时间
        Date startDate;
        Date endDate;
        DateField rangeUnit; // 迭代单位 (按天还是按月)
        String fmt;          // 前端显示的日期格式

        Date now = new Date();

        switch (type) {
            case "week":
                // 本周 (周一 ~ 周日)
                // true 表示周一为第一天，false 表示周日为第一天
                startDate = DateUtil.beginOfWeek(now, true);
                endDate = DateUtil.endOfWeek(now, true);
                rangeUnit = DateField.DAY_OF_YEAR;
                fmt = "MM-dd";
                break;
            case "month":
                // 本月 (1号 ~ 月底)
                startDate = DateUtil.beginOfMonth(now);
                endDate = DateUtil.endOfMonth(now);
                rangeUnit = DateField.DAY_OF_YEAR;
                fmt = "MM-dd";
                break;
            case "year":
                // 本年 (1月 ~ 12月)
                startDate = DateUtil.beginOfYear(now);
                endDate = DateUtil.endOfYear(now);
                rangeUnit = DateField.MONTH;
                fmt = "yyyy-MM";
                break;
            default:
                startDate = DateUtil.beginOfWeek(now, true);
                endDate = DateUtil.endOfWeek(now, true);
                rangeUnit = DateField.DAY_OF_YEAR;
                fmt = "MM-dd";
        }

        // 4. 使用 Hutool 的 DateRange 生成时间序列
        // range 包含了从 startDate 到 endDate 的每一个时间点
        DateRange range = DateUtil.range(startDate, endDate, rangeUnit);

        for (DateTime date : range) {
            // 添加 X 轴坐标 (如 "12-08", "12-09")
            dateList.add(DateUtil.format(date, fmt));

            // 计算查询条件的开始和结束 (精确到时分秒)
            String queryStart;
            String queryEnd;

            if ("year".equals(type)) {
                // 如果是按年统计，查询范围是 "当月1号 00:00:00" 到 "当月最后一天 23:59:59"
                queryStart = DateUtil.format(DateUtil.beginOfMonth(date), DatePattern.NORM_DATETIME_PATTERN);
                queryEnd = DateUtil.format(DateUtil.endOfMonth(date), DatePattern.NORM_DATETIME_PATTERN);
            } else {
                // 如果是按周/月统计，查询范围是 "当天 00:00:00" 到 "当天 23:59:59"
                queryStart = DateUtil.format(DateUtil.beginOfDay(date), DatePattern.NORM_DATETIME_PATTERN);
                queryEnd = DateUtil.format(DateUtil.endOfDay(date), DatePattern.NORM_DATETIME_PATTERN);
            }

            // 执行数据库查询
            // 这种循环查库在数据量巨大时性能不佳，但对于毕设或中小型博客，逻辑最清晰且绝对够用
            commentTrend.add(commentService.count(new QueryWrapper<Comment>().between("create_time", queryStart, queryEnd)));
            messageTrend.add(messageService.count(new QueryWrapper<Message>().between("create_time", queryStart, queryEnd)));
            viewTrend.add(sysLogService.count(new QueryWrapper<SysLog>().between("create_time", queryStart, queryEnd)));
        }

        vo.setDateList(dateList);
        vo.setCommentTrend(commentTrend);
        vo.setMessageTrend(messageTrend);
        vo.setViewTrend(viewTrend);

        return R.success().data("stats", vo);
    }
}