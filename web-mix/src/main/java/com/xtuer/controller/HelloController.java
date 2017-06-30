package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Controller
public class HelloController {
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Welcome";
    }

    @GetMapping("/hello")
    public String hello(ModelMap model) {
        model.put("name", "道格拉斯·狗");
        return "hello.vm";
    }

    @GetMapping("/webuploader")
    public String webUploaderPage() {
        return "webuploader.html";
    }

    @PostMapping("/webuploader")
    @ResponseBody
    public Result uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        file.transferTo(new File("/Users/Biao/Desktop/" + file.getOriginalFilename()));

        return Result.ok("OK", file.getOriginalFilename());
    }

    @RequestMapping("/read-cookie")
    @ResponseBody
    public String readCookie(@CookieValue("username") String cookie) {
        return "Cookie for username: " + cookie;
    }

    @RequestMapping("/write-cookie")
    @ResponseBody
    public String writeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("username", "Don't tell you");
        cookie.setDomain(".xtuer.com");
        cookie.setMaxAge(1000);
        response.addCookie(cookie); // Put cookie in response.
        return "Cookie is wrote.";
    }
}
