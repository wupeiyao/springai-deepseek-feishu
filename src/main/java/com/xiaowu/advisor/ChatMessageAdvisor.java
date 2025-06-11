package com.xiaowu.advisor;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;

/**
 * ChatMessageAdvisor 是对 Spring AI 提供的 MessageChatMemoryAdvisor 的一个简单封装。
 * 它用于将会话上下文（从 ChatMemory 中获取）注入到 AI 聊天过程中，限制最大 token 数为 4000。
 */
public class ChatMessageAdvisor extends MessageChatMemoryAdvisor {

    /**
     * 构造函数：创建一个带有上下文记忆功能的顾问，用于为 AI 聊天注入历史消息。
     *
     * @param chatMemory     聊天上下文存储（如数据库、缓存等实现）
     * @param conversationId 当前对话的唯一标识 ID
     */
    public ChatMessageAdvisor(ChatMemory chatMemory, String conversationId) {
        // 调用父类构造方法，并设置上下文 token 限制为 4000（例如 GPT-3.5 的 token 限制）
        super(chatMemory, conversationId, 4000);
    }
}
