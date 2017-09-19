package com.xtuer.util;

import java.util.HashMap;
import java.util.Map;

public class ContentType {
    public static Map<String, String> contentTypes = new HashMap<String, String>();

    static {
        // HTML content type 对照表: http://tool.oschina.net/commons
        contentTypes.put("svg", "image/svg+xml");
        contentTypes.put("png", "image/png");
        contentTypes.put("jpg", "image/jpeg");
        contentTypes.put("jpeg", "image/jpeg");
        contentTypes.put("gif", "image/gif");
        contentTypes.put("tif", "image/tiff");
        contentTypes.put("tiff", "image/tiff");
    }

    public static String getContentType(String imageType) {
        return contentTypes.get(imageType.toLowerCase());
    }
}
