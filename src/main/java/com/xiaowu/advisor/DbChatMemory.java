package com.xiaowu.advisor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.xiaowu.common.MessageWrapper;
import com.xiaowu.entity.po.BaseConversation;
import com.xiaowu.service.MpConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 实现 ChatMemory 接口的数据库版实现类，用于将 AI 聊天记录持久化存储在数据库中。
 */
@Service
@RequiredArgsConstructor
public class DbChatMemory implements ChatMemory {

    // 会话服务对象，用于操作数据库中的会话数据
    private final MpConversationService conversationService;

    /**
     * 向指定 conversationId 的对话中添加消息列表
     * @param conversationId 会话ID
     * @param messages 要添加的消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 查询当前会话数据
        BaseConversation baseConversation = conversationService.getOneOpt(
                new LambdaQueryWrapper<BaseConversation>()
                    .eq(BaseConversation::getConversationId, conversationId))
            .orElseThrow(() -> new RuntimeException(
                "can not find the conversation , conversation id is %s".formatted(conversationId)));

        // 获取已存在的消息并追加新的消息
        List<Message> savedMessages = convert(baseConversation);
        savedMessages.addAll(messages);

        // 序列化保存到数据库中
        conversationService.update(
            BaseConversation.builder()
                .content(MessageWrapper.toConversationStr(
                    savedMessages.stream().map(MessageWrapper::new).toList()))
                .build(),
            new LambdaQueryWrapper<BaseConversation>()
                .eq(BaseConversation::getConversationId, conversationId)
        );
    }

    /**
     * 获取指定 conversationId 的最近 N 条消息
     * @param conversationId 会话ID
     * @param lastN 获取的消息条数
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        return conversationService.getOneOpt(
                new LambdaQueryWrapper<BaseConversation>()
                    .eq(BaseConversation::getConversationId, conversationId))
            .map(v -> convert(v).stream().limit(lastN).toList())
            .orElse(List.of()); // 如果未找到会话则返回空列表
    }

    /**
     * 清除指定 conversationId 的会话记录（物理删除）
     * @param conversationId 会话ID
     */
    @Override
    public void clear(String conversationId) {
        conversationService.remove(
            new LambdaQueryWrapper<BaseConversation>()
                .eq(BaseConversation::getConversationId, conversationId));
    }

    /**
     * 将 BaseConversation 中的 JSON 字符串转换为 Message 对象列表
     * @param conversation 会话实体
     * @return 消息对象列表
     */
    private List<Message> convert(BaseConversation conversation) {
        if (!StringUtils.hasText(conversation.getContent())) {
            return new ArrayList<>();
        }
        // 反序列化为 MessageWrapper，再转换为 Message 对象
        return MessageWrapper.fromConversationStr(conversation.getContent()).stream()
            .map(MessageWrapper::toMessage)
            .collect(Collectors.toList());
    }
}
