package com.xtuer.controller;

import com.xtuer.bean.User;
import com.xtuer.security.TokenService;
import com.xtuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 处理登录，登录错误，注销，对应的 URL 为:
     *     登录页面: /login
     *     登录错误: /login?error=1
     *     注销成功: /login?logout=1
     *
     * @param error
     * @param logout
     * @param model
     * @return
     */
    @GetMapping(value= Urls.PAGE_LOGIN)
    public String loginPage(@RequestParam(value="error",  required=false) String error,
                            @RequestParam(value="logout", required=false) String logout,
                            ModelMap model) {
        String status = "";
        status = (error != null)  ? "Username or password is not correct" : status; // 登录错误
        status = (logout != null) ? "Logout successful" : status; // 注销成功
        model.put("status", status);

        return Urls.FILE_LOGIN;
    }

    /**
     * 权限不够时访问此方法
     *
     * @return
     */
    @GetMapping(Urls.PAGE_DENY)
    @ResponseBody
    public String toDenyPage() {
        return "权限不够!";
    }

    /**
     * 使用用户名和密码创建 token
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping(Urls.API_LOGIN_TOKENS)
    @ResponseBody
    public String loginToken(@RequestParam String username, @RequestParam String password) {
        User user = userService.findUserByUsernamePassword(username, password);
        String token = tokenService.generateToken(user);

        return token;
    }

    @GetMapping("/page/bindUser")
    @ResponseBody
    public String bindUser(HttpServletRequest request, HttpServletResponse response) {
        // 1. 绑定用户
        // TODO

        // 2. 绑定用户成功后使用 savedRequest 重定向到登陆前的页面，这里只是为了展示怎么取到登陆前页面的 URL
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";

        return redirectUrl;
    }
}
