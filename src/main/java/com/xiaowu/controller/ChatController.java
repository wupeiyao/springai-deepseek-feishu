package com.xiaowu.controller;


import java.util.List;

import com.xiaowu.advisor.ChatMessageAdvisor;
import com.xiaowu.entity.vo.ConversationVO;
import com.xiaowu.service.ChatService;
import com.xiaowu.utils.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/conversation")
public class ChatController {

    final ChatClient chatClient;

    final ChatMemory chatMemory;

    final ChatService chatService;


    @PostMapping("/create")
    public RestResult<ConversationVO> create() {
        return RestResult.buildSuccessResult(chatService.create());
    }

    @PutMapping("/edit")
    public RestResult<Void> edit(
        @RequestParam(value = "conversationId") String conversationId,
        @RequestParam(value = "title")String title) {
        chatService.edit(conversationId, title);
        return RestResult.buildSuccessResult();
    }

    @GetMapping("/chat")
    public RestResult<String> chat(@RequestParam(value = "conversationId") String conversationId,
        @RequestParam(value = "message") String message) {
        return RestResult.buildSuccessResult(chatClient.prompt()
            .user(message)
            .advisors(new ChatMessageAdvisor(chatMemory, conversationId))
            .call()
            .content());
    }

    @DeleteMapping("/del")
    public RestResult<Void> del(@RequestParam(value = "conversationId")String conversationId) {
        chatService.delete(conversationId);
        return RestResult.buildSuccessResult();
    }

    @GetMapping("/list")
    public RestResult<List<ConversationVO>> list() {
        return RestResult.buildSuccessResult(chatService.list());
    }

    @GetMapping("/get")
    public RestResult<ConversationVO> get(@RequestParam(value = "conversationId") String conversationId) {
        return RestResult.buildSuccessResult(chatService.get(conversationId));
    }


}
