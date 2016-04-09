package com.xtuer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {
    @RequestMapping("/demo")
    @ResponseBody
    public String demo() {
        System.out.println("hi---");
        return "Welcome---+++";
    }
}
