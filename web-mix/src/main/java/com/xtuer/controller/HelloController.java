package com.xtuer.controller;

import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

@Controller
public class HelloController {
    public HelloController() {
        System.out.println("=====> HelloController");
    }

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "Welcome";
    }

    @RequestMapping("/hello")
    public String helloPage(Device device) {
        if (device.isMobile()) {
            System.out.println("Hello mobile user!");
        } else if (device.isTablet()) {
            System.out.println("Hello tablet user!");
        } else {
            System.out.println("Hello desktop user!");
        }

        return "a.html";
    }

    @GetMapping("/device")
    @ResponseBody
    public String detectDevice(Device device) {
        if (device.isMobile()) {
            return "Mobile";
        } else if (device.isTablet()) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    @RequestMapping("/inner")
    @ResponseBody
    public String innerPage() {
        return "{\"success\": true, \"message\": \"No message\"}";
    }

    @RequestMapping("/continue-response")
    public void continueResponse(HttpServletResponse response) {
        try {
            for (int i = 0; i < 10; ++i) {
                Writer writer = response.getWriter();
                writer.write("Hello " + i);
                writer.flush();
                Thread.sleep(100);
            }
        } catch (Exception ex) {

        }
    }

    // /request?token=11242312341234
    @GetMapping("request")
    @ResponseBody
    public String testRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getParameter("token");

        return token;
    }
}
