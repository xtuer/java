package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
}
