package com.xtuer.security;

import com.xtuer.bean.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 使用 token 进行身份验证的过滤器。
 * 如果 request header 中有 auth-token，使用 auth-token 的值查询对应的登陆用户，如果用户有效则放行访问，否则返回 401 错误。
 */
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static ThreadLocal<Boolean> allowSessionCreation = new ThreadLocal<>(); // 是否允许当前请求创建 session

    public TokenAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST")); // 参考 UsernamePasswordAuthenticationFilter
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String token = request.getHeader("auth-token");

        // 模拟 token 无效返回 null
        if (!"123".equals(token)) {
            return null;
        }

        // 使用 token 信息查找缓存中的登陆用户信息，下面为了测试方便直接写死一个
        User user = new User("admin", "不重要", "ROLE_ADMIN");

        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        Authentication auth = null;

        allowSessionCreation.set(true);

        // 如果 header 里有 auth-token 时，则使用 token 查询用户数据进行登陆验证
        if (request.getHeader("auth-token") != null) {
            // 1. 尝试进行身份认证
            // 2. 如果用户无效，则返回 401
            // 3. 如果用户有效，则保存到 SecurityContext 中，供本次方式后续使用
            auth = attemptAuthentication(request, response);

            if (auth == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 无效，请重新申请 token");
                return;
            }

            // 保存认证信息到 SecurityContext，禁止 sessionRepository 创建 session
            allowSessionCreation.set(false);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 继续调用下一个 filter: UsernamePasswordAuthenticationToken
        chain.doFilter(request, response);
    }

    public static boolean isAllowSessionCreation() {
        return allowSessionCreation.get();
    }
}
