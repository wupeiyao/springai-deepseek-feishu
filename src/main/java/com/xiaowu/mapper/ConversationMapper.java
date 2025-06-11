package com.xiaowu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowu.entity.po.BaseConversation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConversationMapper extends BaseMapper<BaseConversation> {
}
