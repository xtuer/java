package com.xtuer.controller;

import com.xtuer.bean.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class RestController {
    @GetMapping("/rest/{id}")
    @ResponseBody
    public Result handleGet(@PathVariable int id, @RequestParam String name, ModelMap map) {
        map.addAttribute("id", id);
        map.addAttribute("name", name);

        return new Result(true, "GET handled", map);
    }

    @GetMapping("/rest")
    @ResponseBody
    public Result handleGet(@RequestParam String name) {
        return new Result(true, "GET handled", name);
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
}
