package com.xtuer.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "index page";
    }

    @RequestMapping(value = {"/hello"}, method = RequestMethod.GET)
    public String welcomePage(ModelMap model) {
        model.addAttribute("title", "Spring Security Hello World");
        model.addAttribute("message", "This is welcome page!");

        return "hello.htm";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(ModelMap model) {
        model.addAttribute("title", "Spring Security Hello World");
        model.addAttribute("message", "This is protected page!");

        System.out.println("--------------------");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("username", userDetails.getUsername());

        return "admin.htm";
    }
}
