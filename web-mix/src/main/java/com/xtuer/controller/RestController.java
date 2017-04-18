package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class RestController {
    @GetMapping("/rest")
    @ResponseBody
    public Result get(@RequestParam String name) {
        return Result.ok("GET", name);
    }

    @PostMapping("/rest")
    @ResponseBody
    public Result post(@RequestParam String name) {
        return Result.ok("POST", name);
    }

    @PutMapping("/rest")
    @ResponseBody
    public Result put(@RequestParam String name) {
        return Result.ok("PUT", name);
    }

    @DeleteMapping("/rest")
    @ResponseBody
    public Result delete(@RequestParam String name) {
        return Result.ok("DELETE", name);
    }
}
