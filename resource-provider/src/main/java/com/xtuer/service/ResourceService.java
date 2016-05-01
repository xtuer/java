package com.xtuer.service;

import org.apache.http.client.fluent.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceService {
    /**
     * 从第三方加载资源的描述接口文档
     * @param url 资源列表接口的 URL
     * @return 资源的 XML 描述
     */
    public String requestResourceContent(String url) {
        try {
            return Request.Get(url).connectTimeout(5000).socketTimeout(5000).execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 向第三方请求资源对应的 URL
     * @param resourcePath 资源的标识, 例如特有的路径等
     * @return 返回访问资源的 URL
     */
    public String requestResourceUrl(String resourcePath) {
        // 模拟存储资源的 URL
        Map<String, String> urls = new HashMap<String, String>();
        urls.put("高中语文/人教课标版/高一/必修1梳理探究/优美的汉字", "http://resource.edu-edu.com/a.txt");
        urls.put("高中语文/人教课标版/高一/必修1梳理探究/奇妙的对联", "http://resource.edu-edu.com/b.txt");
        urls.put("高中语文/人教课标版/高一/必修1表达交流/“黄河九曲” 写事要有点波澜", "http://resource.edu-edu.com/c.txt");
        urls.put("高中语文/人教课标版/高一/必修1第一单元/2 诗两首", "http://resource.edu-edu.com/d.txt");

        return urls.get(resourcePath);
    }

    /**
     * 保存资源的 URL 到收藏夹
     * @param resourcePath 资源的标识描述
     * @param resourceUrl 资源的 URL
     */
    public void saveResourceUrlToFavorite(String resourcePath, String resourceUrl) {
        System.out.println("==> " + resourcePath + " : " + resourceUrl);
    }
}
