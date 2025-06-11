package com.xiaowu.common;

import com.xiaowu.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

/**
 * MessageWrapper 是一个用于封装聊天消息的通用类。
 * 支持从第三方 Message 对象构造，也支持将自身转为 Message 实例。
 */
@Data // Lombok 注解，自动生成 getter/setter、toString、equals 等
@NoArgsConstructor // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
public class MessageWrapper {

    // 消息的类型（例如：用户、助手、系统）
    private MessageType type;

    // 消息的具体内容
    private String content;

    /**
     * 构造函数：根据外部 Message 实例创建 MessageWrapper 对象
     * 适配第三方或 SDK 的消息对象
     */
    public MessageWrapper(Message message) {
        this.type = message.getMessageType(); // 获取消息类型
        this.content = message.getText();     // 获取消息文本内容
    }

    /**
     * 将当前封装对象转换为原始 Message 对象（例如 OpenAI 消息模型）
     * @return 对应类型的 Message 实例
     */
    public Message toMessage() {
        return switch (type) {
            case USER -> new UserMessage(content); // 用户消息
            case ASSISTANT -> new AssistantMessage(content); // 助手（AI）消息
            case SYSTEM -> new SystemMessage(content); // 系统消息
            case TOOL -> throw new UnsupportedOperationException(); // 不支持的消息类型
        };
    }

    /**
     * 将消息列表转换为 JSON 字符串，用于存储或网络传输
     * @param messages 消息封装列表
     * @return JSON 格式字符串
     */
    public static String toConversationStr(List<MessageWrapper> messages) {
        return JsonUtil.toJsonString(messages);
    }

    /**
     * 从 JSON 字符串反序列化为消息封装列表
     * @param conversationStr JSON 格式的消息列表
     * @return 解析后的消息列表
     */
    public static List<MessageWrapper> fromConversationStr(String conversationStr) {
        if (!StringUtils.hasText(conversationStr)) {
            return new ArrayList<>(); // 如果是空字符串，返回空列表
        }
        TypeReference<List<MessageWrapper>> typeReference = new TypeReference<>() {};
        return JsonUtil.toJsonObject(conversationStr, typeReference);
    }
}
