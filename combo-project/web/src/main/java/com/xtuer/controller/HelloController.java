package com.xtuer.controller;

import com.xtuer.bean.Demo;
import com.xtuer.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {
    @Autowired
    private DemoMapper demoMapper;

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Home page";
    }

    /**
     * http://localhost:8080/page/hello
     */
    @GetMapping("/page/hello")
    public String hello(ModelMap model) {
        model.put("name", "Biao");

        return "hello.html";
    }

    /**
     * http://localhost:8080/api/json
     */
    @GetMapping("/api/json")
    @ResponseBody
    public Object json() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "Biao");
        map.put("age", "23");

        return map;
    }

    /**
     * http://localhost:8080/api/demos
     */
    @GetMapping("/api/demos")
    @ResponseBody
    public Demo demo() {
        return new Demo(12, "你好");
    }

    @GetMapping("/api/demos/{id}")
    @ResponseBody
    public Demo demoById(@PathVariable int id) {
        return demoMapper.findDemoById(id);
    }
}
