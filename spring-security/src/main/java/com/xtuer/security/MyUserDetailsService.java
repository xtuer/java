package com.xtuer.security;

import com.xtuer.bean.MyUserDetails;
import com.xtuer.dao.UserDao;
import com.xtuer.bean.UserRole;
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
        com.xtuer.bean.User user = userDao.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(username + " not found!");
        }

        return new MyUserDetails(user);
    }
}
