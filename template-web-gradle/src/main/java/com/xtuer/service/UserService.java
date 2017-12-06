package com.xtuer.service;

import com.xtuer.bean.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User findUserByUsername(String username) {
        if ("admin".equals(username)) {
            // Spring Security 5 使用密码的前缀决定使用哪个 Password Encoder，实现同时支持多种加密方式
            // {noop}表示不加密密码，{bcrypt} 使用 bcrypt 加密
            return new User("admin", "{noop}admin", "ROLE_ADMIN");
        } else if ("alice".equals(username)) {
            return new User("alice", "{noop}alice", "ROLE_USER");
        }

        return null;
    }

    public User findUserByUsernamePassword(String username, String password) {
        return findUserByUsername(username); // 测试使用
    }
}
