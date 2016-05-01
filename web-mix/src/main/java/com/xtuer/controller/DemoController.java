package com.xtuer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
