package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller
public class RestController {
    @GetMapping("/rest")
    @ResponseBody
    public Result get(@RequestParam(required = false) String name) {
        return Result.ok("GET", name);
    }

    @PostMapping("/rest")
    @ResponseBody
    public Result post(@RequestParam String name) {
        return Result.ok("POST", name);
    }

    @PutMapping("/rest")
    @ResponseBody
    public Result put(@RequestBody String name) {
        return Result.ok("PUT", name);
    }

    @DeleteMapping("/rest")
    @ResponseBody
    public Result delete() {
        return Result.ok("DELETE");
    }

    @PostMapping("/upload")
    @ResponseBody
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String password) throws IOException {
        System.out.println("Username: " + username + ", Password: " + password);
        System.out.println(file.getOriginalFilename());
        file.transferTo(new File("/Users/Biao/Desktop/" + System.currentTimeMillis() + "-" + file.getOriginalFilename()));

        return Result.ok("OK", file.getOriginalFilename());
    }
}
