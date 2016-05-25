package com.xtuer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CommonController {
    private static Logger logger = LoggerFactory.getLogger(CommonController.class.getName());

    @RequestMapping("/")
    public String index() {
        return UriViewConstants.REDIRECT + UriViewConstants.URI_ADMIN_TOPICS;
    }
}
