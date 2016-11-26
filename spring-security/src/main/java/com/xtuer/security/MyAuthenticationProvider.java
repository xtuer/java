package com.xtuer.security;

import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

public class MyAuthenticationProvider implements AuthenticationProvider {
    @Resource(name="userDetailsService")
    private MyUserDetailsService userDetailsService;

    @Resource(name="passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getName());;

        if(userDetails == null) {
            throw new UsernameNotFoundException("用户不存在");
        } else if (!userDetails.isEnabled()){
            throw new DisabledException("用户已被禁用");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("账号已过期");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("账号已被锁定");
        } else if (!userDetails.isCredentialsNonExpired()) {
            throw new LockedException("凭证已过期");
        }

        // 通过 OAuth 登陆过来的，不需要验证密码
        // 想验证密码也不行啊，例如 OAuth 用户绑定已有用户的情况是不知道原始密码的，已经加密过了
        if (!isOAuthUser(userDetails)) {
            String encryptedPassword = userDetails.getPassword(); // 数据库用户的密码，一般都是加密过的
            String inputPassword = (String) token.getCredentials(); // 用户输入的密码

            if(!passwordEncoder.matches(inputPassword, encryptedPassword)) {
                throw new BadCredentialsException("用户名/密码无效");
            }
        }

        // 成功登陆，userDetails 作为 principal 的好处是可以放自定义的 UserDetails，这样可以存储更多有用的信息，而不只是 username
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private boolean isOAuthUser(UserDetails userDetails) {
        // 以 QQ_ 开头的用户名，说明是 OAuth 登陆的用户
        // 实际中应该是去映射 OAuth 用户和本地用户关系表中查询是否 OAuth 用户
        return userDetails.getUsername().startsWith("QQ_");
    }
}