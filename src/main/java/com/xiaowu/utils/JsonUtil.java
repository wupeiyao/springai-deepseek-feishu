package com.xiaowu.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.Optional;

/**
 * JsonUtil 是一个 JSON 工具类，封装了 Jackson 的常用方法。
 * 提供对象与 JSON 字符串之间的转换功能。
 * <p>
 * 作者：liminggangrs
 */
public class JsonUtil {

    // 定义一个全局共用的 ObjectMapper 实例（线程安全）
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 私有构造函数，禁止外部实例化该工具类
    private JsonUtil() {}

    /**
     * 将任意对象转换为 JSON 字符串
     *
     * @param object 要转换的对象
     * @return 对应的 JSON 字符串
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            // 转换异常时，抛出运行时异常
            throw new RuntimeException("json parse error", e);
        }
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象
     *
     * @param str   JSON 字符串
     * @param clazz 要转换成的目标类
     * @return 转换后的对象
     */
    public static <T> T toJsonObject(String str, Class<T> clazz) {
        if (Objects.isNull(str)) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json parse error", e);
        }
    }

    /**
     * 将对象转换为目标类型（不通过字符串中转）
     * 适合做对象之间的字段拷贝/类型转换
     *
     * @param obj   原始对象
     * @param clazz 目标类型
     * @return Optional 包装的目标对象
     */
    public static <T> Optional<T> toJsonObject(Object obj, Class<T> clazz) {
        if (Objects.isNull(obj)) {
            return Optional.empty();
        }
        // 利用 Jackson 的类型转换功能将对象直接转为目标类型
        return Optional.ofNullable(objectMapper.convertValue(obj, clazz));
    }

    /**
     * 将 JSON 字符串反序列化为泛型对象（如 List<Message>、Map<String, Object> 等）
     *
     * @param str            JSON 字符串
     * @param typeReference  类型引用（用于处理泛型）
     * @return 转换后的对象
     */
    public static <T> T toJsonObject(String str, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(str, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json parse error", e);
        }
    }
}
