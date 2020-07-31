package com.ndt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class Utils {
    /**
     * 把对象转为 Json 字符串
     *
     * @param object 要转为 Json 字符串的对象
     * @return 返回对象的 Json 字符串表示
     */
    public static String toJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Date format
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // Indent
        // objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // 2 个空格
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        printer.indentObjectsWith(indenter); // Indent JSON objects
        printer.indentArraysWith(indenter);  // Indent JSON arrays

        try {
            return objectMapper.writer(printer).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    /**
     * 把 JSON 字符串转为对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类
     * @return 返回得到的对象，转换失败时返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Date format
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 把 JSON 字符串转为对象
     *
     * @param json JSON 字符串
     * @param ref  类型引用，用于集合类型
     * @return 返回得到的对象，转换失败时返回 null
     */
    public static <T> T fromJson(String json, TypeReference<T> ref) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(json, ref);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
