/**
 * 留言/消息服务接口
 *
 * 继承 IService，留言增删时通过 WebSocket 广播通知前端刷新。
 */
package com.heyi.blog.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.heyi.blog.entity.Message;

public interface MessageService extends IService<Message> {
}