package com.xiaowu.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author xmin
 */
@TableName(value = "base_conversation", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseConversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String conversationId;

    private String title;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;


}
