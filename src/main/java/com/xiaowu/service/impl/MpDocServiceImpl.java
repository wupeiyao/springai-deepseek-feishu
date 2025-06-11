package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowu.entity.po.BaseDoc;
import com.xiaowu.mapper.DocMapper;
import com.xiaowu.service.MpDocService;
import org.springframework.stereotype.Service;

@Service
public class MpDocServiceImpl extends ServiceImpl<DocMapper, BaseDoc> implements MpDocService {

}
