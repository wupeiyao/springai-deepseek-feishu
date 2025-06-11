package com.xiaowu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowu.entity.po.BaseDoc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocMapper extends BaseMapper<BaseDoc> {

}
