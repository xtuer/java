package com.xtuer.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {
    /**
     * 处理登录，登录错误，注销，对应的 URL 为:
     *    登录:    /login
     *    登录错误: /login?error
     *    注销:    /login?logout
     *
     * @param error
     * @param logout
     * @param model
     * @return
     */
    @RequestMapping(value = UriConstants.URI_LOGIN, method = RequestMethod.GET)
    public String handleLogin(@RequestParam(required = false) String error,
                              @RequestParam(required = false) String logout,
                              ModelMap model) {
        String status = "";
        String url = UriConstants.URI_SPRING_SECURITY_LOGIN;

        if (error != null) {
            status = "login-error"; // 登录错误
        } else if (logout != null) {
            status = "logout-success"; // 注销成功
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            if (!"anonymousUser".equals(username)) {
                // 已经登录时, 显示为注销按钮与注销的连接
                status = "login-success";
                url = UriConstants.URI_SPRING_SECURITY_LOGOUT;
            }
        }

        model.addAttribute("url", url);
        model.addAttribute("status", status);

        return UriConstants.VIEW_LOGIN;
    }

    @RequestMapping(UriConstants.URI_DENY)
    @ResponseBody
    public String handleDeny() {
        return "权限不够!";
    }
}
