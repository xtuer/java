package com.xtuer.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 用户登录成功后访问不同的页面:
        // 1. 获取用户登录前访问的 URL
        // 2. 如果 url 不为空，访问登录前的页面
        // 3. 如果 url 为空，登录后根据用户的角色访问对应的页面
        SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
        String redirectUrl = (savedRequest == null) ? "" : savedRequest.getRedirectUrl();

        if (!redirectUrl.isEmpty()) {
            response.sendRedirect(redirectUrl);
            return;
        }

        // 获取登录用户信息
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority(); // 用户的权限

            // 不同的用户访问不同的页面
            if ("ROLE_ADMIN".equals(role)) {
                redirectUrl = "/admin";
            } else {
                redirectUrl = "/hello";
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
