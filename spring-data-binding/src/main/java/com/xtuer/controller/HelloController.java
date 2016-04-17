package com.xtuer.controller;

import com.xtuer.bean.Admin;
import com.xtuer.bean.User;
import com.xtuer.bean.UserList;
import com.xtuer.bean.UserMap;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HelloController {
    // http://localhost:8080/array?name=Tom&name=Lucy
    @RequestMapping("/array")
    @ResponseBody
    public String[] array(@RequestParam("name") String[] names) {
        return names;
    }

    // http://localhost:8080/list?name=Tom&name=Lucy
    @RequestMapping("/list")
    @ResponseBody
    public List<String> list(@RequestParam("name") List<String> names) {
        return names;
    }

    // http://localhost:8080/object?username=Tom&age=10
    @RequestMapping("/object")
    @ResponseBody
    public User object(User user) {
        return user;
    }

    // http://localhost:8080/nested-object?username=Tom&age=10&address.city=Beijing&address.street=SCI
    @RequestMapping("/nested-object")
    @ResponseBody
    public User nestedObject(User user) {
        return user;
    }

    // http://localhost:8080/intersect-object?user.username=Tom&admin.username=Jim&age=10
    @RequestMapping("/intersect-object")
    @ResponseBody
    public Map intersectObject(User user, Admin admin) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", user);
        map.put("admin", admin);
        return map;
    }

    @InitBinder("user")
    public void initUser(WebDataBinder binder) {
        binder.setFieldDefaultPrefix("user.");
    }

    @InitBinder("admin")
    public void initAdmin(WebDataBinder binder) {
        binder.setFieldDefaultPrefix("admin.");
    }

    // http://localhost:8080/object-list?users[0].username=Tom&users[1].username=Lucy
    @RequestMapping("/object-list")
    @ResponseBody
    public UserList objectList(UserList userList) {
        return userList;
    }

    // http://localhost:8080/object-map?users["x"].username=Tom&users["y"].username=Lucy
    @RequestMapping("/object-map")
    @ResponseBody
    public UserMap objectMap(UserMap users) {
        return users;
    }

    @RequestMapping("/json-view")
    public String jsonView() {
        return "json-view.htm";
    }

    @RequestMapping("/json-bind")
    @ResponseBody
    public User json(@RequestBody User user) {
        System.out.println(user.getUsername());
        return user;
    }
}
