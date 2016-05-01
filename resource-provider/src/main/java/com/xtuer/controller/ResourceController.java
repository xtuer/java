package com.xtuer.controller;

import com.xtuer.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @RequestMapping("/show-resources")
    public String showResourcePage() {
        return "show-resource.html";
    }

    @RequestMapping(value = "/request-resources", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> requestResources(@RequestBody Map<String, String> paramsMap) {
        // 1. 加载资源的 xml 文件
        String content = resourceService.requestResourceContent(paramsMap.get("resourceUrl"));

        // 2. 返回给客户端
        Map<String, String> map = new HashMap<String, String>();
        map.put("result", content);

        return map;
    }

    @RequestMapping(value = "/save-resources", method = RequestMethod.POST)
    @ResponseBody
    public List<String> saveResources(@RequestBody Map<String, List<String>> paramsMap) {
        List<String> resourcePaths = paramsMap.get("resourcePaths");

        // 保存资源路径到收藏夹里
        for (String path : resourcePaths) {
            String url = resourceService.requestResourceUrl(path);
            resourceService.saveResourceUrlToFavorite(path, url);
        }

        return resourcePaths;
    }


}
