package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowu.entity.po.BaseConversation;
import com.xiaowu.mapper.ConversationMapper;
import com.xiaowu.service.MpConversationService;

import org.springframework.stereotype.Service;

@Service
public class MpConversationServiceImpl extends ServiceImpl<ConversationMapper, BaseConversation> implements MpConversationService {
}
