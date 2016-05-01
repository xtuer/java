package com.xtuer.controller;

import com.xtuer.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/async")
    @ResponseBody
    public String async() {
        asyncService.asyncMethod();
        return "Async";
    }
}
