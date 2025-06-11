package com.xiaowu.config;

// 引入飞书开放平台 SDK 的 Client 类，用于调用 API
import com.lark.oapi.Client;
// 引入基础地址的枚举类型，FeiShu 表示飞书国内接口地址
import com.lark.oapi.core.enums.BaseUrlEnum;
import java.util.concurrent.TimeUnit; // 用于设置超时时间的时间单位

// 引入自定义的飞书配置类，封装了 appId 和 appSecret
import com.xiaowu.entity.po.FeishuConfig;
// 引入 Lombok 注解，用于自动生成构造函数并注入依赖
import lombok.RequiredArgsConstructor;
// Spring 注解，声明这是一个配置类
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 声明为 Spring 的配置类
@RequiredArgsConstructor // 自动生成构造函数，注入 final 修饰的字段
public class FeishuClientConfig {

    // 注入自定义配置类，获取 appId 和 appSecret
    private final FeishuConfig feishuConfig;

    /**
     * 配置并创建一个飞书 SDK 客户端 Client 实例
     * @return Client 对象，可用于发送请求到飞书开放平台
     */
    @Bean
    public Client getClient() {
        return Client.newBuilder(feishuConfig.getAppId(), feishuConfig.getAppSecret()) // 设置 AppID 和 AppSecret
                .marketplaceApp() // 声明为应用市场中的应用（第三方应用）
                .openBaseUrl(BaseUrlEnum.FeiShu) // 设置请求地址为飞书中国站接口
                .requestTimeout(3, TimeUnit.SECONDS) // 设置请求超时时间为 3 秒
                .logReqAtDebug(true) // 在 debug 模式下打印请求和响应日志，方便调试
                .build(); // 构建 Client 实例
    }
}
