package com.xtuer.controller;

import com.xtuer.bean.MyUserDetails;
import com.xtuer.security.SecurityHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Controller
public class HelloController {
    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "index page";
    }

    @RequestMapping(value = {"/hello"}, method = RequestMethod.GET)
    public String welcomePage(ModelMap model, HttpServletRequest request) {
        model.addAttribute("title", "Spring Security Hello World");
        model.addAttribute("message", "This is welcome page!");

        System.out.println(request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"));

        Enumeration en = request.getSession().getAttributeNames();

        System.out.println("+++++++++++++++++++++");
        while (en.hasMoreElements()) {
            System.out.println(en.nextElement() + "\n-------\n");
        }

        return "hello.htm";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(ModelMap model) {
        model.addAttribute("title", "Spring Security Hello World");
        model.addAttribute("message", "This is protected page!");

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("username", userDetails.getUsername());

        return "admin.htm";
    }

    @RequestMapping("/loginUser")
    @ResponseBody
    public Object loginUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (SecurityHelper.isLogin()) {
            MyUserDetails userDetails = (MyUserDetails) principle;
            return userDetails;
        }

        return principle;
    }
}
