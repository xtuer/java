package com.xtuer.controller;

import com.xtuer.bean.MyUserDetails;
import com.xtuer.bean.User;
import com.xtuer.dao.UserDao;
import com.xtuer.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Enumeration;

@Controller
public class HelloController {
    private UserDao userDao = new UserDao();

    @Resource(name="authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBasedRememberMeServices tokenBasedRememberMeServices;

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

    @RequestMapping("/login-user")
    @ResponseBody
    public Object loginUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (SecurityUtils.isLogin()) {
            MyUserDetails userDetails = (MyUserDetails) principle;
            return userDetails;
        }

        return principle;
    }

    /**
     * 绑定用户
     *
     * @return
     */
    @GetMapping("/bindingUser")
    public String bindingUser(HttpServletRequest request, HttpServletResponse response) {
        // [[1]] 绑定已有用户
        // [[2]] 绑定好后进行自动登陆
        String username = "admin";
        String password = "Passw0rd1"; // 原始密码，不是加密后的

        return "redirect:" + autoLogin(request, response, username, password);
    }

    /**
     * 自动登陆
     *
     * @param request
     * @param response
     * @return 登陆后需要访问的页面的 URL
     */
    public String autoLogin(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        String defaultTargetUrl = "/"; // 默认登陆成功的页面
        String redirectUrl = "/login?error=1"; // 默认为登陆错误页面

        try {
            Authentication token = new UsernamePasswordAuthenticationToken(username, password);
            token = authenticationManager.authenticate(token); // 登陆
            SecurityContextHolder.getContext().setAuthentication(token);
            tokenBasedRememberMeServices.onLoginSuccess(request, response, token); // 使用 remember me

            // 重定向到登陆前的页面
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
            redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : defaultTargetUrl;
        } catch (BadCredentialsException ex) {
            ex.printStackTrace();
        }

        return redirectUrl;
    }
}
