package com.xtuer.controller;

import com.xtuer.security.SecurityHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class AutoLoginController {
    @Resource(name="securityHelper")
    private SecurityHelper securityHelper;

    /**
     * OAuth 用户绑定本地用户
     * 然后自动登陆
     */
    @GetMapping("/bindingUser")
    public String bindingUser() {
        // [[1]] 绑定已有用户
        // [[2]] 绑定好后进行自动登陆
        String username = "QQ_admin";
        String password = "wrong";

        return "redirect:" + securityHelper.login(username, password);
    }

    /**
     * 不存在的用户登陆
     */
    @GetMapping("/nonExistingUserLogin")
    public String nonExistingUserLogin() {
        String username = "nonExistingUser";
        String password = "flash";

        return "redirect:" + securityHelper.login(username, password);
    }

    /**
     * admin 登陆
     */
    @GetMapping("/adminLogin")
    public String adminLogin() {
        String username = "admin";
        String password = "Passw0rd";

        return "redirect:" + securityHelper.login(username, password);
    }
}
