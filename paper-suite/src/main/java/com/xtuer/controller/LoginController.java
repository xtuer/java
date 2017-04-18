package com.xtuer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping(value=UriView.URI_LOGIN)
    public String loginPage(@RequestParam(value="error", required=false) String error,
                            @RequestParam(value="logout", required=false) String logout,
                            ModelMap model) {
        String status = "";
        status = (error != null)  ? "Username or password is not correct" : status; // 登录错误
        status = (logout != null) ? "Logout successful" : status; // 注销成功
        model.put("status", status);

        return UriView.VIEW_LOGIN;
    }

    @GetMapping(UriView.URI_DENY)
    @ResponseBody
    public String toDenyPage() {
        return "权限不够!";
    }


}
