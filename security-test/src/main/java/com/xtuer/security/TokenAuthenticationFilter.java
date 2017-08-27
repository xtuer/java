package com.xtuer.security;

import com.xtuer.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppAuthenticationToken extends AbstractAuthenticationProcessingFilter {
    @Autowired
    private HttpSessionSecurityContextRepository sessionRepository;

    public AppAuthenticationToken() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // 使用 auth-token 信息查找缓存中的用户信息，下面为了测试方便直接写死一个
        User user = new User("admin", "", "ROLE_USER");
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        Authentication authResult = null;

        // 如果 header 里有 auth-token 时，则使用 token 查询用户数据进行登陆验证
        if (request.getHeader("auth-token") != null) {
            sessionRepository.setAllowSessionCreation(false); // 禁止 sessionRepository 创建 session
            authResult = attemptAuthentication(request, response); // 使用 token 查询用户数据进行登陆验证

            // token 对应的用户不存在则返回错误
            if (authResult == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "登陆后才能访问");
                return;
            }

            // 保存登陆信息到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authResult);
        }

        // 继续调用下一个 filter: UsernamePasswordAuthenticationToken
        chain.doFilter(request, response);

        // 不是使用 token 登陆验证的，则恢复允许 sessionRepository 创建 session
        if (authResult == null) {
            sessionRepository.setAllowSessionCreation(true);
        }
    }
}
