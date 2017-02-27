package com.xtuer.security;

import com.xtuer.bean.MyUserDetails;
import com.xtuer.dao.UserDao;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MyUserDetailsService implements UserDetailsService {
    private UserDao userDao = new UserDao();

    /**
     * 使用 username 加载用户的信息，如密码，权限等
     *
     * @param username 登陆表单中用户输入的用户名
     * @return
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        com.xtuer.bean.User user = userDao.findUserByUsername(username);

        if (user == null) {
            System.out.println(username + " not found!");
        }

        return user == null ? null : new MyUserDetails(user);
    }
}
