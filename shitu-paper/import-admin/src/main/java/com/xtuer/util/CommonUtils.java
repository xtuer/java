package com.xtuer.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public final class CommonUtils {
    /**
     * 生成 UUID
     * @return 返回 UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 使用 UUID 或则 UUID 的文件名生成目录名字，为 [0, 99]。
     * 算法为 UUID 的 hashCode 的绝对值模 100。
     *
     * @param uuid uuid UUID 或则 UUID 的文件名如 UUID.doc
     * @return 目录名字
     */
    public static String directoryNameByUuid(String uuid) {
        uuid = uuid.trim();
        uuid = uuid.replaceAll("^(\\w+)(\\..*)", "$1"); // 去掉 . 和其后面部分

        return Math.abs(uuid.hashCode()) % 100 + "";
    }

    /**
     * 获取 yaml 配置文件生成的 properties 中的数组的项.
     *
     * @param yamlProperties
     * @param key
     * @return
     */
    public static List<String> getStrings(Properties yamlProperties, String key) {
        List<String> strings = new LinkedList<>();

        for (int i = 0; i < 10000; ++i) {
            String str = yamlProperties.getProperty(key + "[" + i + "]");

            if (str != null) {
                strings.add(str);
            } else {
                break;
            }
        }

        return strings;
    }
}
