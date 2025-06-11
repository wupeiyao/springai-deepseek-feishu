package com.xiaowu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @description:
 * @author: xiaowu
 * @time: 2025/5/15 0:35
 */
// 声明这是一个 Spring 的配置类
@Configuration
// Lombok 注解：为所有 final 字段生成构造函数（用于自动注入）
@RequiredArgsConstructor
public class ChatClientConfig {

    // 注入一个类路径下的资源文件 system-message.st
    // 该文件通常包含系统提示词（prompt），用于设定对话 AI 的行为或身份
    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;

    /**
     * 注册一个 ChatClient Bean，基于 OpenAiChatModel 创建。
     * ChatClient 是一个用于与 OpenAI 对话模型交互的封装客户端。
     *
     * @param openAiChatModel 注入的 OpenAI 对话模型
     * @return 配置好的 ChatClient 实例
     */
    @Bean
    public ChatClient MoNika(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel) // 使用 OpenAiChatModel 构建 ChatClient
                .defaultOptions(OpenAiChatOptions.builder()
                        // temperature 控制生成回复的随机性（创造力），0.7 是一个中等值
                        .temperature(0.7)
                        .build()
                )
                // 设置系统提示词（用于定义 ChatGPT 的角色、行为等）
                .defaultSystem(systemResource)
                .build(); // 构建 ChatClient 实例
    }
}
