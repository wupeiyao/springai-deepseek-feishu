package com.xiaowu.service;


import com.xiaowu.entity.vo.DocVO;

import java.util.List;

/**
 * 文档服务接口，定义文档数据加载与查询的操作
 */
public interface DocService {

    /**
     * 加载文档数据（通常用于定时任务或初始化）
     * 实现类中可能从文件系统、网络或数据库中加载数据
     */
    void loadData();

    /**
     * 获取当前所有文档信息列表
     * @return 文档视图对象集合
     */
    List<DocVO> list();

}
