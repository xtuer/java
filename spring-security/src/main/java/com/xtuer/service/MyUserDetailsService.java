package com.xtuer.service;

import com.xtuer.dao.UserDao;
import com.xtuer.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyUserDetailsService implements UserDetailsService {
    private UserDao userDao = new UserDao();

    /**
     * 使用 username 加载用户的信息，如密码，权限等
     * @param username 登陆表单中用户输入的用户名
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.xtuer.domain.User user = userDao.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username + " not found!");
        }

        return buildUserDetails(user);
    }

    /**
     * Converts User user to org.springframework.security.core.userdetails.User
     * @param user
     * @return
     */
    private User buildUserDetails(com.xtuer.domain.User user) {
        List<GrantedAuthority> authorities = buildUserAuthorities(user.getUserRoles());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }

    /**
     * 把用户的权限 UserRole 转换成 GrantedAuthority
     * @param userRoles 用户的权限
     * @return
     */
    private List<GrantedAuthority> buildUserAuthorities(Set<UserRole> userRoles) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return new ArrayList<GrantedAuthority>(authorities);
    }
}
