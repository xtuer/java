package com.xtuer.controller;

import com.xtuer.bean.Demo;
import com.xtuer.bean.Result;
import com.xtuer.mapper.DemoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class DemoController {
    private static Logger logger = LoggerFactory.getLogger(DemoController.class.getName());

    @Autowired
    private DemoMapper demoMapper;

    @RequestMapping("/")
    public String index() {
        return "首页";
    }

    @RequestMapping(UriView.URI_DEMO)
    public String toHelloPage(ModelMap map) {
        map.put("action", "access demo page");
        return UriView.VIEW_DEMO;
    }

    /**
     * 访问数据库
     *
     * @param id
     * @return
     */
    @RequestMapping(UriView.URI_DEMO_MYBATIS)
    @ResponseBody
    public Result<Demo> queryDemoFromDatabase(@PathVariable int id) {
        return Result.ok("", demoMapper.findDemoById(id));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //                                         REST 用法                                //
    //////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/rest/{id}")
    @ResponseBody
    public Result handleGet(@PathVariable int id, @RequestParam String name, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("name", name);
        return Result.ok("GET handled", map);
    }

    @GetMapping("/rest")
    @ResponseBody
    public Result handleGet(@RequestParam String name) {
        return Result.ok("GET handled", name);
    }

    @PutMapping("/rest")
    @ResponseBody
    public Result handlePut(@RequestBody Map map) {
        return new Result(true, "UPDATE handled", map);
    }

    @PostMapping("/rest")
    @ResponseBody
    public Result handlePost() {
        return new Result(true, "CREATE handled");
    }

    @DeleteMapping("/rest")
    @ResponseBody
    public Result handleDelete() {
        return new Result(true, "DELETE handled");
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //                                      访问时发生异常                                //
    //////////////////////////////////////////////////////////////////////////////////////
    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("普通访问发生异常");
    }

    @GetMapping("/exception-ajax")
    @ResponseBody
    public Result exceptionWhenAjax() {
        throw new RuntimeException("AJAX 访问发生异常");
    }
}
