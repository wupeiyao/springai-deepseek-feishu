package com.xiaowu.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xmin
 */
@TableName(value = "base_doc", autoResultMap = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseDoc {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String docId;

    private String vectorDocId;

    private String docName;

    private String url;

    private String modifiedTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;


}
