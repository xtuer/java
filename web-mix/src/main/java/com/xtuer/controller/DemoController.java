package com.xtuer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class DemoController {
    public DemoController() {
        System.out.println("=====> DemoController()--");
    }
    @RequestMapping("/demo")
    @ResponseBody
    public String demo() {
        return "Welcome---Demo";
    }

    @RequestMapping("/a")
    public String pageA() {
        return "a.html";
    }

    @RequestMapping("/b")
    public String pageB() {
        return "b.html";
    }

    @PostMapping("ajax-test")
    @ResponseBody
    public String ajaxGet(@RequestBody Map map) {
        System.out.println("==> age: " + map);
        return "{\"success\": true}";
    }
}
