package com.xiaowu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lark.oapi.service.drive.v1.model.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.xiaowu.entity.po.BaseDoc;
import com.xiaowu.entity.vo.DocVO;
import com.xiaowu.feishu.FeishuService;
import com.xiaowu.service.DocService;
import com.xiaowu.service.MpDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 文档服务实现类，用于同步飞书文档数据到本地数据库及向向量存储中写入内容。
 */
@Service
@RequiredArgsConstructor // 自动注入构造函数中的 final 成员变量
public class DocServiceImpl implements DocService {

    // 注入 FeishuService，用于调用飞书 API 获取文档数据
    private final FeishuService feishuService;

    // 注入本地数据库操作服务
    private final MpDocService mpDocService;

    // 注入向量存储，用于存储文档向量信息
    private final VectorStore vectorStore;

    /**
     * 加载飞书文档数据，并进行同步处理：
     * 1. 新增文档添加到本地数据库和向量存储
     * 2. 已删除文档从数据库和向量存储中删除
     * 3. 修改过的文档更新至向量存储
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 添加事务控制，操作失败时回滚
    public void loadData() {
        // 1. 获取飞书的所有文档列表
        List<File> files = feishuService.listDocs();

        // 2. 获取本地数据库已存储的所有文档
        List<BaseDoc> docs = mpDocService.list();

        // 3. 将本地文档转换为 Map，以 docId 为 key 便于后续比较
        /*
           .stream()：把 List<BaseDoc> 转成“流”
            .collect(...)：收集器，用来生成新集合，比如 Map
            Collectors.toMap(...)：这是收集成 Map 的方法
            第一个参数：BaseDoc::getDocId，表示用 doc.getDocId() 作为 Map 的 key
            第二个参数：doc -> doc，表示 value 就是 doc 本身
          */
        Map<String, BaseDoc> docMap = docs.stream()
                .collect(Collectors.toMap(BaseDoc::getDocId, doc -> doc));
        //doc是本地的飞书云文档
        // 4. 找出飞书中新增的文档（本地不存在）
        List<BaseDoc> newDocs = files.stream()
                .filter(v -> !docMap.containsKey(v.getToken()))
                .map(v -> BaseDoc.builder()
                        .docId(v.getToken())
                        .docName(v.getName())
                        .url(v.getUrl())
                        .modifiedTime(v.getModifiedTime())
                        .build())
                .toList();

        // 5. 将新增文档写入向量存储和本地数据库
        addToVectorStore(newDocs);

        // 6. 找出已被删除的文档（飞书中不存在但本地存在）
        List<String> fileIds = files.stream().map(File::getToken).toList();
        List<BaseDoc> deletedDocs = docs.stream()
                .filter(v -> !fileIds.contains(v.getDocId()))
                .toList();

        // 7. 从向量存储和本地数据库中移除已删除文档
        if (!CollectionUtils.isEmpty(deletedDocs)) {
            removeFromVectorStore(deletedDocs);
        }

        // 8. 找出内容发生变化的文档（通过 modifiedTime 判断）
        List<BaseDoc> updatedDocs = files.stream()
                .filter(v -> docMap.containsKey(v.getToken()))
                .filter(v -> !v.getModifiedTime().equals(docMap.get(v.getToken()).getModifiedTime()))
                .map(v -> {
                    BaseDoc baseDoc = docMap.get(v.getToken());
                    return BaseDoc.builder()
                            .id(baseDoc.getId()) // 使用原本记录的数据库 ID
                            .docId(baseDoc.getDocId())
                            .docName(v.getName())
                            .url(v.getUrl())
                            .vectorDocId(baseDoc.getVectorDocId())
                            .modifiedTime(v.getModifiedTime())
                            .build();
                })
                .toList();

        // 9. 更新已更改文档至向量存储和数据库
        updateVectorStore(updatedDocs);
    }

    /**
     * 获取所有文档的展示信息（供前端使用）
     *
     * @return 文档展示列表
     */
    @Override
    public List<DocVO> list() {
        return mpDocService.list().stream()
                .map(v -> DocVO.builder()
                        .docId(v.getDocId())
                        .name(v.getDocName())
                        .url(v.getUrl())
                        .build())
                .toList();
    }

    /**
     * 将新文档添加到向量存储，并存入数据库
     *
     * @param docs 新增文档列表
     */
    private void addToVectorStore(List<BaseDoc> docs) {
        for (BaseDoc doc : docs) {
            // 从飞书读取文档内容，创建向量文档
            Document document = new Document(feishuService.readDoc(doc.getDocId()), new HashMap<>());

            // 写入向量存储系统
            vectorStore.write(List.of(document));

            // 设置文档向量 ID（用于后续更新/删除）
            doc.setVectorDocId(document.getId());
        }

        // 批量保存文档元数据到数据库
        mpDocService.saveBatch(docs);
    }

    /**
     * 更新文档内容到向量存储和数据库
     *
     * @param updatedDocs 被更新的文档列表
     */
    private void updateVectorStore(List<BaseDoc> updatedDocs) {
        // 先删除旧的向量记录
        removeFromVectorStore(updatedDocs);

        // 添加新的向量内容
        addToVectorStore(updatedDocs);

        // 更新数据库记录
        mpDocService.updateBatchById(updatedDocs);
    }

    /**
     * 从向量存储和数据库中移除文档
     *
     * @param docs 需要删除的文档列表
     */
    private void removeFromVectorStore(List<BaseDoc> docs) {
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }

        // 删除向量存储中的向量文档
        vectorStore.delete(docs.stream().map(BaseDoc::getVectorDocId).toList());

        // 从数据库中删除对应记录
        mpDocService.remove(
                new LambdaQueryWrapper<BaseDoc>().in(BaseDoc::getDocId,
                        docs.stream().map(BaseDoc::getDocId).toList())
        );
    }
}
