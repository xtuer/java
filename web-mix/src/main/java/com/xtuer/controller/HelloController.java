package com.xtuer.controller;

import com.xtuer.bean.Foo;
import com.xtuer.mapper.FooMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

@Controller
public class HelloController {
    private static Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private FooMapper fooMapper;

    public HelloController() {
        System.out.println("=====> HelloController");
    }
    static int count = 0;

    @GetMapping("/foo")
    @ResponseBody
    public Foo findFoo() {
        logger.debug("findFoo()");

        return fooMapper.findFooByNameAndCity("Biao", "Beijin");
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
        ++count;
        System.out.println(count);
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

    // URL: /request?token=11242312341234
    @GetMapping("/request")
    @ResponseBody
    public String testRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getParameter("token");

        return token;
    }

    // URL: /signIn?id=1&name=xxx
    @GetMapping("/signIn")
    @ResponseBody
    public String singInGet(@RequestParam String id,
                         @RequestParam String name,
                         @RequestHeader(value="token", required=false) String token) throws Exception {
        System.out.println("Original name: " + name);
//        name = new String(name.getBytes("iso8859-1"), "UTF-8");
        return String.format("GET: id: %s, name: %s, token: %s", id, name, token);
    }

    // URL: /signIn
    @PostMapping("/signIn")
    @ResponseBody
    public String singInPost(@RequestParam String id,
                         @RequestParam String name,
                         @RequestHeader(value="token", required=false) String token) throws Exception {
        return String.format("POST: id: %s, name: %s, token: %s", id, name, token);
    }
}
