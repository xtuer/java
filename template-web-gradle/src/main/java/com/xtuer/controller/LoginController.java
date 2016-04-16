package com.xtuer.controller;

import com.xtuer.util.SecurityUtil;
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
     *    登录页面: /login
     *    登录错误: /login?error=1
     *    注销成功: /login?logout=1
     *
     * @param error
     * @param logout
     * @param model
     * @return
     */
    @RequestMapping(value = UriConstants.URI_LOGIN, method = RequestMethod.GET)
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            ModelMap model) {
        String status = "";
        status = (error == null)  ? status : "login-error";         // 登录错误
        status = (logout == null) ? status : "logout-success";      // 注销成功
        status = SecurityUtil.isLogin() ? "login-success" : status; // 登陆状态
        model.addAttribute("status", status);

        return UriConstants.VIEW_LOGIN;
    }

    @RequestMapping(UriConstants.URI_DENY)
    @ResponseBody
    public String denyPage() {
        return "权限不够!";
    }


}
