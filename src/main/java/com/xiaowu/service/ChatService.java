package com.xiaowu.service;



import com.xiaowu.entity.vo.ConversationVO;

import java.util.List;

/**
 * ChatService 接口定义了对聊天会话（Conversation）相关的操作。
 * 主要用于创建、修改、获取、删除聊天会话记录。
 */
public interface ChatService {

    /**
     * 创建一个新的会话
     * @return 新建的会话信息对象
     */
    ConversationVO create();

    /**
     * 编辑指定会话的名称
     * @param conversationId 要修改的会话 ID
     * @param name 新的会话名称
     */
    void edit(String conversationId, String name);

    /**
     * 获取所有会话的列表
     * @return 会话信息列表
     */
    List<ConversationVO> list();

    /**
     * 删除指定会话
     * @param conversationId 要删除的会话 ID
     */
    void delete(String conversationId);

    /**
     * 获取指定 ID 的会话详情
     * @param conversationId 会话 ID
     * @return 对应的会话信息对象
     */
    ConversationVO get(String conversationId);
}
