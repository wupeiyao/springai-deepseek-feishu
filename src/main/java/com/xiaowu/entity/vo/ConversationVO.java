package com.xiaowu.entity.vo;


import java.util.List;

import com.xiaowu.common.MessageWrapper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationVO {

    private String conversationId;

    private String title;

    private List<MessageWrapper> messages;

}
