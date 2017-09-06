package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.service.XService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Controller
public class HelloController {
    @Autowired
    private XService xservice;

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "14";
    }

    @GetMapping("/hello")
    public String hello(ModelMap model) {
        model.put("date", new Date());
        model.put("level", "warning");

        return "leaf.html";
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

    @GetMapping("/dot/{name}")
    @ResponseBody
    public String dotName(@PathVariable String name) {
        System.out.println(name);
        return name;
    }

    @GetMapping("/ip")
    @ResponseBody
    public String ip(HttpServletRequest request) {
        return getClientIp(request);
    }

    /**
     * 获取客户端的 IP
     * @param request
     * @return 客户端的 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        final String UNKNOWN = "unknown";
        // 需要注意有多个 Proxy 的情况: X-Forwarded-For: client, proxy1, proxy2
        // 没有最近的 Proxy 的 IP, 只有 1 个 Proxy 时是客户端的 IP
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // 没有使用 Proxy 时是客户端的 IP, 使用 Proxy 时是最近的 Proxy 的 IP
        }
        return ip;
    }

    @GetMapping("/re")
    public String redirect(HttpServletRequest request) {
        System.out.println("Local port---: " + request.getLocalPort());
        System.out.println(request.getRemotePort());
        System.out.println(request.getServerPort());
        System.out.println(request.getHeader("Host"));
        return "redirect:/hello";
    }

    @PostMapping("/xss")
    @ResponseBody
    public Result postA(@RequestParam String name, @Valid User user, BindingResult binding, HttpServletRequest request) {
        System.out.println(name);
        System.out.println(request.getParameter("name"));
        return Result.ok("", user);
    }

    // http://localhost:8080/params?error
    @GetMapping("/params")
    @ResponseBody
    public String params(@RequestParam String error) {
        System.out.println("Error: " + error);
        return error;
    }
}
