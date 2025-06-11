package com.xiaowu.feishu;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.response.BaseResponse;
import com.lark.oapi.service.docx.v1.model.RawContentDocumentReq;
import com.lark.oapi.service.docx.v1.model.RawContentDocumentResp;
import com.lark.oapi.service.docx.v1.model.RawContentDocumentRespBody;
import com.lark.oapi.service.drive.v1.model.File;
import com.lark.oapi.service.drive.v1.model.ListFileReq;
import com.lark.oapi.service.drive.v1.model.ListFileResp;
import com.lark.oapi.service.drive.v1.model.ListFileRespBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.xiaowu.entity.po.FeishuConfig;
import com.xiaowu.feishu.model.FeishuAkRequest;
import com.xiaowu.feishu.model.FeishuAkResponse;
import com.xiaowu.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FeishuService {

    // 飞书 SDK 提供的 Client 对象，作为发起 API 请求的核心类
    private final Client client;

    // 用于调用飞书 token 接口（非 SDK 调用，直接用 RestTemplate）
    private final RestTemplate restTemplate;

    // 从配置类中读取飞书 AppID、AppSecret、根文件夹等信息
    private final FeishuConfig feishuConfig;

    // 飞书开放平台获取应用访问令牌的地址
    public static final String AK_URL = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";

    // Caffeine 缓存，用于缓存 app access token（避免频繁请求）
    private static final Cache<String, String> ACCESS_TOKEN_CACHE = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(100, TimeUnit.MINUTES) // token 有效期为 2 小时，这里缓存 100 分钟
            .build();

    /**
     * 获取飞书 access token（使用缓存）
     *
     * @param appId 飞书应用 appId
     * @return accessToken 字符串
     */
    private String getAccessToken(String appId) {
        return ACCESS_TOKEN_CACHE.get(appId, s -> {
            HttpEntity<FeishuAkRequest> request = new HttpEntity<>(new FeishuAkRequest(
                    feishuConfig.getAppId(), feishuConfig.getAppSecret()
            ));

            // 调用飞书官方 access_token 接口
            var response = Optional.of(
                    restTemplate.exchange(AK_URL, HttpMethod.POST, request, FeishuAkResponse.class)
            );

            // 如果返回值不正常则抛出异常
            if (response.map(HttpEntity::getBody).map(FeishuAkResponse::getCode).filter(code -> code.equals(0)).isEmpty()) {
                throw new RuntimeException("failed to obtain access token, the response body is %s".formatted(response.get()));
            }

            // 返回 app_access_token
            return response.get().getBody().getAppAccessToken();
        });
    }

    /**
     * 读取指定飞书文档的原始内容（纯文本）
     *
     * @param docId 飞书文档 ID
     * @return 文档内容（字符串）
     */
    public String readDoc(String docId) {
        // 构造请求对象
        RawContentDocumentReq req = RawContentDocumentReq.newBuilder()
                .lang(0) // 中文语言代码
                .documentId(docId)
                .build();

        Optional<RawContentDocumentResp> resp;
        try {
            // 发起请求并带上 access_token
            resp = resolveException(client.docx().v1().document().rawContent(req, RequestOptions.newBuilder()
                    .userAccessToken(getAccessToken(feishuConfig.getAppId()))
                    .build()));
        } catch (Exception e) {
            throw new RuntimeException("failed to read the document", e);
        }

        // 提取响应内容（如果有）
        return resp.map(BaseResponse::getData)
                .map(RawContentDocumentRespBody::getContent)
                .orElse("");
    }

    /**
     * 获取指定文件夹下的所有文件（飞书云文档）
     *
     * @return 文件列表（List<File>）
     */
    public List<File> listDocs() {
        // 构造请求，默认一次最多 50 个文件
        ListFileReq req = ListFileReq.newBuilder()
                .pageSize(50)
                .folderToken(feishuConfig.getRootFolder()) // 根目录 token
                .build();

        Optional<ListFileResp> resp;
        try {
            resp = resolveException(client.drive().v1().file().list(req, RequestOptions.newBuilder()
                    .userAccessToken(getAccessToken(feishuConfig.getAppId()))
                    .build()));
        } catch (Exception e) {
            throw new RuntimeException("failed to list the docs", e);
        }

        // 提取文件列表
        return resp.map(BaseResponse::getData)
                .map(ListFileRespBody::getFiles)
                .map(Arrays::asList)
                .orElse(new ArrayList<>());
    }

    /**
     * 校验 API 调用结果是否成功，失败则抛出异常
     */
    private <T extends BaseResponse<?>> Optional<T> resolveException(T response) {
        if (!response.success()) {
            throw new RuntimeException("failed to call the api function, response body is %s"
                    .formatted(JsonUtil.toJsonString(response)));
        }
        return Optional.of(response);
    }
}
