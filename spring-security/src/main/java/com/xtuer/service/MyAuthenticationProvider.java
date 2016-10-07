package com.xtuer.service;

import com.xtuer.bean.MyUserDetails;
import com.xtuer.bean.User;
import com.xtuer.dao.UserDao;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyAuthenticationProvider implements AuthenticationProvider {
    private UserDao userDao = new UserDao();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        UserDetails userDetails = loadUserByUsername(token.getName());

        if(userDetails == null) {
            throw new UsernameNotFoundException("用户名/密码无效");
        } else if (!userDetails.isEnabled()){
            throw new DisabledException("用户已被禁用");
        } else if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("账号已过期");
        } else if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("账号已被锁定");
        } else if (!userDetails.isCredentialsNonExpired()) {
            throw new LockedException("凭证已过期");
        }

        String password = userDetails.getPassword(); // 数据库用户的密码，一般都是加密过的，可以使用 BCrypt

        // 与 authentication 里面的 credentials 相比较
        // token.getCredentials(): 未加密的密码
        // TODO: 如果是第三方账号，则跳过这一步，否则比较输入的密码和数据库加密后的秘密是否相等
        if(!password.equals(token.getCredentials())) {
            //throw new BadCredentialsException("用户名/密码无效");
        }

        // 授权
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private UserDetails loadUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        User user = userDao.findUserByUsername(username);
        return new MyUserDetails(user);
    }
}
