package com.eduedu.ebag.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CommonUtils {
    /**
     * 生成一个 UUID
     * @return UUID 字符串
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    /**
     * 使用 UUID 生成不会重复的文件名, 后缀和原来的后缀一样(为了统一, 使用小写)
     * @param fileName 文件名
     * @return 不会重复的文件名
     */
    public static String generateUniqueFileName(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        extension = StringUtils.isEmpty(extension) ? "" : "." + extension.toLowerCase();

        return CommonUtils.generateUuid() + extension;
    }

    /**
     * 格式化日期为 yyyy-MM-dd
     * @param date 时间
     * @return 包含年月日的日期字符串
     */
    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 计算文件的 MD5.
     *
     * @param filePath 文件的路径
     * @return 文件的 MD5
     */
    public static String fileMd5(String filePath) {
        InputStream in = null;

        try {
            in = new FileInputStream(filePath);
            return DigestUtils.md5DigestAsHex(in); // Spring 自带的 MD5 工具
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static void main(String[] args) {
        System.out.println(generateUniqueFileName("x"));
        System.out.println(generateUniqueFileName("x.doc"));
        System.out.println(generateUniqueFileName("x.DOC"));
        System.out.println(dateToString(new Date()));
        System.out.println(fileMd5("/Users/Biao/Desktop/QQ.app.zip"));
    }
}
