package com.xtuer.util;

import org.springframework.util.DigestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class CommonUtils {
    /**
     * BindingResult 中的错误信息很多，对用户不够友好，使用 getBindingMessage()
     * 提取对用户阅读友好的定义验证规则 message。
     *
     * @param result 验证的结果对象
     * @return 验证规则 message
     */
    public static String getBindingMessage(BindingResult result) {
        StringBuffer sb = new StringBuffer();

        for (FieldError error : result.getFieldErrors()) {
            // sb.append(error.getField() + " : " + error.getDefaultMessage() + "\n");
            sb.append(error.getDefaultMessage() + "\n");
        }

        return sb.toString();
    }

    /**
     * 计算字符串的 MD5
     *
     * @param text 需要计算 MD5 的字符串
     * @return 返回字符串的 MD5
     */
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * 计算文件的 MD5
     *
     * @param file 需要计算 MD5 的文件
     * @return 返回文件的 MD5，如果出错，例如文件不存在则返回 null
     */
    public static String md5(File file) {
        try (InputStream in = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 生成 UUID
     *
     * @return 返回 UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

