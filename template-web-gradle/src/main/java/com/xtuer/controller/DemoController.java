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

import java.util.Date;
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

    // URL: http://localhost:8080/demo
    @RequestMapping(UriView.URI_DEMO)
    public String toHelloPage(ModelMap map) {
        map.put("action", "access demo page");
        return UriView.VIEW_DEMO;
    }

    /**
     * 访问数据库
     * URL: http://localhost:8080/demo/{id}
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
    //

    /**
     * REST 读取
     * URL: http://localhost:8080/rest/{id}
     *
     * @param id
     * @param name
     * @param map
     * @return
     */
    @GetMapping("/rest/{id}")
    @ResponseBody
    public Result handleGet(@PathVariable int id, @RequestParam String name, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("name", name);
        return Result.ok("GET handled", map);
    }

    /**
     * REST 的读取
     * URL: http://localhost:8080/rest
     *
     * @param name
     * @return
     */
    @GetMapping("/rest")
    @ResponseBody
    public Result handleGet(@RequestParam String name) {
        return Result.ok("GET handled", name);
    }

    /**
     * REST 的更新
     * URL: http://localhost:8080/rest
     *
     * @param map
     * @return
     */
    @PutMapping("/rest")
    @ResponseBody
    public Result handlePut(@RequestBody Map map) {
        return new Result(true, "UPDATE handled", map);
    }

    /**
     * REST 创建
     * URL: http://localhost:8080/rest
     *
     * @return
     */
    @PostMapping("/rest")
    @ResponseBody
    public Result handlePost() {
        return new Result(true, "CREATE handled");
    }

    /**
     * REST 删除
     * URL: http://localhost:8080/rest
     *
     * @return
     */
    @DeleteMapping("/rest")
    @ResponseBody
    public Result handleDelete() {
        return new Result(true, "DELETE handled");
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //                                      访问时发生异常                                //
    //////////////////////////////////////////////////////////////////////////////////////
    // http://localhost:8080/
    @GetMapping("/exception")
    public String exception() {
        throw new RuntimeException("普通访问发生异常");
    }

    // http://localhost:8080/
    @GetMapping("/exception-ajax")
    @ResponseBody
    public Result exceptionWhenAjax() {
        throw new RuntimeException("AJAX 访问发生异常");
    }

    /**
     * 字符串日期转换为日期 Date 对象
     * URL: http://localhost:8080/to-date?date=2017-03-12
     *      http://localhost:8080/to-date?date=2017-03-12%2012:10:15
     * @param date
     * @return
     */
    @GetMapping("/to-date")
    @ResponseBody
    public Result<Date> toDate(@RequestParam("date") Date date) {
        return Result.ok("日期转换", date);
    }
}
