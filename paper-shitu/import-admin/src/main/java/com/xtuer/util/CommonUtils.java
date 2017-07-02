package com.xtuer.util;

import java.util.UUID;

public final class CommonUtils {
    /**
     * 生成 UUID
     * @return 返回 UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
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
}
