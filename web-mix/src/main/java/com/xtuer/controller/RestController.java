package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class RestController {
    @GetMapping("/rest")
    @ResponseBody
    public Result get(@RequestParam String name) {
        return Result.ok("GET", name);
    }

    @PostMapping("/rest")
    @ResponseBody
    public Result post(@RequestBody Map<String, String> map) {
        String name = map.get("name");
        return Result.ok("POST", name);
    }

    @PutMapping("/rest")
    @ResponseBody
    public Result put(@RequestBody Map<String, String> map) {
        String name = map.get("name");
        return Result.ok("PUT", name);
    }

    @DeleteMapping("/rest")
    @ResponseBody
    public Result delete(@RequestBody Map<String, String> map) {
        String name = map.get("name");
        return Result.ok("DELETE", name);
    }
}
