package com.xiaowu.scheduler;


import com.xiaowu.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * DocScanScheduler 是一个定时任务类，
 * 每分钟自动调用 DocService 的 loadData() 方法，用于文档数据的定时加载或同步。
 */
@EnableScheduling // 开启 Spring 定时任务功能
@Component        // 注册为 Spring 组件（由 Spring 管理）
@RequiredArgsConstructor // 使用 Lombok 自动生成构造方法注入 docService
public class DocScanScheduler {

    // 注入业务服务 DocService，用于执行实际的数据加载逻辑
    private final DocService docService;

    /**
     * 每隔 60 秒执行一次该方法
     * fixedRate：上一次任务开始执行后，延迟固定时间再开始下一次任务（单位：毫秒）
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void scan() {
        // 调用文档服务中的数据加载方法
        docService.loadData();
    }
}
