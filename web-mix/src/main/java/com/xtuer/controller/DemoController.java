package com.xtuer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/a")
    public String pageA() {
        return "a.html";
    }

    @PostMapping("/a")
    @ResponseBody
    public String postA(@RequestParam String name, HttpServletRequest request) {
        System.out.println(name);
        return name;
    }

    @PostMapping("ajax-test")
    @ResponseBody
    public String ajaxGet(@RequestBody Map map) {
        System.out.println("==> age: " + map);
        return "{\"success\": true}";
    }
}
