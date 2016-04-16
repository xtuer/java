package com.xtuer.controller;

import com.xtuer.dao.DemoDao;
import com.xtuer.bean.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class DemoController {
    private static Logger logger = LoggerFactory.getLogger(DemoController.class.getName());

    @Autowired
    private DemoDao demoDao; // 在 dao.xml 里配置

    @RequestMapping(UriConstants.URI_DEMO)
    public String toHello(ModelMap map) {
        map.put("action", "Access demo page");

        return UriConstants.VIEW_DEMO;
    }

    @RequestMapping(UriConstants.URI_DEMO_MYBATIS)
    @ResponseBody
    public Demo queryDemoFromDatabase(@PathVariable int id) {
        Demo demo = demoDao.findDemoById(id);
        return demo;
    }

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "Index page";
    }
}
