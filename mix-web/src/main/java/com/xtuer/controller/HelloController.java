package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HelloController {
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Home page";
    }

    /**
     * http://localhost:8080/page/hello
     */
    @GetMapping("/page/hello")
    public String hello(ModelMap model) {
        model.put("name", "Biao");

        return "hello.html";
    }

    @GetMapping("/api/files")
    @ResponseBody
    public Object files(@RequestParam(name = "ids[]") List<String> ids) {
        System.out.println(ids);
        return ids;
    }

    /**
     * http://localhost:8080/api/json
     */
    @GetMapping("/api/json")
    @ResponseBody
    public Object json() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "Biao");
        map.put("age", "23");

        return map;
    }

    @GetMapping("/data/file/{filename}")
    @ResponseBody
    public String bar(@PathVariable String filename) {
        return filename;
    }

    @GetMapping("/api/file/{filename}")
    @ResponseBody
    public Result fox(@PathVariable String filename) {
//    public Result fox() {
        System.out.println(filename);
        return Result.ok(filename);
    }

    @GetMapping("/api/dot/{filename}")
    @ResponseBody
    public void dot(@PathVariable String filename, HttpServletResponse response) {
        System.out.println(filename);
        ajaxResponse(response, filename);
    }

    @GetMapping("/api/bar/{filename:.*}")
    @ResponseBody
    public void bar(@PathVariable String filename, HttpServletResponse response) {
        System.out.println(filename);
        ajaxResponse(response, filename);
    }

    public static void ajaxResponse(HttpServletResponse response, String data) {
        response.setContentType("application/json"); // 使用 ajax 的方式
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);

        try {
            // 写入数据到流里，刷新并关闭流
            PrintWriter writer = response.getWriter();
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
        }
    }

    /**
     * http://localhost:8080/api/foo.bar/fox
     * @param withDot
     * @return
     */
    @GetMapping("/api/{withDot}/fox")
    @ResponseBody
    public Result withDot(@PathVariable String withDot) {
        return Result.ok(withDot);
    }

    @GetMapping("/api/foo")
    @ResponseBody
    public Result foo(HttpServletRequest request) {
        return Result.ok(request.getRequestURI() + ", " + request.getRequestURL());
    }

    /**
     * POST 请求中 url 可以有 query 参数，同时 body 的 form 表单也能传参数，
     * 这 2 种参数都是使用 @RequestParam 进行获取
     *
     * 网址: http://localhost:8080/api/postWithQuery?hash=1234
     * 参数: name
     */
    @PostMapping("/api/postWithQuery")
    @ResponseBody
    public Result postWithQuery(@RequestParam String hash, @RequestParam String name) {
        return Result.ok(hash + " -> " + name);
    }

    // @PostMapping("/api/login")
    // @ResponseBody
    // public String post(@RequestParam String username, @RequestParam String password) {
    //     return "Username: " + username + ", Password: " + password;
    // }

    @GetMapping("/api/login")
    @ResponseBody
    public String get(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        System.out.println(request.getContentType());
        return "Username: " + username + ", Password: " + password;
    }


    @PostMapping("/api/login")
    @ResponseBody
    public String put(@RequestBody Map map) {
        return map.toString();
    }

    @PutMapping("/api/login2")
    @ResponseBody
    public String put2(@RequestParam String username, @RequestParam String password, HttpServletRequest request) {
        System.out.println(request.getContentType());
        return "Username: " + username + ", Password: " + password;
    }

    @PutMapping("/api/login3")
    @ResponseBody
    public String put3(HttpServletRequest request) {
        System.out.println(request.getContentType());
        return "ok";
    }
}