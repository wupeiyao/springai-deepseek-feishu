package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.xiaowu.common.MessageWrapper;
import com.xiaowu.entity.po.BaseConversation;
import com.xiaowu.entity.vo.ConversationVO;
import com.xiaowu.service.ChatService;
import com.xiaowu.service.MpConversationService;
import com.xiaowu.utils.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service // 标识为 Spring 的服务组件（Service层）
@RequiredArgsConstructor // 自动为构造函数注入 final 修饰的字段
public class ChatServiceImpl implements ChatService {

    // 注入 MyBatis-Plus 封装的数据库服务，用于操作 Conversation 表
    private final MpConversationService conversationService;

    /**
     * 创建一个新的会话，并保存到数据库
     */
    @Override
    public ConversationVO create() {
        // 生成唯一 ID
        String conversationId = IdUtil.gen32UUID();
        // 默认标题
        String title = "New Chat";
        // 保存到数据库
        conversationService.save(BaseConversation.builder()
            .conversationId(conversationId)
            .title(title)
            .build());
        // 返回会话信息视图对象
        return ConversationVO.builder().conversationId(conversationId)
            .title(title)
            .build();
    }

    /**
     * 修改某个会话的标题
     */
    @Override
    public void edit(String conversationId, String title) {
        // 若标题为空则忽略
        if (!StringUtils.hasText(title)) {
            return;
        }
        // 根据会话 ID 更新标题
        conversationService.update(
            BaseConversation.builder().title(title).build(),
            new LambdaQueryWrapper<BaseConversation>().eq(BaseConversation::getConversationId, conversationId)
        );
    }

    /**
     * 查询所有会话，按创建时间排序
     */
    @Override
    public List<ConversationVO> list() {
        return conversationService.list(
                new LambdaQueryWrapper<BaseConversation>()
                    .orderByAsc(BaseConversation::getCreatedTime) // 按创建时间升序
            ).stream()
            .map(v -> ConversationVO.builder()
                .conversationId(v.getConversationId())
                .title(v.getTitle())
                .build())
            .toList();
    }

    /**
     * 删除指定 ID 的会话
     */
    @Override
    public void delete(String conversationId) {
        conversationService.remove(
            new LambdaQueryWrapper<BaseConversation>().eq(BaseConversation::getConversationId, conversationId)
        );
    }

    /**
     * 获取指定会话的详细信息，包括消息内容
     */
    @Override
    public ConversationVO get(String conversationId) {
        return conversationService.getOneOpt(
                new LambdaQueryWrapper<BaseConversation>().eq(BaseConversation::getConversationId, conversationId)
            )
            .map(v -> ConversationVO.builder()
                .conversationId(v.getConversationId())
                .title(v.getTitle())
                // 将 JSON 字符串形式的 content 转换为消息对象列表
                .messages(MessageWrapper.fromConversationStr(v.getContent()))
                .build()
            )
            .orElseThrow(() ->
                new RuntimeException("can not find conversation with id: %s ".formatted(conversationId))
            );
    }
}
